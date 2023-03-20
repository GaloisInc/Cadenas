package com.hashapps.butkusapp.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.model.ModelsRepository
import com.hashapps.butkusapp.data.profile.ProfilesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.crypto.KeyGenerator

class ProfileEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val profilesRepository: ProfilesRepository,
    private val modelsRepository: ModelsRepository,
) : ViewModel() {
    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    val models = modelsRepository.getAllModelNamesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    private val itemId: Int = checkNotNull(savedStateHandle[ProfileEditDestination.profileIdArg])

    init {
        viewModelScope.launch {
            val profile = profilesRepository.getProfileStream(itemId)
                .filterNotNull()
                .first()
            val selectedModel = modelsRepository
                .getModelNameStream(profile.selectedModel)
                .filterNotNull()
                .first()
            profileUiState = profile.toProfileUiState(selectedModel, actionEnabled = true)
        }
    }

    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    fun updateProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                val selectedModel = modelsRepository
                    .getModelIdStream(profileUiState.selectedModel)
                    .filterNotNull()
                    .first()
                profilesRepository.updateProfile(profileUiState.toProfile(selectedModel))
            }
        }
    }

    private companion object {
        val KEYGEN: KeyGenerator = KeyGenerator.getInstance("AES").also { it.init(256) }
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }
    fun genKey() {
        profileUiState = profileUiState.copy(key = KEYGEN.generateKey().encoded.toHex())
    }
}