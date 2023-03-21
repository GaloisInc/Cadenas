package com.hashapps.butkusapp.ui.model.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.model.ModelsRepository
import com.hashapps.butkusapp.data.model.profile.ProfilesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.crypto.KeyGenerator

class ProfileEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val profilesRepository: ProfilesRepository,
) : ViewModel() {
    private val modelId: Int = checkNotNull(savedStateHandle[ProfileEntryDestination.modelIdArg])

    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    fun saveProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                profilesRepository.insertProfile(profileUiState.toProfile(modelId))
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