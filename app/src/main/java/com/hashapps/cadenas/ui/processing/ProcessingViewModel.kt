package com.hashapps.cadenas.ui.processing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.Cadenas
import com.hashapps.cadenas.data.SettingsRepository
import kotlinx.coroutines.launch

/**
 * View model for the processing screens.
 *
 * @property[cadenasInitialized] Whether or not Cadenas is ready to process
 * @property[selectedProfile] The currently-selected messaging profile
 * @property[encodeUiState] The UI state for the encoding screen
 * @property[decodeUiState] The UI state for the decoding screen
 */
class ProcessingViewModel(
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val cadenasInitialized = settingsRepository.cadenasInitialized

    val selectedProfile = settingsRepository.selectedProfile

    var encodeUiState by mutableStateOf(ProcessingUiState())
        private set

    /**
     * Update the encode screen UI state, only enabling the action if the new
     * state is valid.
     *
     * @param[newEncodeUiState] The new UI state
     */
    fun updateEncodeUiState(newEncodeUiState: ProcessingUiState) {
        encodeUiState = newEncodeUiState.copy(actionEnabled = newEncodeUiState.isValid())
    }

    var decodeUiState by mutableStateOf(ProcessingUiState())
        private set

    /**
     * Update the decode screen UI state, only enabling the action if the new
     * state is valid.
     *
     * @param[newDecodeUiState] The new UI state
     */
    fun updateDecodeUiState(newDecodeUiState: ProcessingUiState) {
        decodeUiState = newDecodeUiState.copy(actionEnabled = newDecodeUiState.isValid())
    }

    /**
     * Attempt to encode the
     */
    fun encodeMessage(tag: String) {
        viewModelScope.launch {
            encodeUiState = encodeUiState.copy(inProgress = true, result = null)
            val encodedMessage = Cadenas.getInstance()?.encode(encodeUiState.toProcess)
            encodeUiState = encodeUiState.copy(inProgress = false, result = encodedMessage?.plus(tag) )
        }
    }

    fun decodeMessage(tag: String) {
        viewModelScope.launch {
            decodeUiState = decodeUiState.copy(inProgress = true, result = null)
            val decodedMessage = Cadenas.getInstance()?.decode(decodeUiState.toProcess.removeSuffix(tag))
            decodeUiState = decodeUiState.copy(inProgress = false, result = decodedMessage)
        }
    }
}