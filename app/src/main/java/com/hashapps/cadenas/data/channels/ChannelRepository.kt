package com.hashapps.cadenas.data.channels

import androidx.compose.ui.graphics.ImageBitmap
import com.galois.cadenas.mbfte.TextCover
import kotlinx.coroutines.flow.Flow

/**
 * Channel repository interface.
 *
 * This abstraction exists to enable testing of ViewModels that use channels.
 */
interface ChannelRepository {
    suspend fun insertChannel(channel: Channel): Long
    suspend fun updateChannel(channel: Channel)
    suspend fun deleteChannel(channel: Channel)
    suspend fun deleteAllChannels()

    fun getChannelStream(id: Long): Flow<Channel>
    fun getAllChannelsStream(): Flow<List<Channel>>

    fun genKey(): String

    suspend fun saveQRBitmap(qrBitmap: ImageBitmap, channelId: Long)
    suspend fun createTextCoverForChannel(id: Long): TextCover
}