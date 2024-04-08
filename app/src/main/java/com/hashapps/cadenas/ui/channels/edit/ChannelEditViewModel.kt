package com.hashapps.cadenas.ui.channels.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.OfflineChannelRepository
import com.hashapps.cadenas.data.channels.isValid
import com.hashapps.cadenas.ui.channels.ChannelUiState
import com.hashapps.cadenas.ui.channels.isValid
import com.hashapps.cadenas.ui.channels.toChannel
import com.hashapps.cadenas.ui.channels.toChannelUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * View model for the channel-editing screen.
 *
 * @property[channelUiState] The UI state
 */
class ChannelEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val offlineChannelRepository: OfflineChannelRepository,
) : ViewModel() {
    private val channelEditArgs = ChannelEditArgs(savedStateHandle)

    var channelUiState by mutableStateOf(ChannelUiState())
        private set

    init {
        viewModelScope.launch {
            val channel = offlineChannelRepository.getChannelStream(channelEditArgs.channelId)
                .filterNotNull()
                .first()
            channelUiState = channel.toChannelUiState(actionEnabled = channel.isValid())
        }
    }

    /**
     * Update the channel-editing screen UI state, only enabling the save
     * button if the new state is valid.
     */
    fun updateUiState(newChannelUiState: ChannelUiState) {
        channelUiState = newChannelUiState.copy(actionEnabled = newChannelUiState.isValid())
    }

    /**
     * If a valid name and description have been entered, save the changes to
     * the channel to the database.
     */
    fun updateChannel() {
        viewModelScope.launch {
            if (channelUiState.isValid()) {
                offlineChannelRepository.updateChannel(channelUiState.toChannel())
            }
        }
    }
}