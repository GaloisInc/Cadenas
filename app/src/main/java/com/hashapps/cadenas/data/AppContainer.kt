package com.hashapps.cadenas.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

private const val CADENAS_SETTINGS_NAME = "cadenas_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CADENAS_SETTINGS_NAME
)

interface AppContainer {
    val configRepository: ConfigRepository
    val settingsRepository: SettingsRepository
}

class AppDataContainer(
    private val context: Context
) : AppContainer {
    override val configRepository by lazy {
        ConfigRepository(
            modelDao = ConfigDatabase.getDatabase(context).modelDao(),
            profileDao = ConfigDatabase.getDatabase(context).profileDao(),
        )
    }

    override val settingsRepository by lazy {
        SettingsRepository(
            dataStore = context.dataStore,
            internalStorage = context.filesDir,
            savedConfigDao = ConfigDatabase.getDatabase(context).configDao(),
            externalScope = CoroutineScope(SupervisorJob()),
        )
    }
}