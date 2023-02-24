package com.hashapps.butkusapp.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import com.hashapps.butkusapp.ui.EncodeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EncodeViewModel : ViewModel() {
    var uiState by mutableStateOf(EncodeUiState())
        private set

    fun updatePlaintextMessage(plaintext: String) {
        uiState = uiState.copy(message = plaintext)
    }

    fun updateTagToAdd(tag: String) {
        uiState = uiState.copy(tagToAdd = tag)
    }

    fun addTag(tag: String) {
        uiState = uiState.copy(
            tagToAdd = "",
            addedTags = uiState.addedTags + tag,
            encodedMessage = uiState.encodedMessage?.let { "$it #$tag" }
        )
    }

    fun removeTag(tag: String) {
        uiState = uiState.copy(
            addedTags = uiState.addedTags - tag,
            encodedMessage = uiState.encodedMessage?.let {
                it.substringBefore(" #$tag") + it.substringAfter(
                    " #$tag"
                )
            }
        )
    }

    fun encodeMessage() {
        viewModelScope.launch {
            uiState = uiState.copy(inProgress = true, encodedMessage = null)

            withContext(Dispatchers.Default) {
                val encodedMessage = Butkus.getInstance().encode(uiState.message)
                val tagsString =
                    uiState.addedTags.joinToString(separator = "") { " #$it" }

                Snapshot.withMutableSnapshot {
                    uiState = uiState.copy(encodedMessage = encodedMessage + tagsString)
                }
            }

            uiState = uiState.copy(inProgress = false)
        }
    }

    fun resetScreen() {
        uiState = EncodeUiState()
    }
}