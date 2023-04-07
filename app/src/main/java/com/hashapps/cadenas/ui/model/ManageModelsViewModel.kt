package com.hashapps.cadenas.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ModelRepository
import com.hashapps.cadenas.data.SettingsRepository
import com.hashapps.cadenas.data.profile.ProfileRepository
import kotlinx.coroutines.launch

class ManageModelsViewModel(
    private val modelRepository: ModelRepository,
    private val profileRepository: ProfileRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val selectedModel = settingsRepository.selectedModel

    var availableModels: List<String> by mutableStateOf(modelRepository.downloadedModels())
        private set

    fun deleteModel(model: String) {
        viewModelScope.launch {
            profileRepository.deleteProfilesForModel(model)
            modelRepository.deleteFilesForModel(model)
            availableModels = modelRepository.downloadedModels()
        }
    }
}