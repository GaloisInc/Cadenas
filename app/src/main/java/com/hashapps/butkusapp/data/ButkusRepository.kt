package com.hashapps.butkusapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hashapps.butkusapp.data.profile.Profile
import com.hashapps.butkusapp.data.profile.ProfileDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException

private const val BUTKUS_SETTINGS_NAME = "butkus_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BUTKUS_SETTINGS_NAME
)

class ButkusRepository(
    private val context: Context,
    private val externalScope: CoroutineScope,
    private val profileDao: ProfileDao,
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

    init {
        externalScope.launch(Dispatchers.IO) {
            selectedProfile.filterNotNull().collectLatest {
                val profile = profileDao.getProfile(it).first()
                // TODO: Use the profile info the initialize Butkus
                Butkus.initialize(context)
                if (Butkus.getInstance() != null) {
                    _butkusInitialized.update { true }
                }
            }
        }
    }

    suspend fun encode(plaintext: String): String? = coroutineScope {
        val encodedMessage = async(Dispatchers.Default) { Butkus.getInstance()?.encode(plaintext) }
        encodedMessage.await()
    }

    suspend fun decode(encodedMessage: String): String? = coroutineScope {
        val plaintext = async(Dispatchers.Default) { Butkus.getInstance()?.decode(encodedMessage) }
        plaintext.await()
    }
}