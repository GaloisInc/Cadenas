package com.hashapps.butkusapp.ui.decode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DecodeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DecodeUiState())
    val uiState: StateFlow<DecodeUiState>
        get() = _uiState

    fun updateEncodedMessage(encoded: String) {
        _uiState.update { it.copy(message = encoded) }
    }

    fun clearEncodedMessage() {
        _uiState.update { it.copy(message = "") }
    }

    fun decodeMessage() {
        viewModelScope.launch {
            _uiState.update { it.copy(inProgress = true, decodedMessage = null) }

            withContext(Dispatchers.Default) {
                val untaggedMessage = uiState.value.message.substringBefore(delimiter = " #")
                val decodedMessage = Butkus.getInstance().decode(untaggedMessage)
                _uiState.update { it.copy(decodedMessage = decodedMessage) }
            }

            _uiState.update { it.copy(inProgress = false) }
        }
    }

    fun clearDecodedMessage() {
        _uiState.update { it.copy(decodedMessage = null) }
    }
}