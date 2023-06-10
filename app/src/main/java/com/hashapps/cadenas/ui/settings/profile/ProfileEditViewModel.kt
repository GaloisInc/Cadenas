package com.hashapps.cadenas.ui.settings.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ProfileRepository
import com.hashapps.cadenas.data.ModelRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * View model for the profile-editing screen.
 *
 * @property[profileUiState] The UI state
 * @property[availableModels] The list of all downloaded models
 */
class ProfileEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val modelRepository: ModelRepository,
) : ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[ProfileEditDestination.profileIdArg])

    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    init {
        viewModelScope.launch {
            val profile = profileRepository.getProfileStream(itemId)
                .filterNotNull()
                .first()
            profileUiState = profile.toProfileUiState(actionEnabled = true)
        }
    }

    /**
     * Update the profile-editing screen UI state, only enabling the save
     * button if the new state is valid.
     */
    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    val availableModels = modelRepository.downloadedModels()

    /**
     * If a valid name and description have been entered, save the changes to
     * the profile to the database.
     */
    fun updateProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                profileRepository.updateProfile(profileUiState.toProfile())
            }
        }
    }
}