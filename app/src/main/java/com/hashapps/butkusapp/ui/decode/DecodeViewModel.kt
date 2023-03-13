package com.hashapps.butkusapp.ui.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DecodeUiState (
    /** The message to decode. */
    val message: String = "",

    /** The decoded message. */
    val decodedMessage: String? = null,

    /** Flag indicating decoding is in-progress. */
    val inProgress: Boolean = false,
)

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