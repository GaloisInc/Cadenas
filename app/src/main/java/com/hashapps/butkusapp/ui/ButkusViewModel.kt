package com.hashapps.butkusapp.ui

import androidx.lifecycle.ViewModel
import com.hashapps.butkusapp.data.DecodeUiState
import com.hashapps.butkusapp.data.EncodeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ButkusViewModel : ViewModel() {
    private val _encodeUiState = MutableStateFlow(EncodeUiState())
    private val _decodeUiState = MutableStateFlow(DecodeUiState())

    // Avoids state changes from other classes for UI elements on the screens.
    val encodeUiState: StateFlow<EncodeUiState> = _encodeUiState.asStateFlow()
    val decodeUiState: StateFlow<DecodeUiState> = _decodeUiState.asStateFlow()

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
            currentState.copy(encodedMessage = "I'm hiding a secret", canShare = true)
        }
    }

    fun resetEncodeState() {
        _encodeUiState.value = EncodeUiState()
    }

    /* ********************************************************************* */

    fun updateEncodedMessage(encoded: String) {
        _decodeUiState.update { currentState ->
            currentState.copy(message = encoded)
        }
    }

    // TODO: Make this do something interesting!
    fun decodeMessage() {
        _decodeUiState.update { currentState ->
            currentState.copy(decodedMessage = "Secret message", canShare = true)
        }
    }

    fun resetDecodeState() {
        _decodeUiState.value = DecodeUiState()
    }
}