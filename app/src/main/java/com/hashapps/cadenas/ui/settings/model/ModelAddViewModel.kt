package com.hashapps.cadenas.ui.settings.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ModelRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * View model for the model-add screen.
 *
 * @property[modelUiState] The UI state
 * @property[modelDownloaderState] The state of the model-downloading worker
 */
class ModelAddViewModel(
    private val modelRepository: ModelRepository,
) : ViewModel() {
    var modelUiState: ModelUiState by mutableStateOf(ModelUiState())
        private set

    /**
     * Update the model-add screen UI state, only enabling the download button
     * if the new state is valid.
     */
    fun updateUiState(newModelUiState: ModelUiState) {
        modelUiState = newModelUiState.copy(actionEnabled = newModelUiState.isValid())
    }

    val modelDownloaderState = modelRepository.modelDownloaderState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null,
    )

    /**
     * If a valid name and URL have been entered, start the model-downloading
     * worker.
     */
    fun downloadModel() {
        if (modelUiState.isValid()) {
            modelRepository.downloadModelFromAndSaveAs(modelUiState.url, modelUiState.name)
        }
    }
}