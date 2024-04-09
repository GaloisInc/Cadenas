package com.hashapps.cadenas.ui.channels.export

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.Channel
import com.hashapps.cadenas.data.channels.ChannelRepository
import com.hashapps.cadenas.data.models.ModelRepository
import io.github.g0dkar.qrcode.ErrorCorrectionLevel
import io.github.g0dkar.qrcode.QRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

private fun Channel.toQRCode(hash: String): QRCode = QRCode(
    data = "key:$key;prompt:$prompt;model:$hash",
    errorCorrectionLevel = ErrorCorrectionLevel.Q,
)

private suspend fun QRCode.toByteArray(): ByteArray = withContext(Dispatchers.Default) {
    ByteArrayOutputStream()
        .also {
            render(margin = 25).writeImage(destination = it, quality = 50)
        }
        .toByteArray()
}

suspend fun QRCode.toImageBitmap(): ImageBitmap {
    return withContext(Dispatchers.Default) {
        val bytes = toByteArray()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
            .also { it.prepareToDraw() }
    }
}

/**
 * View model for the channel-exporting screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChannelExportViewModel(
    savedStateHandle: SavedStateHandle,
    private val channelRepository: ChannelRepository,
    private val modelRepository: ModelRepository,
) : ViewModel() {
    private val channelExportArgs = ChannelExportArgs(savedStateHandle)

    var qrBitmap: ImageBitmap? by mutableStateOf(null)
        private set
    var channelName: String? by mutableStateOf(null)
        private set
    private var channelId: Long? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            qrBitmap = channelRepository.getChannelStream(channelExportArgs.channelId)
                .flatMapLatest { channel ->
                    channelName = channel.name
                    channelId = channel.id
                    modelRepository.getModelStream(channel.selectedModel)
                        .map { model ->
                            channel.toQRCode(model.hash).toImageBitmap()
                        }
                }
                .first()
        }
    }

    /**
     * Save the channel's QR code as an image.
     */
    fun saveQRBitmap() {
        viewModelScope.launch {
            if (qrBitmap != null && channelId != null) {
                channelRepository.saveQRBitmap(qrBitmap!!, channelId!!)
            }
        }
    }
}