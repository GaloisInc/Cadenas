package com.hashapps.cadenas.ui.processing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.OfflineChannelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View model for the processing screens.
 *
 * @property[processingUiState] The UI state for the processing screen
 */
class ProcessingViewModel(
    savedStateHandle: SavedStateHandle,
    private val offlineChannelRepository: OfflineChannelRepository,
) : ViewModel() {
    private val processingArgs = ProcessingArgs(savedStateHandle)

    var processingUiState by mutableStateOf(ProcessingUiState())
        private set

    init {
        viewModelScope.launch {
            val channelName =
                offlineChannelRepository.getChannelStream(processingArgs.channelId).map { it.name }.first()
            processingUiState = processingUiState.copy(
                channelName = channelName,
                toProcess = processingArgs.toDecode,
                processingMode = if (processingArgs.toDecode.isEmpty()) {
                    ProcessingMode.Encode
                } else {
                    ProcessingMode.Decode
                }
            )
        }
    }

    /**
     * Update the UI state.
     *
     * @param[newProcessingUiState] The new UI state
     */
    fun updateProcessingUiState(newProcessingUiState: ProcessingUiState) {
        processingUiState = newProcessingUiState.copy()
    }

    /**
     * Enter encoding mode.
     */
    fun encodingMode() {
        processingUiState = ProcessingUiState(
            channelName = processingUiState.channelName,
            processingMode = ProcessingMode.Encode
        )
    }

    /**
     * Enter decoding mode.
     */
    fun decodingMode() {
        processingUiState = ProcessingUiState(
            channelName = processingUiState.channelName,
            processingMode = ProcessingMode.Decode
        )
    }

    /**
     * Encode or decode the input, based on the current processing mode.
     */
    fun processMessage() {
        when (processingUiState.processingMode) {
            ProcessingMode.Encode -> encodeMessage()
            ProcessingMode.Decode -> decodeMessage()
        }
    }

    /**
     * Attempt to encode the input message using the selected messaging
     * channel.
     */
    private fun encodeMessage() {
        viewModelScope.launch {
            processingUiState = processingUiState.copy(inProgress = true, result = null)
            val textCover = offlineChannelRepository.createTextCoverForChannel(processingArgs.channelId)
            val encodedMessage = withContext(Dispatchers.Default) {
                textCover.encodeUntilDecodable(processingUiState.toProcess)
            }
            textCover.destroy()
            processingUiState =
                processingUiState.copy(inProgress = false, result = encodedMessage?.coverText)
        }
    }

    /**
     * Attempt to decode the input message using the selected messaging
     * channel.
     */
    private fun decodeMessage() {
        viewModelScope.launch {
            processingUiState = processingUiState.copy(inProgress = true, result = null)
            val textCover = offlineChannelRepository.createTextCoverForChannel(processingArgs.channelId)
            val decodedMessage = withContext(Dispatchers.Default) {
                textCover.decode(processingUiState.toProcess)
            }
            textCover.destroy()
            processingUiState = processingUiState.copy(inProgress = false, result = decodedMessage)
        }
    }
}