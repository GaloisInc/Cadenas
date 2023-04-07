package com.hashapps.cadenas.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.profile.ProfileRepository
import com.hashapps.cadenas.data.ModelRepository
import kotlinx.coroutines.launch

class ProfileAddViewModel(
    private val profileRepository: ProfileRepository,
    modelRepository: ModelRepository,
) : ViewModel() {
    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    val availableModels = modelRepository.downloadedModels()

    fun saveProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                profileRepository.insertProfile(profileUiState.toProfile())
            }
        }
    }

    fun genKey() {
        profileUiState = profileUiState.copy(key = profileRepository.genKey())
    }
}