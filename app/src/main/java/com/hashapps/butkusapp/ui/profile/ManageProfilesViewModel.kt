package com.hashapps.butkusapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.ButkusRepository
import com.hashapps.butkusapp.data.profile.Profile
import com.hashapps.butkusapp.data.profile.ProfilesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ManageProfilesViewModel(
    profilesRepository: ProfilesRepository,
    private val butkusRepository: ButkusRepository,
) : ViewModel() {
    val selectedProfile = butkusRepository.selectedProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null,
    )

    fun selectProfile(selectedProfile: Int) {
        viewModelScope.launch {
            butkusRepository.saveSelectedProfile(selectedProfile)
        }
    }

    val profiles = profilesRepository.getAllProfilesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )
}