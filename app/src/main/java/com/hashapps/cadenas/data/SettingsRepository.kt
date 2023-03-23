package com.hashapps.cadenas.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.pathString

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val internalStorage: File,
    private val configDao: ConfigDao,
    externalScope: CoroutineScope,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private companion object {
        val SELECTED_PROFILE = intPreferencesKey("selected_profile")

        const val TAG = "SettingsRepo"
    }

    val selectedProfile: Flow<Int?> = dataStore.data
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
        dataStore.edit {
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
                configDao.getConfig(it).collectLatest { savedConfig ->
                    _selectedModel.update { savedConfig.modelId }

                    Cadenas.initialize(CadenasConfig(
                        modelDir = Path(internalStorage.path, savedConfig.modelDir).pathString,
                        key = savedConfig.key,
                        seed = savedConfig.seed,
                    ))

                    if (Cadenas.getInstance() != null) {
                        _cadenasInitialized.update { true }
                    }
                }
            }
        }
    }
}