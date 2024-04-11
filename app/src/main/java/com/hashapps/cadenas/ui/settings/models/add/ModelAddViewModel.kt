package com.hashapps.cadenas.ui.settings.models.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.models.ModelRepository
import com.hashapps.cadenas.ui.settings.models.ModelUiState
import com.hashapps.cadenas.ui.settings.models.isValid
import com.hashapps.cadenas.ui.settings.models.toModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
        modelUiState =
            newModelUiState.copy(actionEnabled = newModelUiState.isValid(modelNames.value))
    }

    val modelDownloaderState = modelRepository.modelDownloaderState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null,
    )

    val modelNames = modelRepository.getAllModelsStream().map { model ->
        model.map { it.name }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    /**
     * If a valid name and URL have been entered, start the model-downloading
     * worker.
     */
    fun downloadModel() {
        if (modelUiState.isValid(modelNames.value)) {
            modelRepository.downloadModelFromAndSaveAs(modelUiState.url, modelUiState.name)
        }
    }

    /**
     * Save a downloaded model to the database.
     */
    fun saveModel(hash: String) {
        viewModelScope.launch {
            modelRepository.insertModel(modelUiState.toModel(hash))
        }
    }
}