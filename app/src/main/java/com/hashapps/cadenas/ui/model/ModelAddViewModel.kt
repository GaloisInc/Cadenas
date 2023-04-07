package com.hashapps.cadenas.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ModelRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ModelAddViewModel(
    private val modelRepository: ModelRepository,
) : ViewModel() {
    var modelUiState: ModelUiState by mutableStateOf(ModelUiState())
        private set

    fun updateUiState(newModelUiState: ModelUiState) {
        modelUiState = newModelUiState.copy(actionEnabled = newModelUiState.isValid())
    }

    val modelDownloaderState = modelRepository.modelDownloaderState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null,
    )

    fun downloadModel() {
        if (modelUiState.isValid()) {
            modelRepository.downloadModelFromAndSaveAs(modelUiState.url, modelUiState.name)
        }
    }
}