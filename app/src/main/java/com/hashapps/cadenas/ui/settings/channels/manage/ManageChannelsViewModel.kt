package com.hashapps.cadenas.ui.settings.channels.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ChannelRepository
import com.hashapps.cadenas.data.SettingsRepository
import com.hashapps.cadenas.data.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * View model for the channel-management screen.
 *
 * @property[selectedChannel] The currently-selected messaging channel
 * @property[channels] The list of all saved channels
 */
class ManageChannelsViewModel(
    private val channelRepository: ChannelRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val selectedChannel = settingsRepository.selectedChannel

    /**
     * Select a messaging channel from the database.
     *
     * @param[id] The ID of the channel to select
     */
    fun selectChannel(id: Int) {
        viewModelScope.launch {
            settingsRepository.saveSelectedChannel(id)
        }
    }

    val channels = channelRepository.getAllChannelsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    /**
     * Remove a channel from the database.
     *
     * @param[channel] The channel to delete from the database
     */
    fun deleteChannel(channel: Channel) {
        viewModelScope.launch {
            channelRepository.deleteChannel(channel)
        }
    }
}