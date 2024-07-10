package com.hashapps.cadenas.data.models

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

/**
 * Model repository interface.
 *
 * This abstraction exists to enable testing of ViewModels that use models.
 */
interface ModelRepository {
    suspend fun insertModel(model: Model): Long
    suspend fun deleteModel(model: Model)
    suspend fun deleteAllModels()
    fun getModelStream(name: String): Flow<Model>
    fun getModelStreamWithHash(hash: String): Flow<Model?>
    fun getAllModelsStream(): Flow<List<Model>>

    val modelDownloaderState: Flow<WorkInfo?>

    fun downloadModelFromAndSaveAs(url: String, modelName: String)
}