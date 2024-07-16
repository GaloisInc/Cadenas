package com.hashapps.cadenas.data.models

import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.hashapps.cadenas.workers.ModelDeleteWorker
import com.hashapps.cadenas.workers.ModelDownloadWorker
import com.hashapps.cadenas.workers.ModelInstallWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
class OfflineModelRepository(
    private val modelsDir: File,
    private val workManager: WorkManager,
    private val modelDao: ModelDao,
) : ModelRepository {
    override suspend fun insertModel(model: Model): Long = modelDao.insert(model)
    override suspend fun deleteModel(model: Model) {
        modelDao.delete(model)
        deleteFilesForModel(model.name)
    }
    override suspend fun deleteAllModels() {
        modelDao.deleteAll()
        deleteAllModelFiles()
    }
    override fun getModelStream(name: String): Flow<Model> = modelDao.getModel(name)
    override fun getModelStreamWithHash(hash: String): Flow<Model?> =
        modelDao.getModelWithHash(hash)

    override fun getAllModelsStream(): Flow<List<Model>> = modelDao.getAllModels()

    override val modelDownloaderState = workManager
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
    override fun downloadModelFromAndSaveAs(url: String, modelName: String) {
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

    override val modelInstallerState = workManager
        .getWorkInfosForUniqueWorkLiveData("installModel")
        .asFlow()
        .map { it.getOrNull(0) }

    /**
     * Install a model from the device, and save it to Cadenas with a given
     * name. Work is performed asynchronously such that even application death
     * will not cancel the download.
     *
     * @param[uri] The media store URI of the model ZIP
     * @param[modelName] The name of the model/directory to store the model to
     */
    override fun installModelFromAndSaveAs(uri: Uri, modelName: String) {
        val data = Data.Builder()

        data.putString(ModelInstallWorker.KEY_MODEL_URI, uri.toString())

        val outDir = modelsDir.resolve(modelName)
        data.putString(ModelInstallWorker.KEY_MODEL_DIR, outDir.path)

        val modelInstallRequest = OneTimeWorkRequestBuilder<ModelInstallWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(data.build())
            .build()

        workManager
            .enqueueUniqueWork(
                "installModel",
                ExistingWorkPolicy.KEEP,
                modelInstallRequest,
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

    private fun deleteAllModelFiles() {
        val toDeleteDir = modelsDir
        if (toDeleteDir.exists() && toDeleteDir.isDirectory) {
            toDeleteDir.listFiles()?.forEach { file ->
                deleteFilesForModel(file.name)
            }
        }
    }
}