package com.hashapps.cadenas.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.SettingsRepository
import com.hashapps.cadenas.data.model.Model
import com.hashapps.cadenas.data.model.ModelsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageModelsViewModel(
    private val modelsRepository: ModelsRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val selectedModelId = settingsRepository.selectedModel

    val models = modelsRepository.getAllModelsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    fun deleteModel(model: Model) {
        viewModelScope.launch {
            modelsRepository.deleteModel(model)
        }
    }
}