package com.hashapps.cadenas.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hashapps.cadenas.CadenasApplication
import com.hashapps.cadenas.R
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import javax.net.ssl.HttpsURLConnection
import kotlin.io.path.Path
import kotlin.io.path.outputStream

class ModelDownloadWorker(
    private val context: Context,
    params: WorkerParameters,
) : Worker(context, params) {
    override fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, CadenasApplication.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.download_title))
            .setSmallIcon(R.drawable.baseline_downloading_24)
            .build()

        return ForegroundInfo(0, notification)
    }

    override fun doWork(): Result {
        val url = inputData.getString(KEY_MODEL_URL) ?: return Result.failure()
        val outDir = inputData.getString(KEY_MODEL_DIR) ?: return Result.failure()

        return downloadModelTo(url, outDir)
    }

    private fun downloadModelTo(url: String, outDir: String): Result {
        val tempOutDir = File("$outDir.temp").also { it.mkdirs() }

        // TODO: We need to be smarter about some of these string literals for localization!
        // TODO: Maybe we use some internal string constants to later choose the UI text?

        val urlConnection = try {
            URL(url).openConnection()
        } catch (_: IOException) {
            return fail("Something went wrong preparing a connection to $url. Please try again.")
        }

        // Sanity check: The UI should catch any non-HTTPS inputs.
        require(urlConnection is HttpsURLConnection)

        try {
            urlConnection.connect()
        } catch (_: SocketTimeoutException) {
            return fail("The connection to $url timed out. Please try again.")
        } catch (_: IOException) {
            return fail("Something went wrong connecting to $url. Please try again.")
        }

        if (urlConnection.contentType != ZIP_MIME) {
            return fail("$url does not refer to a ZIP. Please try again.")
        }

        val zipStream = try {
            urlConnection.inputStream
        } catch (_: IOException) {
            return fail("Something went wrong creating an input stream for $url. Please try again.")
        }
        try {
            ZipInputStream(zipStream).use { inStream ->
                generateSequence { inStream.nextEntry }
                    .filterNot { it.isDirectory }
                    .forEach {
                        Path(tempOutDir.path, it.name).outputStream().use { outStream ->
                            inStream.copyTo(outStream)
                        }
                    }
            }
        } catch (_: ZipException) {
            return fail("There was an error decoding the ZIP at $url. Please try again.")
        } catch (_: IOException) {
            return fail("Something went wrong reading the ZIP at $url. Please try again.")
        }

        tempOutDir.renameTo(File(outDir))

        return succeed()
    }

    private fun succeed(): Result {
        return Result.success(
            Data.Builder().putString(
                KEY_RESULT_MSG,
                "Model download successful!"
            ).build()
        )
    }
    private fun fail(msg: String): Result {
        return Result.failure(
            Data.Builder().putString(
                KEY_RESULT_MSG,
                msg
            ).build()
        )
    }

    companion object {
        const val KEY_MODEL_URL = "model_url"
        const val KEY_MODEL_DIR = "model_dir"
        const val KEY_RESULT_MSG = "msg"
        private const val ZIP_MIME = "application/zip"
    }
}