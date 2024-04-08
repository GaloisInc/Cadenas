package com.hashapps.cadenas.ui.home

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.hashapps.cadenas.data.channels.Channel
import com.hashapps.cadenas.data.channels.ChannelRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    private val channelRepository: ChannelRepository,
) : ViewModel() {
    val sharedTextState =
        savedStateHandle.getStateFlow(NavController.KEY_DEEP_LINK_INTENT, Intent())
            .map {
                if (it.action != Intent.ACTION_SEND) {
                    return@map ""
                }

                if (it.type.equals("text/plain")) {
                    Uri.encode(it.getStringExtra(Intent.EXTRA_TEXT) ?: "")
                } else {
                    ""
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = ""
            )

    init {
        sharedTextState.launchIn(viewModelScope)
    }

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