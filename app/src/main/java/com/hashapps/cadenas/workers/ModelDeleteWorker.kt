package com.hashapps.cadenas.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ModelDeleteWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val modelDir = inputData.getString(KEY_MODEL_DIR) ?: return Result.failure()

        return withContext(Dispatchers.IO) {
            if (File(modelDir).deleteRecursively()) {
                Result.success()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_MODEL_DIR = "model_dir"
    }
}