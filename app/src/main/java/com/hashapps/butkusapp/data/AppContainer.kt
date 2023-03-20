package com.hashapps.butkusapp.data

import android.content.Context
import com.hashapps.butkusapp.data.model.ModelsRepository
import com.hashapps.butkusapp.data.model.OfflineModelsRepository
import com.hashapps.butkusapp.data.profile.OfflineProfilesRepository
import com.hashapps.butkusapp.data.profile.ProfilesRepository
import kotlinx.coroutines.CoroutineScope

interface AppContainer {
    val profilesRepository: ProfilesRepository

    val modelsRepository: ModelsRepository

    val butkusRepository: ButkusRepository
}

class AppDataContainer(
    private val applicationScope: CoroutineScope,
    private val context: Context
) : AppContainer {
    override val profilesRepository by lazy {
        OfflineProfilesRepository(ButkusDatabase.getDatabase(context).profileDao())
    }

    override val modelsRepository by lazy {
        OfflineModelsRepository(ButkusDatabase.getDatabase(context).modelDao())
    }

    override val butkusRepository by lazy {
        ButkusRepository(
            context = context,
            profileDao = ButkusDatabase.getDatabase(context).profileDao(),
            externalScope = applicationScope,
        )
    }
}