package com.hashapps.cadenas.ui.processing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.Cadenas
import com.hashapps.cadenas.data.SettingsRepository
import kotlinx.coroutines.launch

class ProcessingViewModel(
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val cadenasInitialized = settingsRepository.cadenasInitialized

    val selectedProfile = settingsRepository.selectedProfile

    var encodeUiState by mutableStateOf(ProcessingUiState())
        private set

    fun updateEncodeUiState(newEncodeUiState: ProcessingUiState) {
        encodeUiState = newEncodeUiState.copy(actionEnabled = newEncodeUiState.isValid())
    }

    var decodeUiState by mutableStateOf(ProcessingUiState())
        private set

    fun updateDecodeUiState(newDecodeUiState: ProcessingUiState) {
        decodeUiState = newDecodeUiState.copy(actionEnabled = newDecodeUiState.isValid())
    }

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