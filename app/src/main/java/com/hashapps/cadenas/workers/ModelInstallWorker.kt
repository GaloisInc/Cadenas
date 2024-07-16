package com.hashapps.cadenas.workers

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.hashapps.cadenas.CadenasApplication
import com.hashapps.cadenas.R
import com.hashapps.cadenas.utils.toHex
import java.io.File
import java.io.IOException
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.io.path.outputStream

class ModelInstallWorker(
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
        val uri = Uri.parse(inputData.getString(KEY_MODEL_URI) ?: return Result.failure())
        val outDir = inputData.getString(KEY_MODEL_DIR) ?: return Result.failure()

        return installModelTo(uri, outDir)
    }

    private suspend fun installModelTo(uri: Uri, outDir: String): Result {
        val contentResolver = context.contentResolver

        val zipStream = contentResolver.openInputStream(uri) ?: return Result.failure()

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
            return fail("There was an error decoding the ZIP at $uri. Please try again.")
        } catch (_: IOException) {
            tempOutDir.deleteRecursively()
            return fail("Something went wrong reading the ZIP at $uri. Please try again.")
        }

        return succeed(hash)
    }

    private fun succeed(hash: String): Result {
        return Result.success(
            workDataOf(
                KEY_RESULT_MSG to "Model install successful!",
                KEY_MODEL_HASH to hash
            )
        )
    }

    private fun fail(msg: String): Result {
        return Result.failure(
            workDataOf(KEY_RESULT_MSG to msg)
        )
    }

    companion object {
        const val PROGRESS = "progress"
        const val KEY_MODEL_URI = "model_uri"
        const val KEY_MODEL_DIR = "model_dir"
        const val KEY_RESULT_MSG = "msg"
        const val KEY_MODEL_HASH = "hash"
    }
}