package com.hashapps.butkusapp.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.ButkusRepository
import com.hashapps.butkusapp.data.model.Model
import com.hashapps.butkusapp.data.model.ModelsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageModelsViewModel(
    private val modelsRepository: ModelsRepository,
    butkusRepository: ButkusRepository,
) : ViewModel() {
    val selectedModelId = butkusRepository.selectedModel

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