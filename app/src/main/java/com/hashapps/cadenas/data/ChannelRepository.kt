package com.hashapps.cadenas.data

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.galois.cadenas.crypto.RandomPadding
import com.galois.cadenas.crypto.SivAesWithSentinel
import com.galois.cadenas.mbfte.TextCover
import com.galois.cadenas.model.PyTorchGPT2LanguageModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.crypto.KeyGenerator

/**
 * Repository class for Cadenas messaging channels.
 *
 * Loosely wraps around the [ChannelDao] methods, and provides key-generation
 * capabilities for channel creation.
 */
class ChannelRepository(
    private val contentResolver: ContentResolver,
    private val modelsDir: File,
    private val channelDao: ChannelDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun insertChannel(channel: Channel): Long = channelDao.insert(channel)
    suspend fun updateChannel(channel: Channel): Unit = channelDao.update(channel)
    suspend fun deleteChannel(channel: Channel): Unit = channelDao.delete(channel)

    suspend fun deleteChannelsForModel(model: String): Unit =
        channelDao.deleteChannelsForModel(model)

    fun getChannelStream(id: Long): Flow<Channel> = channelDao.getChannel(id)
    fun getAllChannelsStream(): Flow<List<Channel>> = channelDao.getAllChannels()

    private companion object {
        val KEYGEN: KeyGenerator = KeyGenerator.getInstance("AES").also { it.init(256) }
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }

    /**
     * Generate and return an AES-256 key as ASCII-Hex.
     */
    fun genKey(): String = KEYGEN.generateKey().encoded.toHex()

    /**
     * Save a channel's QR bitmap to disk.
     */
    suspend fun saveQRBitmap(qrBitmap: ImageBitmap?) = withContext(ioDispatcher) {
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val qrDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "qr-${UUID.randomUUID()}.png")
        }
        val qrUri = contentResolver.insert(imageCollection, qrDetails)!!
        contentResolver.openOutputStream(qrUri).use {
            it?.also { qrBitmap?.asAndroidBitmap()?.compress(Bitmap.CompressFormat.PNG, 0, it) }
        }
    }

    /**
     * Create a [TextCover] for the profile with given ID.
     */
    suspend fun createTextCoverForChannel(id: Long): TextCover {
        return withContext(ioDispatcher) {
            val channel = getChannelStream(id).first()
            TextCover(
                cryptoSystem = RandomPadding(
                    SivAesWithSentinel(
                        channel.key.chunked(2).map { b -> b.toInt(16).toByte() }.toByteArray()
                    )
                ),
                languageModel = PyTorchGPT2LanguageModel(modelsDir.resolve(channel.selectedModel).path),
                seed = channel.prompt,
            )
        }
    }
}