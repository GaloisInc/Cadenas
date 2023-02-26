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

data class DecodeUiState (
    /** The message to decode. */
    val message: String = "",

    /** The decoded message. */
    val decodedMessage: String? = null,

    /** Flag indicating decoding is in-progress. */
    val inProgress: Boolean = false,
)

class DecodeViewModel : ViewModel() {
    var uiState by mutableStateOf(DecodeUiState())
        private set

    fun updateEncodedMessage(encoded: String) {
        uiState = uiState.copy(message = encoded)
    }

    fun clearEncodedMessage() {
        uiState = uiState.copy(message = "")
    }

    fun decodeMessage() {
        viewModelScope.launch {
            uiState = uiState.copy(inProgress = true, decodedMessage = null)

            withContext(Dispatchers.Default) {
                val untaggedMessage = uiState.message.substringBefore(delimiter = " #")
                val decodedMessage = Butkus.getInstance().decode(untaggedMessage)

                Snapshot.withMutableSnapshot {
                    uiState = uiState.copy(decodedMessage = decodedMessage)
                }
            }

            uiState = uiState.copy(inProgress = false)
        }
    }

    fun clearDecodedMessage() {
        uiState = uiState.copy(decodedMessage = null)
    }
}