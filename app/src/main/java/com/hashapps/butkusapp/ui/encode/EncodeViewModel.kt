package com.hashapps.butkusapp.ui.encode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EncodeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EncodeUiState())
    val uiState: StateFlow<EncodeUiState>
        get() = _uiState

    fun updatePlaintextMessage(plaintext: String) {
        _uiState.update { it.copy(message = plaintext) }
    }

    fun clearPlaintextMessage() {
        _uiState.update { it.copy(message = "") }
    }

    fun updateTagToAdd(tag: String) {
        _uiState.update { it.copy(tagToAdd = tag) }
    }

    fun addTag(tag: String) {
        _uiState.update { cs ->
            cs.copy(
                tagToAdd = "",
                addedTags = uiState.value.addedTags + tag,
                encodedMessage = uiState.value.encodedMessage?.let { "$it #$tag" }
            )
        }
    }

    fun removeTag(tag: String) {
        _uiState.update { cs ->
            cs.copy(
                addedTags = uiState.value.addedTags - tag,
                encodedMessage = uiState.value.encodedMessage?.let {
                    it.substringBefore(" #$tag") + it.substringAfter(
                        " #$tag"
                    )
                }
            )
        }
    }

    fun encodeMessage() {
        viewModelScope.launch {
            _uiState.update { it.copy(inProgress = true, encodedMessage = null) }

            withContext(Dispatchers.Default) {
                val encodedMessage = Butkus.getInstance().encode(uiState.value.message)
                val tagsString =
                    uiState.value.addedTags.joinToString(separator = "") { " #$it" }
                _uiState.update { it.copy(encodedMessage = encodedMessage + tagsString) }
            }

            _uiState.update { it.copy(inProgress = false) }
        }
    }

    fun clearEncodedMessage() {
        _uiState.update { it.copy(encodedMessage = null) }
    }
}