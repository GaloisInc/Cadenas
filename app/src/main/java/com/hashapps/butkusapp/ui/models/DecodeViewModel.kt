package com.hashapps.butkusapp.ui.models

import com.hashapps.butkusapp.Butkus
import com.hashapps.butkusapp.data.DecodeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** ButkusViewModel for the decoding screen */
class DecodeViewModel : ButkusViewModel() {
    private val _decodeUiState = MutableStateFlow(DecodeUiState())

    /** True iff the encoded text input box is non-empty */
    override val runInputNonempty: Boolean
        get() = _decodeUiState.value.message.isNotEmpty()

    /** DecodeViewModel-controlled state, safe from changes from other classes */
    val decodeUiState: StateFlow<DecodeUiState> = _decodeUiState.asStateFlow()

    /** Update state backing the encoded text input box */
    fun updateEncodedMessage(encoded: String) {
        _decodeUiState.update { currentState ->
            currentState.copy(message = encoded)
        }
    }

    /** Strip any tags from the encoded text, decode the message, and update
     * the state backing the (usually hidden) decoded-message text box */
    override suspend fun run() {
        val untaggedMessage = _decodeUiState.value.message.substringBefore(delimiter = " #")
        val decodedMessage = Butkus.getInstance().decode(untaggedMessage)

        _decodeUiState.update { currentState ->
            currentState.copy(decodedMessage = decodedMessage)
        }
    }

    /** Reset the decode UI state to defaults (i.e. clear the UI) */
    override fun reset() {
        _decodeUiState.value = DecodeUiState()
    }
}