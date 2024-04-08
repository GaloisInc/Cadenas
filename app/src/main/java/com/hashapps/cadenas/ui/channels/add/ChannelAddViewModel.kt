package com.hashapps.cadenas.ui.channels.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.OfflineChannelRepository
import com.hashapps.cadenas.data.models.ModelRepository
import com.hashapps.cadenas.ui.channels.ChannelUiState
import com.hashapps.cadenas.ui.channels.isValid
import com.hashapps.cadenas.ui.channels.toChannel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * View model for the channel-add screen.
 *
 * @property[channelUiState] The UI state
 * @property[models] The list of all downloaded models
 */
class ChannelAddViewModel(
    private val offlineChannelRepository: OfflineChannelRepository,
    modelRepository: ModelRepository,
) : ViewModel() {
    var channelUiState by mutableStateOf(ChannelUiState(key = offlineChannelRepository.genKey()))
        private set

    /**
     * Update the channel-add screen UI state, only enabling the save button if
     * the new state is valid.
     */
    fun updateUiState(newChannelUiState: ChannelUiState) {
        channelUiState = newChannelUiState.copy(actionEnabled = newChannelUiState.isValid())
    }

    val models = modelRepository.getAllModelsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    /**
     * If valid, add the channel to the database.
     */
    fun saveChannel() {
        viewModelScope.launch {
            if (channelUiState.isValid()) {
                offlineChannelRepository.insertChannel(channelUiState.toChannel())
            }
        }
    }
}