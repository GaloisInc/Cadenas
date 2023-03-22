package com.hashapps.cadenas.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope

interface AppContainer {
    val configRepository: ConfigRepository
    val settingsRepository: SettingsRepository
}

class AppDataContainer(
    private val applicationScope: CoroutineScope,
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
            context = context,
            profileDao = ConfigDatabase.getDatabase(context).profileDao(),
            externalScope = applicationScope,
        )
    }
}