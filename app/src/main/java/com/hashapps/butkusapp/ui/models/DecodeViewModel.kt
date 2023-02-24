package com.hashapps.butkusapp.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import com.hashapps.butkusapp.ui.DecodeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DecodeViewModel : ViewModel() {
    var uiState by mutableStateOf(DecodeUiState())
        private set

    fun updateEncodedMessage(encoded: String) {
        uiState = uiState.copy(message = encoded)
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

    fun resetScreen() {
        uiState = DecodeUiState()
    }
}