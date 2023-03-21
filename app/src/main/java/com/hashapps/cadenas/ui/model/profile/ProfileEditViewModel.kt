package com.hashapps.cadenas.ui.model.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.model.ModelsRepository
import com.hashapps.cadenas.data.model.profile.ProfilesRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.crypto.KeyGenerator

class ProfileEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val profilesRepository: ProfilesRepository,
    private val modelsRepository: ModelsRepository,
) : ViewModel() {
    private val modelId: Int = checkNotNull(savedStateHandle[ProfileEditDestination.modelIdArg])
    private val itemId: Int = checkNotNull(savedStateHandle[ProfileEditDestination.profileIdArg])

    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    init {
        viewModelScope.launch {
            val profile = profilesRepository.getProfileStream(itemId)
                .filterNotNull()
                .first()
            profileUiState = profile.toProfileUiState(actionEnabled = true)
        }
    }

    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    fun updateProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                profilesRepository.updateProfile(profileUiState.toProfile(modelId))
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