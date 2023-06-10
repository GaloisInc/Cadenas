package com.hashapps.cadenas.ui.settings.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ProfileRepository
import com.hashapps.cadenas.data.SettingsRepository
import com.hashapps.cadenas.data.Profile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * View model for the profile-management screen.
 *
 * @property[selectedProfile] The currently-selected messaging profile
 * @property[profiles] The list of all saved profiles
 */
class ManageProfilesViewModel(
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val selectedProfile = settingsRepository.selectedProfile

    /**
     * Select a messaging profile from the database.
     *
     * @param[id] The ID of the profile to select
     */
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

    /**
     * Remove a profile from the database.
     *
     * @param[profile] The profile to delete from the database
     */
    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
        }
    }
}