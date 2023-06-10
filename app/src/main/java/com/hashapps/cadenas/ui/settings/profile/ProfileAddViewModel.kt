package com.hashapps.cadenas.ui.settings.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ProfileRepository
import com.hashapps.cadenas.data.ModelRepository
import kotlinx.coroutines.launch

/**
 * View model for the profile-add screen.
 *
 * @property[profileUiState] The UI state
 * @property[availableModels] The list of all downloaded models
 */
class ProfileAddViewModel(
    private val profileRepository: ProfileRepository,
    modelRepository: ModelRepository,
) : ViewModel() {
    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    /**
     * Update the profile-add screen UI state, only enabling the save button if
     * the new state is valid.
     */
    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    val availableModels = modelRepository.downloadedModels()

    /**
     * If valid, add the profile to the database.
     */
    fun saveProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                profileRepository.insertProfile(profileUiState.toProfile())
            }
        }
    }

    /**
     * Generate and populate the UI with an AES-256 key as ASCII-hex.
     */
    fun genKey() {
        profileUiState = profileUiState.copy(key = profileRepository.genKey())
    }
}