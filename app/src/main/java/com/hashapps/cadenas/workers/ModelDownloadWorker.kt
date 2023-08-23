package com.hashapps.cadenas.workers

import android.app.Notification
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.hashapps.cadenas.CadenasApplication
import com.hashapps.cadenas.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.io.path.outputStream

class ModelDownloadWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = Notification.Builder(context, CadenasApplication.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.download_title))
            .setSmallIcon(R.drawable.baseline_downloading_24)
            .build()

        return ForegroundInfo(0, notification)
    }

    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_MODEL_URL) ?: return Result.failure()
        val outDir = inputData.getString(KEY_MODEL_DIR) ?: return Result.failure()

        var downloadSuccessful = false
        val msg = withContext(Dispatchers.IO) {
            try {
                downloadModelTo(url, outDir)
                downloadSuccessful = true
                "Download complete!"
            } catch (_: IOException) {
                "An IO error occurred - please try again."
            } catch (_: ZipException) {
                "The model ZIP at $url is invalid."
            } catch (_: java.lang.Exception) {
                "An unknown error occurred."
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
        val tempOutDir = File("$outDir.temp").also { it.mkdirs() }

        ZipInputStream(URL(url).openConnection().inputStream).use { inStream ->
            generateSequence { inStream.nextEntry }
                .filterNot { it.isDirectory }
                .forEach {
                    Path(tempOutDir.path, it.name).outputStream().use { outStream ->
                        inStream.copyTo(outStream)
                    }
                }
        }

        File(tempOutDir, URL_FILE).writeText(url)

        tempOutDir.renameTo(File(outDir))
    }

    companion object {
        const val KEY_MODEL_URL = "model_url"
        const val KEY_MODEL_DIR = "model_dir"
        const val KEY_RESULT_MSG = "msg"
        private const val URL_FILE = ".url"
    }
}