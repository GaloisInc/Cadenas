package com.hashapps.butkusapp.ui.models

import com.hashapps.butkusapp.Butkus
import com.hashapps.butkusapp.data.EncodeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** ButkusViewModel for the encoding screen */
class EncodeViewModel : ButkusViewModel() {
    private val _encodeUiState = MutableStateFlow(EncodeUiState())

    /** True iff the plaintext input box is non-empty */
    override val runInputNonempty: Boolean
        get() = _encodeUiState.value.message.isNotEmpty()

    /** True iff the encoded output is non-null */
    override val runOutputNonempty: Boolean
        get() = _encodeUiState.value.encodedMessage != null

    /** EncodeViewModel-controlled state, safe from changes from other classes */
    val encodeUiState: StateFlow<EncodeUiState> = _encodeUiState.asStateFlow()

    /** Update state backing the plaintext input box */
    fun updatePlaintextMessage(plaintext: String) {
        _encodeUiState.update { currentState ->
            currentState.copy(message = plaintext)
        }
    }

    /** Update state backing tag input box */
    fun updateTagToAdd(tag: String) {
        _encodeUiState.update { currentState ->
            currentState.copy(tagToAdd = tag)
        }
    }

    /** Add `tag` from the state backing the tag list view and update the
     * encoded text if it is not null */
    fun addTag(tag: String) {
        if (tag !in _encodeUiState.value.addedTags) {
            _encodeUiState.update { currentState ->
                currentState.copy(
                    addedTags = currentState.addedTags + tag,
                    encodedMessage = currentState.encodedMessage?.let { "$it #$tag" }
                )
            }
        }
    }

    /** Remove `tag` from the state backing the tag list view and update the
     * encoded text if it is not null */
    fun removeTag(tag: String) {
        if (tag in _encodeUiState.value.addedTags) {
            _encodeUiState.update { currentState ->
                currentState.copy(
                    addedTags = currentState.addedTags - tag,
                    encodedMessage = currentState.encodedMessage?.let {
                        it.substringBefore(" #$tag") + it.substringAfter(
                            " #$tag"
                        )
                    }
                )
            }
        }
    }

    /** Get the (possibly null) encoded output */
    val encodedMessage
        get() = encodeUiState.value.encodedMessage

    /** Encode the message in the plaintext input box, format and append tags,
     * and update the state backing the (usually hidden) encoded-message text box */
    override suspend fun run() {
        val encodedMessage = Butkus.getInstance().encode(_encodeUiState.value.message)
        val tagsString =
            _encodeUiState.value.addedTags.joinToString(separator = "") { " #$it" }

        _encodeUiState.update { it.copy(encodedMessage = encodedMessage + tagsString) }
    }

    /** Reset the encode UI state to defaults (i.e. clear the UI) */
    override fun reset() {
        _encodeUiState.value = EncodeUiState()
    }
}