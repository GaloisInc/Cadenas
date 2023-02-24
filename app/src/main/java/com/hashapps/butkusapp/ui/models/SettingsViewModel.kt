package com.hashapps.butkusapp.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hashapps.butkusapp.ui.SettingsUiState

class SettingsViewModel : ViewModel() {
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