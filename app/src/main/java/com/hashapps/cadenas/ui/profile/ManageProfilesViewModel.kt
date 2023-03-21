package com.hashapps.cadenas.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.CadenasRepository
import com.hashapps.cadenas.data.profile.Profile
import com.hashapps.cadenas.data.profile.ProfilesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageProfilesViewModel(
    savedStateHandle: SavedStateHandle,
    private val profilesRepository: ProfilesRepository,
    private val cadenasRepository: CadenasRepository,
) : ViewModel() {
    val modelId: Int = checkNotNull(savedStateHandle[ManageProfilesDestination.modelIdArg])

    val selectedProfileId = cadenasRepository.selectedProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null,
    )

    fun selectProfile(id: Int) {
        viewModelScope.launch {
            cadenasRepository.saveSelectedProfile(id)
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