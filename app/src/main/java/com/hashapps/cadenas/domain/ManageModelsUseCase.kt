package com.hashapps.cadenas.domain

import com.hashapps.cadenas.data.ModelRepository
import com.hashapps.cadenas.data.models.Model
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use-case for language model management.
 *
 * Unifies the language model and messaging channel repositories for the
 * purpose of fetching all downloaded models and appropriately deleting the
 * channels associated with a given model.
 */
class ManageModelsUseCase(
    private val modelRepository: ModelRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Delete the given model and all associated messaging channels.
     */
    suspend operator fun invoke(model: Model): Unit = withContext(ioDispatcher) {
        modelRepository.deleteModel(model)
        modelRepository.deleteFilesForModel(model.name)
    }
}