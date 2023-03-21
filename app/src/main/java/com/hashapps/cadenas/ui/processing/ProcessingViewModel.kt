package com.hashapps.cadenas.ui.processing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.CadenasRepository
import kotlinx.coroutines.launch

class ProcessingViewModel(
    private val cadenasRepository: CadenasRepository,
) : ViewModel() {
    val cadenasInitialized = cadenasRepository.cadenasInitialized

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

    fun encodeMessage() {
        viewModelScope.launch {
            encodeUiState = encodeUiState.copy(inProgress = true, result = null)
            val encodedMessage = cadenasRepository.encode(encodeUiState.toProcess)
            encodeUiState = encodeUiState.copy(inProgress = false, result = encodedMessage)
        }
    }

    fun decodeMessage() {
        viewModelScope.launch {
            decodeUiState = decodeUiState.copy(inProgress = true, result = null)
            val decodedMessage = cadenasRepository.decode(decodeUiState.toProcess)
            decodeUiState = decodeUiState.copy(inProgress = false, result = decodedMessage)
        }
    }
}