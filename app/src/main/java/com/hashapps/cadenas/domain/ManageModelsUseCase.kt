package com.hashapps.cadenas.domain

import com.hashapps.cadenas.data.ModelRepository
import com.hashapps.cadenas.data.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ManageModelsUseCase(
    private val modelRepository: ModelRepository,
    private val profileRepository: ProfileRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(): List<String> = modelRepository.downloadedModels()

    suspend operator fun invoke(model: String): List<String> = withContext(ioDispatcher) {
        profileRepository.deleteProfilesForModel(model)
        modelRepository.deleteFilesForModel(model)
        modelRepository.downloadedModels()
    }
}