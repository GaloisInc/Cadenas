package com.hashapps.cadenas.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.pathString

private const val CADENAS_SETTINGS_NAME = "cadenas_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CADENAS_SETTINGS_NAME
)

class SettingsRepository(
    private val context: Context,
    private val configDao: ConfigDao,
    externalScope: CoroutineScope,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
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
                configDao.getConfig(it).collectLatest { savedConfig ->
                    _selectedModel.update { savedConfig.modelId }

                    val config = CadenasConfig(
                        modelDir = Path(context.filesDir.absolutePath, savedConfig.modelDir).pathString,
                        key = savedConfig.key,
                        seed = savedConfig.seed,
                    )

                    Cadenas.initialize(config)

                    if (Cadenas.getInstance() != null) {
                        _cadenasInitialized.update { true }
                    }
                }
            }
        }
    }
}