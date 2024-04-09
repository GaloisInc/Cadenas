package com.hashapps.cadenas.ui.settings.models.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.models.Model
import com.hashapps.cadenas.data.models.ModelRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * View model for the model-management screen.
 *
 * @property[models] The list of all downloaded models
 */
class ManageModelsViewModel(
    private val modelRepository: ModelRepository,
) : ViewModel() {
    val models = modelRepository.getAllModelsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    /**
     * Start the model-deleting worker for the given model name and update the
     * list of available models.
     *
     * @param[model] The name of the model to delete
     */
    fun deleteModel(model: Model) {
        viewModelScope.launch {
            modelRepository.deleteModel(model)
        }
    }
}