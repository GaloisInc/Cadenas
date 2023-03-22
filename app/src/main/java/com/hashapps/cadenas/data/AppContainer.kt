package com.hashapps.cadenas.data

import android.content.Context
import com.hashapps.cadenas.data.model.ModelsRepository
import com.hashapps.cadenas.data.model.OfflineModelsRepository
import com.hashapps.cadenas.data.profile.OfflineProfilesRepository
import com.hashapps.cadenas.data.profile.ProfilesRepository
import kotlinx.coroutines.CoroutineScope

interface AppContainer {
    val profilesRepository: ProfilesRepository

    val modelsRepository: ModelsRepository

    val settingsRepository: SettingsRepository
}

class AppDataContainer(
    private val applicationScope: CoroutineScope,
    private val context: Context
) : AppContainer {
    override val profilesRepository by lazy {
        OfflineProfilesRepository(ConfigDatabase.getDatabase(context).profileDao())
    }

    override val modelsRepository by lazy {
        OfflineModelsRepository(ConfigDatabase.getDatabase(context).modelDao())
    }

    override val settingsRepository by lazy {
        SettingsRepository(
            context = context,
            profileDao = ConfigDatabase.getDatabase(context).profileDao(),
            externalScope = applicationScope,
        )
    }
}