package com.hashapps.cadenas.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.Channel
import com.hashapps.cadenas.data.channels.ChannelRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val channelRepository: ChannelRepository,
) : ViewModel() {
    val channels = channelRepository.getAllChannelsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = listOf(),
    )

    /**
     * Delete a channel.
     *
     * @param[channel] The channel to delete
     */
    fun deleteChannel(channel: Channel) {
        viewModelScope.launch {
            channelRepository.deleteChannel(channel)
        }
    }
}