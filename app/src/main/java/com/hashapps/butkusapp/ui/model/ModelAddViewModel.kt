package com.hashapps.butkusapp.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.model.ModelsRepository
import kotlinx.coroutines.launch

class ModelAddViewModel(
    private val modelsRepository: ModelsRepository,
) : ViewModel() {
    var modelUiState: ModelUiState by mutableStateOf(ModelUiState())
        private set

    fun updateUiState(newModelUiState: ModelUiState) {
        modelUiState = newModelUiState.copy(actionEnabled = newModelUiState.isValid())
    }

    // TODO: This just adds to the DB, but should try fetching the model from
    // the given URL and reporting any relevant failures.
    fun saveModel() {
        viewModelScope.launch {
            if (modelUiState.isValid()) {
                modelsRepository.insertModel(modelUiState.toModel())
            }
        }
    }
}