package com.hashapps.cadenas.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

/**
 * Repository class for Cadenas application settings.
 *
 * While there aren't exactly many personalization/customization options for
 * Cadenas, there are a few persistent settings relevant to normal operation.
 * More specifically, we must keep track of:
 *
 * - Whether or not the app's first-time setup has been completed
 * - Which messaging profile is selected
 *
 * These settings are persisted using the [DataStore]<[Preferences]>, a
 * file-based key-value store. On instantiation, this class launches an
 * application-scoped background job that listens for changes to the
 * selected profile, re-initializing Cadenas when a change occurs.
 *
 * @property[runOnce] Boolean preference indicating whether first-time setup
 * has been completed
 * @property[cadenasInitialized] A derived Boolean flag indicating whether or
 * not the Cadenas backend has been initialized or not
 * @property[selectedProfile] The [Profile] selected by the stored integer
 * preference ID
 */
class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val modelsDir: File,
    private val profileDao: ProfileDao,
    externalScope: CoroutineScope,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private companion object {
        val SELECTED_PROFILE = intPreferencesKey("selected_profile")
        val RUN_ONCE = booleanPreferencesKey("run_once")

        const val TAG = "SettingsRepo"
    }

    val runOnce = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading settings.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { it[RUN_ONCE] ?: false }

    /**
     * Indicate the first app run has been completed, so setup screens only
     * show on first launch.
     */
    suspend fun completeFirstRun() {
        dataStore.edit {
            it[RUN_ONCE] = true
        }
    }

    /**
     * Update which messaging profile is selected.
     *
     * @param[selectedProfile] The ID of the messaging profile that's been
     * selected
     */
    suspend fun saveSelectedProfile(selectedProfile: Int) {
        dataStore.edit {
            it[SELECTED_PROFILE] = selectedProfile
        }
    }

    private var _cadenasInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val cadenasInitialized: StateFlow<Boolean> = _cadenasInitialized.asStateFlow()

    private var _selectedProfile: MutableStateFlow<Profile?> = MutableStateFlow(null)
    val selectedProfile: StateFlow<Profile?> = _selectedProfile.asStateFlow()

    init {
        /*
         * Worth exploring if there's a better way to do this. The idea is:
         * For the duration of the application's lifetime, in the background,
         * listen for changes to the selected profile.
         *
         * The questionable thing is the nested use of collectLatest; getting
         * the second/inner flow depends on the first, so a simple combine
         * won't suffice.
         */
        externalScope.launch(ioDispatcher) {
            dataStore.data
                .catch {
                    if (it is IOException) {
                        Log.e(TAG, "Error reading settings.", it)
                        emit(emptyPreferences())
                    } else {
                        throw it
                    }
                }.map { preferences ->
                    preferences[SELECTED_PROFILE]
                }.filterNotNull().collectLatest {
                    profileDao.getProfile(it).collectLatest { profile ->
                        _selectedProfile.update { profile }

                        Cadenas.initialize(
                            CadenasConfig(
                                modelDir = modelsDir.resolve(profile.selectedModel).path,
                                key = profile.key,
                                seed = profile.seed,
                            )
                        )

                        if (Cadenas.getInstance() != null) {
                            _cadenasInitialized.update { true }
                        }
                    }
                }
        }
    }
}