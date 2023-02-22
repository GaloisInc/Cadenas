package com.hashapps.butkusapp.ui.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import com.hashapps.butkusapp.ui.DecodeUiState
import com.hashapps.butkusapp.ui.EncodeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for ButkusApp
 */
class ButkusAppViewModel(app: Application) : AndroidViewModel(app) {
    /** Flag indicating whether or not Butkus has been initialized */
    private val _butkusInitialized = MutableStateFlow(false)
    val butkusInitialized: StateFlow<Boolean> = _butkusInitialized.asStateFlow()

    /** Encode screen backing property / read-only interface  */
    private val _encodeUiState = MutableStateFlow(EncodeUiState())
    val encodeUiState: StateFlow<EncodeUiState> = _encodeUiState.asStateFlow()

    /** Decode screen backing property / read-only interface */
    private val _decodeUiState = MutableStateFlow(DecodeUiState())
    val decodeUiState: StateFlow<DecodeUiState> = _decodeUiState.asStateFlow()

    // Since we are an AndroidViewModel, we can access application context
    init {
        viewModelScope.launch {
            Butkus.initialize(app.applicationContext)
            _butkusInitialized.value = true
        }
    }

    /** ********** Encode screen methods ********** */

    /** Update plaintext input box on encode screen */
    fun updatePlaintextMessage(plaintext: String) {
        _encodeUiState.update {
            it.copy(message = plaintext)
        }
    }

    /** Update tag input box on encode screen */
    fun updateTagToAdd(tag: String) {
        _encodeUiState.update {
            it.copy(tagToAdd = tag)
        }
    }

    /** Add tag to set backing list view, update encoded text if not null */
    fun addTag(tag: String) {
        _encodeUiState.update { cs ->
            cs.copy(
                addedTags = cs.addedTags + tag,
                encodedMessage = cs.encodedMessage?.let { "$it #$tag" }
            )
        }
    }

    /** Remove tag from set backing list view, update encoded text if not null */
    fun removeTag(tag: String) {
        _encodeUiState.update { cs ->
            cs.copy(
                addedTags = cs.addedTags - tag,
                encodedMessage = cs.encodedMessage?.let {
                    it.substringBefore(" #$tag") + it.substringAfter(
                        " #$tag"
                    )
                }
            )
        }
    }

    /**
     * Encode the message in the plaintext input box, format and append tags,
     * and update encoded text
     */
    suspend fun encodeMessage() {
        withContext(Dispatchers.Default) {
            _encodeUiState.update { it.copy(inProgress = true, encodedMessage = null) }

            val encodedMessage = Butkus.getInstance().encode(_encodeUiState.value.message)
            val tagsString =
                _encodeUiState.value.addedTags.joinToString(separator = "") { " #$it" }
            _encodeUiState.update { it.copy(encodedMessage = encodedMessage + tagsString) }

            _encodeUiState.update { it.copy(inProgress = false) }
        }
    }

    /** Reset the encode UI state to defaults */
    fun resetEncodeScreen() {
        _encodeUiState.value = EncodeUiState()
    }

    /** ********** Decode screen methods ********** */

    /** Updated encoded message input box on decode screen */
    fun updateEncodedMessage(encoded: String) {
        _decodeUiState.update { it.copy(message = encoded) }
    }

    /**
     * Strip any tags from the encoded text, decode it, and update decoded text
     */
    suspend fun decodeMessage() {
        withContext(Dispatchers.Default) {
            _decodeUiState.update { it.copy(inProgress = true, decodedMessage = null) }

            val untaggedMessage = _decodeUiState.value.message.substringBefore(delimiter = " #")
            val decodedMessage = Butkus.getInstance().decode(untaggedMessage)
            _decodeUiState.update { it.copy(decodedMessage = decodedMessage) }

            _decodeUiState.update { it.copy(inProgress = false) }
        }
    }

    /** Reset the decode UI state to defaults */
    fun resetDecodeScreen() {
        _decodeUiState.value = DecodeUiState()
    }
}