package com.hashapps.butkusapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hashapps.butkusapp.Butkus
import com.hashapps.butkusapp.data.DecodeUiState
import com.hashapps.butkusapp.data.EncodeUiState
import com.hashapps.butkusapp.data.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ButkusViewModel : ViewModel() {
    private val _encodeUiState = MutableStateFlow(EncodeUiState())
    private val _decodeUiState = MutableStateFlow(DecodeUiState())
    private val _settingsUiState = MutableStateFlow(SettingsUiState())

    // Has Butkus been initialized yet?
    var butkusInitialized by mutableStateOf(false)

    // Is encoding occurring?
    var isEncoding by mutableStateOf(false)

    // Is decoding occurring?
    var isDecoding by mutableStateOf(false)

    // Avoids state changes from other classes for UI elements on the screens.
    val encodeUiState: StateFlow<EncodeUiState> = _encodeUiState.asStateFlow()
    val decodeUiState: StateFlow<DecodeUiState> = _decodeUiState.asStateFlow()
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    /* ********************************************************************* */
    /* Queries */
    fun uiEnabled(): Boolean {
        return !(isEncoding || isDecoding)
    }

    fun canEncode(): Boolean {
        return butkusInitialized && !isEncoding && _encodeUiState.value.message.isNotEmpty()
    }

    fun canDecode(): Boolean {
        return butkusInitialized && !isDecoding && _decodeUiState.value.message.isNotEmpty()
    }

    /* ********************************************************************* */
    /* Encoding UI updates */

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

    suspend fun encodeMessage() {
        val encodedMessage = Butkus.getInstance().encode(_encodeUiState.value.message)
        val tagsString =
            _encodeUiState.value.addedTags.joinToString(separator = " ", prefix = " ") { "#$it" }

        _encodeUiState.update { currentState ->
            currentState.copy(encodedMessage = encodedMessage + tagsString)
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

    suspend fun decodeMessage() {
        val untaggedMessage = _decodeUiState.value.message.substringBefore(delimiter = " #")
        val decodedMessage = Butkus.getInstance().decode(untaggedMessage)

        _decodeUiState.update { currentState ->
            currentState.copy(decodedMessage = decodedMessage)
        }
    }

    fun resetDecodeState() {
        _decodeUiState.value = DecodeUiState()
    }
}