package com.hashapps.cadenas.ui.settings.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.Profile
import com.hashapps.cadenas.data.ProfileRepository
import kotlinx.coroutines.launch

class ProfileImportViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    fun saveProfileAndGoToEdit(profile: Profile, navigateToEdit: (Int) -> Unit) {
        viewModelScope.launch {
            navigateToEdit(profileRepository.insertProfile(profile).toInt())
        }
    }
}