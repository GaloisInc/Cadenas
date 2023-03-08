package com.hashapps.butkusapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hashapps.butkusapp.data.SettingsRepository

private const val BUTKUS_SETTINGS_NAME = "butkus_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BUTKUS_SETTINGS_NAME
)

class ButkusApplication : Application() {
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(dataStore)
    }
}