package com.hashapps.cadenas.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ConfigRepository
import com.hashapps.cadenas.data.SettingsRepository
import com.hashapps.cadenas.data.profile.Profile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageProfilesViewModel(
    savedStateHandle: SavedStateHandle,
    private val configRepository: ConfigRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val modelId: Int = checkNotNull(savedStateHandle[ManageProfilesDestination.modelIdArg])

    val selectedProfileId = settingsRepository.selectedProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null,
    )

    fun selectProfile(id: Int) {
        viewModelScope.launch {
            settingsRepository.saveSelectedProfile(id)
        }
    }

    val profiles = configRepository.getAllProfilesForModel(modelId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            configRepository.deleteProfile(profile)
        }
    }
}