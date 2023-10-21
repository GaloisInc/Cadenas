package com.hashapps.cadenas.ui.settings.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ProfileRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * View model for the profile-exporting screen.
 */
class ProfileExportViewModel(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val profileExportArgs = ProfileExportArgs(savedStateHandle)

    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    init {
        viewModelScope.launch {
            val profile = profileRepository.getProfileStream(profileExportArgs.profileId)
                .filterNotNull()
                .first()
            profileUiState = profile.toProfileUiState(actionEnabled = true)
        }
    }

    /**
     * Save the profile's QR code as an image.
     */
    fun saveQRImage() {
        viewModelScope.launch {
            profileRepository.saveQRForProfile(profileUiState.toProfile())
        }
    }
}