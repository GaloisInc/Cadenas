package com.hashapps.butkusapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hashapps.butkusapp.data.model.profile.ProfileDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException

private const val BUTKUS_SETTINGS_NAME = "butkus_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BUTKUS_SETTINGS_NAME
)

class ButkusRepository(
    private val context: Context,
    private val profileDao: ProfileDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    externalScope: CoroutineScope,
) {
    private companion object {
        val SELECTED_PROFILE = intPreferencesKey("selected_profile")

        const val TAG = "SettingsRepo"
    }

    val selectedProfile: Flow<Int?> = context.dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading settings.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[SELECTED_PROFILE]
        }

    suspend fun saveSelectedProfile(selectedProfile: Int) {
        context.dataStore.edit {
            it[SELECTED_PROFILE] = selectedProfile
        }
    }

    private var _butkusInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val butkusInitialized: StateFlow<Boolean> = _butkusInitialized.asStateFlow()

    private var _selectedModel: MutableStateFlow<Int?> = MutableStateFlow(null)
    val selectedModel: StateFlow<Int?> = _selectedModel.asStateFlow()

    init {
        externalScope.launch(ioDispatcher) {
            selectedProfile.filterNotNull().collectLatest {
                val profile = profileDao.getProfile(it).first()
                _selectedModel.update { profile.selectedModel }
                // TODO: Use the profile info the initialize Butkus
                Butkus.initialize(context)
                if (Butkus.getInstance() != null) {
                    _butkusInitialized.update { true }
                }
            }
        }
    }

    suspend fun encode(plaintext: String): String? = coroutineScope {
        withContext(defaultDispatcher) {
            Butkus.getInstance()?.encode(plaintext)
        }
    }

    suspend fun decode(encodedMessage: String): String? = coroutineScope {
        withContext(defaultDispatcher) {
            Butkus.getInstance()?.decode(encodedMessage)
        }
    }
}