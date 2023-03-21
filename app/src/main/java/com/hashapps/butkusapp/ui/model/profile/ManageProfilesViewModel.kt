package com.hashapps.butkusapp.ui.model.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.ButkusRepository
import com.hashapps.butkusapp.data.model.profile.Profile
import com.hashapps.butkusapp.data.model.profile.ProfilesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageProfilesViewModel(
    savedStateHandle: SavedStateHandle,
    private val profilesRepository: ProfilesRepository,
    private val butkusRepository: ButkusRepository,
) : ViewModel() {
    val modelId: Int = checkNotNull(savedStateHandle[ManageProfilesDestination.modelIdArg])

    val selectedProfileId = butkusRepository.selectedProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null,
    )

    fun selectProfile(id: Int) {
        viewModelScope.launch {
            butkusRepository.saveSelectedProfile(id)
        }
    }

    val profiles = profilesRepository.getAllProfilesForModel(modelId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            profilesRepository.deleteProfile(profile)
        }
    }
}