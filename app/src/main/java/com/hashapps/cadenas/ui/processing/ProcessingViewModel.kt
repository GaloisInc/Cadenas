package com.hashapps.cadenas.ui.processing

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.galois.cadenas.mbfte.TextCover
import com.hashapps.cadenas.data.ChannelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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
    private val sharedTextState =
        savedStateHandle.getStateFlow(NavController.KEY_DEEP_LINK_INTENT, Intent())
            .map {
                if (it.action != Intent.ACTION_SEND) {
                    return@map ""
                }

                if (it.type.equals("text/plain")) {
                    it.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                } else {
                    ""
                }
            }
            .onEach {
                if (it.isNotEmpty()) updateProcessingUiState(
                    ProcessingUiState(
                        toProcess = it,
                        processingMode = ProcessingMode.Decode
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = ""
            )

    init {
        sharedTextState.launchIn(viewModelScope)
    }

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

    init {
        viewModelScope.launch {
            val channelName =
                channelRepository.getChannelStream(processingArgs.channelId).map { it.name }.first()
            processingUiState = processingUiState.copy(channelName = channelName)
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
            val encodedMessage = withContext(Dispatchers.Default) {
                textCover?.encodeUntilDecodable(processingUiState.toProcess)
            }
            processingUiState =
                processingUiState.copy(inProgress = false, result = encodedMessage)
        }
    }

    /**
     * Attempt to decode the input message using the selected messaging
     * channel.
     */
    private fun decodeMessage() {
        viewModelScope.launch {
            processingUiState = processingUiState.copy(inProgress = true, result = null)
            val decodedMessage = withContext(Dispatchers.Default) {
                textCover?.decode(processingUiState.toProcess)
            }
            processingUiState = processingUiState.copy(inProgress = false, result = decodedMessage)
        }
    }
}