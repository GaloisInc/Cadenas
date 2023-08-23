package com.hashapps.cadenas.workers

import android.app.Notification
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.hashapps.cadenas.CadenasApplication
import com.hashapps.cadenas.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ModelDeleteWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = Notification.Builder(context, CadenasApplication.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.delete_title))
            .setSmallIcon(R.drawable.baseline_delete_forever_24)
            .build()

        return ForegroundInfo(0, notification)
    }

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