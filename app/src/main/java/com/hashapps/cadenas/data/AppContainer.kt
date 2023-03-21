package com.hashapps.cadenas.data

import android.content.Context
import com.hashapps.cadenas.data.model.ModelsRepository
import com.hashapps.cadenas.data.model.OfflineModelsRepository
import com.hashapps.cadenas.data.model.profile.OfflineProfilesRepository
import com.hashapps.cadenas.data.model.profile.ProfilesRepository
import kotlinx.coroutines.CoroutineScope

interface AppContainer {
    val profilesRepository: ProfilesRepository

    val modelsRepository: ModelsRepository

    val cadenasRepository: CadenasRepository
}

class AppDataContainer(
    private val applicationScope: CoroutineScope,
    private val context: Context
) : AppContainer {
    override val profilesRepository by lazy {
        OfflineProfilesRepository(CadenasDatabase.getDatabase(context).profileDao())
    }

    override val modelsRepository by lazy {
        OfflineModelsRepository(CadenasDatabase.getDatabase(context).modelDao())
    }

    override val cadenasRepository by lazy {
        CadenasRepository(
            context = context,
            profileDao = CadenasDatabase.getDatabase(context).profileDao(),
            externalScope = applicationScope,
        )
    }
}