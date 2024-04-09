package com.hashapps.cadenas.data.channels

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
import com.hashapps.cadenas.utils.toHex
import com.hashapps.cadenas.utils.toHexBytes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.crypto.KeyGenerator

/**
 * Repository class for Cadenas messaging channels.
 *
 * Loosely wraps around the [ChannelDao] methods, and provides key-generation
 * capabilities for channel creation.
 */
class OfflineChannelRepository(
    private val contentResolver: ContentResolver,
    private val modelsDir: File,
    private val channelDao: ChannelDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): ChannelRepository {
    override suspend fun insertChannel(channel: Channel): Long = channelDao.insert(channel)
    override suspend fun updateChannel(channel: Channel): Unit = channelDao.update(channel)
    override suspend fun deleteChannel(channel: Channel): Unit = channelDao.delete(channel)

    override fun getChannelStream(id: Long): Flow<Channel> = channelDao.getChannel(id)
    override fun getAllChannelsStream(): Flow<List<Channel>> = channelDao.getAllChannels()

    private companion object {
        val KEYGEN: KeyGenerator = KeyGenerator.getInstance("AES").also { it.init(256) }
    }

    /**
     * Generate and return an AES-256 key as ASCII-Hex.
     */
    override fun genKey(): String = KEYGEN.generateKey().encoded.toHex()

    /**
     * Save a channel's QR bitmap to disk.
     */
    override suspend fun saveQRBitmap(qrBitmap: ImageBitmap, channelId: Long) = withContext(ioDispatcher) {
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val qrDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "qr-$channelId.png")
        }
        val qrUri = contentResolver.insert(imageCollection, qrDetails)!!
        contentResolver.openOutputStream(qrUri).use {
            it?.also { qrBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 0, it) }
        }

        Unit
    }

    /**
     * Create a [TextCover] for the profile with given ID.
     */
    override suspend fun createTextCoverForChannel(id: Long): TextCover {
        return withContext(ioDispatcher) {
            val channel = getChannelStream(id).first()
            TextCover(
                cryptoSystem = RandomPadding(
                    SivAesWithSentinel(
                        channel.key.toHexBytes()
                    )
                ),
                languageModel = PyTorchGPT2LanguageModel(modelsDir.resolve(channel.selectedModel).path),
                prompt = channel.prompt,
            )
        }
    }
}