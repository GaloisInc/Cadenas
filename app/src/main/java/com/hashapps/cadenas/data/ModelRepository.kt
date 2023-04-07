package com.hashapps.cadenas.data

import androidx.lifecycle.asFlow
import androidx.work.*
import com.hashapps.cadenas.workers.ModelDeleteWorker
import com.hashapps.cadenas.workers.ModelDownloadWorker
import kotlinx.coroutines.flow.*
import java.io.File

class ModelRepository(
    private val internalStorage: File,
    private val workManager: WorkManager,
) {
    val modelDownloaderState = workManager
        .getWorkInfosForUniqueWorkLiveData("downloadModel")
        .asFlow()
        .map { it.getOrNull(0) }

    fun downloadModelFromAndSaveAs(url: String, modelName: String) {
        val data = Data.Builder()

        data.putString(ModelDownloadWorker.KEY_MODEL_URL, url)

        val outDir = File(internalStorage, "models/$modelName")
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

    fun deleteFilesForModel(modelName: String) {
        val toDeleteDir = File(internalStorage, "models/$modelName.temp")
        File(internalStorage, "models/$modelName").renameTo(toDeleteDir)


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

    fun downloadedModels() = File(internalStorage, "models")
        .listFiles()
        .orEmpty()
        .filter { it.isDirectory && !it.path.endsWith(".temp") }
        .map { it.name }
}