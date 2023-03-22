package com.hashapps.cadenas.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ConfigRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ModelEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val configRepository: ConfigRepository,
) : ViewModel() {
    private val modelId: Int = checkNotNull(savedStateHandle[ModelEditDestination.modelIdArg])

    var modelUiState by mutableStateOf(ModelUiState())
        private set

    init {
        viewModelScope.launch {
            val model = configRepository.getModelStream(modelId)
                .filterNotNull()
                .first()
            modelUiState = model.toModelUiState(actionEnabled = true)
        }
    }

    fun updateUiState(newModelUiState: ModelUiState) {
        modelUiState = newModelUiState.copy(actionEnabled = newModelUiState.isValid())
    }

    fun updateModel() {
        viewModelScope.launch {
            if (modelUiState.isValid()) {
                configRepository.updateModel(modelUiState.toModel())
            }
        }
    }
}