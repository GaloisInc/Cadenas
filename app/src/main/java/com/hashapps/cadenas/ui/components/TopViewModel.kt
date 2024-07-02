package com.hashapps.cadenas.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.ChannelRepository
import kotlinx.coroutines.launch

open class TopViewModel(
    private val channelRepository: ChannelRepository,
) : ViewModel() {

    /**
     * Delete all channels
     */
    fun deleteAllChannels() {
        viewModelScope.launch {
            channelRepository.deleteAllChannels()
        }
    }

}