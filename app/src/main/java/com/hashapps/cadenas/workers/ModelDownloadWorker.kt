package com.hashapps.cadenas.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.io.path.outputStream

class ModelDownloadWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_MODEL_URL) ?: return Result.failure()
        val outDir = inputData.getString(KEY_MODEL_DIR) ?: return Result.failure()

        var downloadSuccessful = false
        val msg = withContext(Dispatchers.IO) {
            try {
                downloadModelTo(url, outDir)
                downloadSuccessful = true
                "Download complete! You can now use $outDir."
            } catch (_: IOException) {
                "An IO error occurred - please try again."
            } catch (_: ZipException) {
                "The model ZIP at $url is invalid."
            }
        }

        val outData = Data.Builder().putString(KEY_RESULT_MSG, msg).build()
        return if (downloadSuccessful) {
            Result.success(outData)
        } else {
            Result.failure(outData)
        }
    }

    private fun downloadModelTo(url: String, outDir: String) {
        ZipInputStream(URL(url).openConnection().inputStream).use { inStream ->
            generateSequence { inStream.nextEntry }
                .filterNot { it.isDirectory }
                .forEach {
                    Path(outDir, it.name).outputStream().use { outStream ->
                        inStream.copyTo(outStream)
                    }
                }
        }
    }

    companion object {
        const val KEY_MODEL_URL = "model_url"
        const val KEY_MODEL_DIR = "model_dir"
        const val KEY_RESULT_MSG = "error_msg"
    }
}