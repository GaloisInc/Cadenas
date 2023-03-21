package com.hashapps.butkusapp.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.data.model.ModelsRepository
import com.hashapps.butkusapp.data.profile.ProfilesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.crypto.KeyGenerator

class ProfileEntryViewModel(
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

    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    fun saveProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                val selectedModel = modelsRepository
                    .getModelIdStream(profileUiState.selectedModel)
                    .filterNotNull()
                    .first()
                profilesRepository.insertProfile(profileUiState.toProfile(selectedModel))
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