package com.hashapps.cadenas.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hashapps.cadenas.data.profile.ProfileDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException

private const val CADENAS_SETTINGS_NAME = "cadenas_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CADENAS_SETTINGS_NAME
)

class SettingsRepository(
    private val context: Context,
    private val profileDao: ProfileDao,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
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

    private var _cadenasInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val cadenasInitialized: StateFlow<Boolean> = _cadenasInitialized.asStateFlow()

    private var _selectedModel: MutableStateFlow<Int?> = MutableStateFlow(null)
    val selectedModel: StateFlow<Int?> = _selectedModel.asStateFlow()

    init {
        externalScope.launch(ioDispatcher) {
            selectedProfile.filterNotNull().collectLatest {
                val profile = profileDao.getProfile(it).first()
                _selectedModel.update { profile.selectedModel }
                // TODO: Use the profile info the initialize Cadenas
                Cadenas.initialize(context)
                if (Cadenas.getInstance() != null) {
                    _cadenasInitialized.update { true }
                }
            }
        }
    }
}