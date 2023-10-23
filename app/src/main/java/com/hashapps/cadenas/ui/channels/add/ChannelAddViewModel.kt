package com.hashapps.cadenas.ui.channels.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ChannelRepository
import com.hashapps.cadenas.data.ModelRepository
import com.hashapps.cadenas.ui.channels.ChannelUiState
import com.hashapps.cadenas.ui.channels.isValid
import com.hashapps.cadenas.ui.channels.toChannel
import kotlinx.coroutines.launch

/**
 * View model for the channel-add screen.
 *
 * @property[channelUiState] The UI state
 * @property[availableModels] The list of all downloaded models
 */
class ChannelAddViewModel(
    private val channelRepository: ChannelRepository,
    modelRepository: ModelRepository,
) : ViewModel() {
    var channelUiState by mutableStateOf(ChannelUiState(key = channelRepository.genKey()))
        private set

    /**
     * Update the channel-add screen UI state, only enabling the save button if
     * the new state is valid.
     */
    fun updateUiState(newChannelUiState: ChannelUiState) {
        channelUiState = newChannelUiState.copy(actionEnabled = newChannelUiState.isValid())
    }

    val availableModels = modelRepository.downloadedModels()

    /**
     * If valid, add the channel to the database.
     */
    fun saveChannel() {
        viewModelScope.launch {
            if (channelUiState.isValid()) {
                channelRepository.insertChannel(channelUiState.toChannel())
            }
        }
    }
}