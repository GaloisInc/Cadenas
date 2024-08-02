package com.hashapps.cadenas.ui.channels.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.ChannelRepository
import com.hashapps.cadenas.data.models.ModelRepository
import com.hashapps.cadenas.ui.channels.ChannelUiState
import com.hashapps.cadenas.ui.channels.isValid
import com.hashapps.cadenas.ui.channels.toChannel
import com.hashapps.cadenas.ui.components.TopViewModel
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
    private val channelRepository: ChannelRepository,
    modelRepository: ModelRepository,
) : TopViewModel(modelRepository) {
    var channelUiState by mutableStateOf(ChannelUiState(key = channelRepository.genKey()))
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
                channelRepository.insertChannel(channelUiState.toChannel())
            }
        }
    }
}