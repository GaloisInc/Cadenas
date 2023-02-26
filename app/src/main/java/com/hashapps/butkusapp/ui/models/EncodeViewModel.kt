package com.hashapps.butkusapp.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class EncodeUiState (
    /** The message to encode. */
    val message: String = "",

    /** The to-be-added tag. */
    val tagToAdd: String = "",

    /** The tags to append to the encoded message. */
    val addedTags: Set<String> = setOf("modnargathsah"),

    /** The encoded message. */
    val encodedMessage: String? = null,

    /** Flag indicating encoding is in-progress.  */
    val inProgress: Boolean = false,
)

class EncodeViewModel : ViewModel() {
    var uiState by mutableStateOf(EncodeUiState())
        private set

    fun updatePlaintextMessage(plaintext: String) {
        uiState = uiState.copy(message = plaintext)
    }

    fun clearPlaintextMessage() {
        uiState = uiState.copy(message = "")
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

    fun clearEncodedMessage() {
        uiState = uiState.copy(encodedMessage = null)
    }
}