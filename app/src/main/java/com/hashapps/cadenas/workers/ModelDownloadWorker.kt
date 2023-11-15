package com.hashapps.cadenas.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.hashapps.cadenas.CadenasApplication
import com.hashapps.cadenas.R
import com.hashapps.cadenas.utils.toHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import javax.net.ssl.HttpsURLConnection
import kotlin.io.path.Path
import kotlin.io.path.outputStream

class ModelDownloadWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, CadenasApplication.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.download_title))
            .setSmallIcon(R.drawable.baseline_downloading_24)
            .build()

        return ForegroundInfo(0, notification)
    }

    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_MODEL_URL) ?: return Result.failure()
        val outDir = inputData.getString(KEY_MODEL_DIR) ?: return Result.failure()

        return downloadModelTo(url, outDir)
    }

    private suspend fun downloadModelTo(url: String, outDir: String): Result {
        // TODO: We need to be smarter about some of these string literals for localization!
        // TODO: Maybe we use some internal string constants to later choose the UI text?

        val urlConnection = try {
            withContext(Dispatchers.IO) {
                URL(url).openConnection()
            }
        } catch (_: IOException) {
            return fail("Something went wrong preparing a connection to $url. Please try again.")
        }

        // Sanity check: The UI should catch any non-HTTPS inputs.
        require(urlConnection is HttpsURLConnection)

        try {
            withContext(Dispatchers.IO) {
                urlConnection.connect()
            }
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

        val tempOutDir = File("$outDir.temp")
        if (!tempOutDir.mkdirs()) {
            return fail("Could not create a directory for the model. Please try again.")
        }

        val hash = try {
            val md = MessageDigest.getInstance("MD5")
            ZipInputStream(zipStream).use { inStream ->
                generateSequence { inStream.nextEntry }
                    .filterNot { it.isDirectory }
                    .forEach {
                        val dis = if (it.name == "gpt2.ptl") {
                            DigestInputStream(inStream, md)
                        } else {
                            inStream
                        }
                        Path(tempOutDir.path, it.name).outputStream().use { outStream ->
                            setProgress(workDataOf(PROGRESS to it.name))
                            dis.copyTo(outStream)
                        }
                    }
            }
            tempOutDir.renameTo(File(outDir))
            md.digest().toHex()
        } catch (_: ZipException) {
            tempOutDir.deleteRecursively()
            return fail("There was an error decoding the ZIP at $url. Please try again.")
        } catch (_: IOException) {
            tempOutDir.deleteRecursively()
            return fail("Something went wrong reading the ZIP at $url. Please try again.")
        }

        return succeed(hash)
    }

    private fun succeed(hash: String): Result {
        return Result.success(
            workDataOf(KEY_RESULT_MSG to "Model download successful!", KEY_MODEL_HASH to hash)
        )
    }
    private fun fail(msg: String): Result {
        return Result.failure(
            workDataOf(KEY_RESULT_MSG to msg)
        )
    }

    companion object {
        const val PROGRESS = "progress"
        const val KEY_MODEL_URL = "model_url"
        const val KEY_MODEL_DIR = "model_dir"
        const val KEY_RESULT_MSG = "msg"
        const val KEY_MODEL_HASH = "hash"
        private const val ZIP_MIME = "application/zip"
    }
}