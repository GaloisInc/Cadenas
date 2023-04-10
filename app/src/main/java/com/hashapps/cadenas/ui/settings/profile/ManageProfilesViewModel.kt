package com.hashapps.cadenas.ui.settings.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.profile.ProfileRepository
import com.hashapps.cadenas.data.SettingsRepository
import com.hashapps.cadenas.data.profile.Profile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageProfilesViewModel(
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val selectedProfile = settingsRepository.selectedProfile

    fun selectProfile(id: Int) {
        viewModelScope.launch {
            settingsRepository.saveSelectedProfile(id)
        }
    }

    val profiles = profileRepository.getAllProfilesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
        }
    }
}