package com.hashapps.cadenas.ui.settings.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.SettingsRepository
import com.hashapps.cadenas.domain.ManageModelsUseCase
import kotlinx.coroutines.launch

class ManageModelsViewModel(
    private val manageModelsUseCase: ManageModelsUseCase,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val selectedProfile = settingsRepository.selectedProfile

    var availableModels: List<String> by mutableStateOf(manageModelsUseCase())
        private set

    fun deleteModel(model: String) {
        viewModelScope.launch {
            availableModels = manageModelsUseCase(model)
        }
    }
}