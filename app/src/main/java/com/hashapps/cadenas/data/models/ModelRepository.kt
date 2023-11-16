package com.hashapps.cadenas.data.models

import androidx.lifecycle.asFlow
import androidx.work.*
import com.hashapps.cadenas.workers.ModelDeleteWorker
import com.hashapps.cadenas.workers.ModelDownloadWorker
import kotlinx.coroutines.flow.*
import java.io.File

/**
 * Repository class for downloaded language models.
 *
 * Provides the means to fetch and delete language models, as well as a list of
 * all models which have been previously downloaded.
 *
 * Rather than use another database table and duplicate the information, we
 * rely on the device filesystem itself to act as the repository of language
 * model truth.
 *
 * @property[modelDownloaderState] The state of the worker responsible for
 * downloading models from the Internet
 */
class ModelRepository(
    private val modelsDir: File,
    private val workManager: WorkManager,
    private val modelDao: ModelDao,
) {
    suspend fun insertModel(model: Model): Long = modelDao.insert(model)
    suspend fun deleteModel(model: Model) {
        modelDao.delete(model)
        deleteFilesForModel(model.name)
    }

    fun getModelStream(name: String): Flow<Model> = modelDao.getModel(name)
    fun getAllModelsStream(): Flow<List<Model>> = modelDao.getAllModels()

    val modelDownloaderState = workManager
        .getWorkInfosForUniqueWorkLiveData("downloadModel")
        .asFlow()
        .map { it.getOrNull(0) }

    /**
     * Download a model from the Internet, and save it to disk with a given
     * name. Work is performed asynchronously such that even application death
     * will not cancel the download.
     *
     * @param[url] The HTTPS URL of the model ZIP
     * @param[modelName] The name of the model/directory to store the model to
     */
    fun downloadModelFromAndSaveAs(url: String, modelName: String) {
        val data = Data.Builder()

        data.putString(ModelDownloadWorker.KEY_MODEL_URL, url)

        val outDir = modelsDir.resolve(modelName)
        data.putString(ModelDownloadWorker.KEY_MODEL_DIR, outDir.path)

        val modelDownloadRequest = OneTimeWorkRequestBuilder<ModelDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(data.build())
            .build()

        workManager
            .enqueueUniqueWork(
                "downloadModel",
                ExistingWorkPolicy.KEEP,
                modelDownloadRequest,
            )
    }

    private fun deleteFilesForModel(modelName: String) {
        val toDeleteDir = modelsDir.resolve("$modelName.temp")
        modelsDir.resolve(modelName).renameTo(toDeleteDir)

        val modelDeleteWOrkRequest = OneTimeWorkRequestBuilder<ModelDeleteWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                Data.Builder().putString(
                    ModelDeleteWorker.KEY_MODEL_DIR,
                    toDeleteDir.path
                ).build()
            )
            .build()

        workManager.enqueue(modelDeleteWOrkRequest)
    }
}