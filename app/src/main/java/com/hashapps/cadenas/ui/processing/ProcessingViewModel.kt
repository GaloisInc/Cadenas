package com.hashapps.cadenas.ui.processing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galois.cadenas.mbfte.TextCover
import com.hashapps.cadenas.data.ChannelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View model for the processing screens.
 *
 * @property[processingUiState] The UI state for the processing screen
 */
class ProcessingViewModel(
    savedStateHandle: SavedStateHandle,
    private val channelRepository: ChannelRepository,
) : ViewModel() {
    private val processingArgs = ProcessingArgs(savedStateHandle)

    private var textCover: TextCover? by mutableStateOf(null)

    val cadenasInitialized
        get() = textCover != null

    init {
        viewModelScope.launch {
            textCover = channelRepository.createTextCoverForChannel(processingArgs.channelId)
        }
    }

    var processingUiState by mutableStateOf(ProcessingUiState())
        private set

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
        processingUiState = ProcessingUiState(processingMode = ProcessingMode.Encode)
    }

    /**
     * Enter decoding mode.
     */
    fun decodingMode() {
        processingUiState = ProcessingUiState(processingMode = ProcessingMode.Decode)
    }

    /**
     * Encode or decode the input, based on the current processing mode.
     */
    fun processMessage(tag: String) {
        when (processingUiState.processingMode) {
            ProcessingMode.Encode -> encodeMessage(tag)
            ProcessingMode.Decode -> decodeMessage(tag)
        }
    }

    /**
     * Attempt to encode the input message using the selected messaging
     * channel, adding the channel's tag to the end (if any.)
     */
    private fun encodeMessage(tag: String) {
        viewModelScope.launch {
            processingUiState = processingUiState.copy(inProgress = true, result = null)
            val encodedMessage = withContext(Dispatchers.Default) {
                textCover?.encodeUntilDecodable(processingUiState.toProcess)
            }
            processingUiState =
                processingUiState.copy(inProgress = false, result = encodedMessage?.plus(tag))
        }
    }

    /**
     * Attempt to decode the input message using the selected messaging
     * channel, first removing the channel's tag (if any.)
     */
    private fun decodeMessage(tag: String) {
        viewModelScope.launch {
            processingUiState = processingUiState.copy(inProgress = true, result = null)
            val decodedMessage = withContext(Dispatchers.Default) {
                textCover?.decode(processingUiState.toProcess.removeSuffix(tag))
            }
            processingUiState = processingUiState.copy(inProgress = false, result = decodedMessage)
        }
    }
}