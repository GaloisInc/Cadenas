package com.hashapps.cadenas.domain

import com.hashapps.cadenas.data.ModelRepository
import com.hashapps.cadenas.data.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use-case for language model management.
 *
 * Unifies the language model and messaging profile repositories for the
 * purpose of fetching all downloaded models and appropriately deleting the
 * profiles associated with a given model.
 */
class ManageModelsUseCase(
    private val modelRepository: ModelRepository,
    private val profileRepository: ProfileRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Return all language models downloaded to the device.
     */
    operator fun invoke(): List<String> = modelRepository.downloadedModels()

    /**
     * Delete the given model and all associated messaging profiles, returning an
     * updated list of downloaded models.
     */
    suspend operator fun invoke(model: String): List<String> = withContext(ioDispatcher) {
        profileRepository.deleteProfilesForModel(model)
        modelRepository.deleteFilesForModel(model)
        modelRepository.downloadedModels()
    }
}