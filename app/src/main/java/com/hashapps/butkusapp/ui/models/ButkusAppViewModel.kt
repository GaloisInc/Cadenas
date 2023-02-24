package com.hashapps.butkusapp.ui.models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import com.hashapps.butkusapp.ui.DecodeUiState
import com.hashapps.butkusapp.ui.EncodeUiState
import com.hashapps.butkusapp.ui.SettingsUiState
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
        viewModelScope.launch(Dispatchers.Default) {
            uiState = uiState.copy(inProgress = true, encodedMessage = null)

            val encodedMessage = Butkus.getInstance().encode(uiState.message)
            val tagsString =
                uiState.addedTags.joinToString(separator = "") { " #$it" }
            uiState = uiState.copy(encodedMessage = encodedMessage + tagsString)

            uiState = uiState.copy(inProgress = false)
        }
    }

    fun resetScreen() {
        uiState = EncodeUiState()
    }
}

class DecodeScreenModel : ViewModel() {
    var uiState by mutableStateOf(DecodeUiState())
        private set

    fun updateEncodedMessage(encoded: String) {
        uiState = uiState.copy(message = encoded)
    }

    fun decodeMessage() {
        viewModelScope.launch(Dispatchers.Default) {
            Snapshot.withMutableSnapshot {
                uiState = uiState.copy(inProgress = true, decodedMessage = null)

                val untaggedMessage = uiState.message.substringBefore(delimiter = " #")
                val decodedMessage = Butkus.getInstance().decode(untaggedMessage)
                uiState = uiState.copy(decodedMessage = decodedMessage)

                uiState = uiState.copy(inProgress = false)
            }
        }
    }

    fun resetScreen() {
        uiState = DecodeUiState()
    }
}

class SettingsScreenModel : ViewModel() {
    var uiState by mutableStateOf(SettingsUiState())
        private set

    fun updateSeedText(seed: String) {
        uiState = uiState.copy(seedText = seed)
    }

    fun updateModelToAdd(url: String) {
        uiState = uiState.copy(modelUrlToAdd = url)
    }

    fun addUrl(url: String) {
        uiState = uiState.copy(
            modelUrlToAdd = "",
            modelUrls = uiState.modelUrls + url
        )
    }

    fun toggleUrlMenu() {
        if (uiState.modelUrls.isNotEmpty()) {
            uiState = uiState.copy(urlMenuExpanded = !uiState.urlMenuExpanded)
        }
    }

    fun dismissUrlMenu() {
        uiState = uiState.copy(urlMenuExpanded = false)
    }

    fun selectModelUrl(url: String) {
        uiState = uiState.copy(selectedModel = url)
    }
}

class ButkusAppViewModel(app: Application) : AndroidViewModel(app) {
    var butkusInitialized by mutableStateOf(false)
        private set

    val encode = EncodeViewModel()
    val decode = DecodeScreenModel()
    val settings = SettingsScreenModel()

    // Since this is an AndroidViewModel, we can access application context
    // It's probably worth figuring out if we can avoid this, since it goes
    // against recommended practices
    init {
        viewModelScope.launch(Dispatchers.IO) {
            Butkus.initialize(app.applicationContext)
            butkusInitialized = true
        }
    }
}