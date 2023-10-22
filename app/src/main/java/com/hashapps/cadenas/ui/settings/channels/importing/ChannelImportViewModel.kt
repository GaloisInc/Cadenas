package com.hashapps.cadenas.ui.settings.channels.importing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.Channel
import com.hashapps.cadenas.data.ChannelRepository
import kotlinx.coroutines.launch

class ChannelImportViewModel(
    private val channelRepository: ChannelRepository,
) : ViewModel() {
    fun saveChannelAndGoToEdit(channel: Channel, navigateToEdit: (Int) -> Unit) {
        viewModelScope.launch {
            navigateToEdit(channelRepository.insertChannel(channel).toInt())
        }
    }
}