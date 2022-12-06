package com.hashapps.butkusapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hashapps.butkusapp.data.EncodeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ButkusViewModel : ViewModel() {
    private val _encodeUiState = MutableStateFlow(EncodeUiState())

    // Avoids state changes from other classes for UI elements on the Encoding
    // screen.
    val encodeUiState: StateFlow<EncodeUiState> = _encodeUiState.asStateFlow()

    // TODO: Do that same thing for the decoding screen

    fun updatePlaintextMessage(plaintext: String) {
        _encodeUiState.update { currentState ->
            currentState.copy(message = plaintext)
        }
    }

    fun updateTagToAdd(tag: String) {
        _encodeUiState.update { currentState ->
            currentState.copy(tagToAdd = tag)
        }
    }

    fun addTag(tag: String) {
        _encodeUiState.update { currentState ->
            currentState.copy(addedTags = currentState.addedTags + tag)
        }
    }

    fun removeTag(tag: String) {
        _encodeUiState.update { currentState ->
            currentState.copy(addedTags = currentState.addedTags - tag)
        }
    }

    // TODO: Make this do something interesting!
    fun encodeMessage() {
        _encodeUiState.update { currentState ->
            currentState.copy(encodedMessage = "I'm hiding a secret")
        }
    }
}