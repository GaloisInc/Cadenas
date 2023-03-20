package com.hashapps.butkusapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.ButkusRepository
import com.hashapps.butkusapp.data.profile.ProfilesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ManageProfilesViewModel(
    private val profilesRepository: ProfilesRepository,
    private val butkusRepository: ButkusRepository,
) : ViewModel() {
    val selectedProfileId = butkusRepository.selectedProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null,
    )

    fun selectProfile(selectedProfileId: Int) {
        viewModelScope.launch {
            butkusRepository.saveSelectedProfile(selectedProfileId)
        }
    }

    val profiles = profilesRepository.getAllProfilesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    fun deleteProfile(selectedProfileId: Int) {
        viewModelScope.launch {
            val selectedProfile = profilesRepository.getProfileStream(selectedProfileId).first()
            profilesRepository.deleteProfile(selectedProfile)
        }
    }
}