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

class ProfileEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val modelRepository: ModelRepository,
) : ViewModel() {
//    private val modelId: Int = checkNotNull(savedStateHandle[ProfileEditDestination.modelIdArg])
//    val modelName = configRepository.getModelNameStream(modelId).stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000L),
//        initialValue = "",
//    )

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

    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    val availableModels = modelRepository.downloadedModels()

    fun updateProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                profileRepository.updateProfile(profileUiState.toProfile())
            }
        }
    }

    fun genKey() {
        profileUiState = profileUiState.copy(key = profileRepository.genKey())
    }
}