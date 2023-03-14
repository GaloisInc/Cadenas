package com.hashapps.butkusapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val BUTKUS_SETTINGS_NAME = "butkus_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BUTKUS_SETTINGS_NAME
)

interface AppContainer {
    val settingsRepository: SettingsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val settingsRepository by lazy {
        SettingsRepository(context.dataStore)
    }
}