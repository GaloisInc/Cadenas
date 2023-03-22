package com.hashapps.cadenas.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ConfigRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.crypto.KeyGenerator

class ProfileAddViewModel(
    savedStateHandle: SavedStateHandle,
    private val configRepository: ConfigRepository,
) : ViewModel() {
    private val modelId: Int = checkNotNull(savedStateHandle[ProfileAddDestination.modelIdArg])
    val modelName = configRepository.getModelNameStream(modelId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = "",
    )

    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    fun updateUiState(newProfileUiState: ProfileUiState) {
        profileUiState = newProfileUiState.copy(actionEnabled = newProfileUiState.isValid())
    }

    fun saveProfile() {
        viewModelScope.launch {
            if (profileUiState.isValid()) {
                configRepository.insertProfile(profileUiState.toProfile(modelId))
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