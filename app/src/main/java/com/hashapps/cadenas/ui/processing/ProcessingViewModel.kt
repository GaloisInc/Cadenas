package com.hashapps.cadenas.ui.processing

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.hashapps.cadenas.data.Cadenas
import com.hashapps.cadenas.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View model for the processing screens.
 *
 * @property[cadenasInitialized] Whether or not Cadenas is ready to process
 * @property[selectedChannel] The currently-selected messaging channel
 * @property[processingUiState] The UI state for the processing screen
 */
class ProcessingViewModel(
    savedStateHandle: SavedStateHandle,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val cadenasInitialized = settingsRepository.cadenasInitialized

    val selectedChannel = settingsRepository.selectedChannel

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

    private val sharedTextState = savedStateHandle.getStateFlow(NavController.KEY_DEEP_LINK_INTENT, Intent())
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
        .onEach { if (it.isNotEmpty()) updateProcessingUiState(ProcessingUiState(toProcess = it, processingMode = ProcessingMode.Decode)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ""
        )

    init {
        sharedTextState.launchIn(viewModelScope)
    }

    /**
     * Attempt to encode the input message using the selected messaging
     * channel, adding the channel's tag to the end (if any.)
     */
    private fun encodeMessage(tag: String) {
        viewModelScope.launch {
            processingUiState = processingUiState.copy(inProgress = true, result = null)
            val encodedMessage = withContext(Dispatchers.Default) {
                Cadenas.getInstance()?.encode(processingUiState.toProcess)
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
                Cadenas.getInstance()?.decode(processingUiState.toProcess.removeSuffix(tag))
            }
            processingUiState = processingUiState.copy(inProgress = false, result = decodedMessage)
        }
    }
}