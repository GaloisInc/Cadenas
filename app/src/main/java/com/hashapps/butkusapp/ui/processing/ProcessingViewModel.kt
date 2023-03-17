package com.hashapps.butkusapp.ui.processing

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.ButkusRepository
import kotlinx.coroutines.launch

class ProcessingViewModel(
    private val butkusRepository: ButkusRepository,
) : ViewModel() {
    val butkusInitialized = butkusRepository.butkusInitialized

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
            val encodedMessage = butkusRepository.encode(encodeUiState.toProcess)
            encodeUiState = encodeUiState.copy(inProgress = false, result = encodedMessage)
        }
    }

    fun decodeMessage() {
        viewModelScope.launch {
            decodeUiState = decodeUiState.copy(inProgress = true, result = null)
            val decodedMessage = butkusRepository.decode(decodeUiState.toProcess)
            decodeUiState = decodeUiState.copy(inProgress = false, result = decodedMessage)
        }
    }
}