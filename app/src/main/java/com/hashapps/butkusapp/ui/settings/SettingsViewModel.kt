package com.hashapps.butkusapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SavedSettings(
    val secretKey: String = "",
    val seedText: String = "",
    val selectedModel: String = "",
)
data class SettingsUiState(
    /** Model URL to add. */
    val modelUrlToAdd: String = "",

    /** Known model URIs to display in selection menu. */
    val modelUrls: Set<String> = setOf(),

    /** Flag indicating the URL menu is expanded. */
    val urlMenuExpanded: Boolean = false,
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _savedSettings = MutableStateFlow(SavedSettings())
    val savedSettings: StateFlow<SavedSettings>
        get() = _savedSettings

    init {
        viewModelScope.launch {
            _savedSettings.value = settingsRepository.settings.first()
        }
    }

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState>
        get() = _uiState

    fun genKey() {
        _savedSettings.update { it.copy(secretKey = settingsRepository.genKey()) }
    }

    fun updateSeedText(seed: String) {
        _savedSettings.update { it.copy(seedText = seed) }
    }

    fun updateModelToAdd(url: String) {
        _uiState.update { it.copy(modelUrlToAdd = url) }
    }

    fun addUrl(url: String) {
        _uiState.update {
            it.copy(
                modelUrlToAdd = "",
                modelUrls = uiState.value.modelUrls + url
            )
        }
    }

    fun toggleUrlMenu() {
        if (uiState.value.modelUrls.isNotEmpty()) {
            _uiState.update { it.copy(urlMenuExpanded = !uiState.value.urlMenuExpanded) }
        }
    }

    fun dismissUrlMenu() {
        _uiState.update { it.copy(urlMenuExpanded = false) }
    }

    fun selectModelUrl(url: String) {
        _savedSettings.update { it.copy(selectedModel = url) }
    }

    fun saveSettings() {
        viewModelScope.launch {
            settingsRepository.saveSettings(savedSettings.value)
        }
    }
}