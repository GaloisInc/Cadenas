package com.hashapps.cadenas.ui.channels.import

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.Channel
import com.hashapps.cadenas.data.channels.OfflineChannelRepository
import com.hashapps.cadenas.data.models.Model
import com.hashapps.cadenas.data.models.ModelRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChannelImportViewModel(
    private val offlineChannelRepository: OfflineChannelRepository,
    private val modelRepository: ModelRepository,
) : ViewModel() {
    var modelInQR: Model? by mutableStateOf(null)
        private set

    fun saveChannelAndGoToEdit(channel: Channel, navigateToEdit: (Long) -> Unit) {
        viewModelScope.launch {
            navigateToEdit(offlineChannelRepository.insertChannel(channel))
        }
    }

    fun getModelWithHash(hash: String) {
        viewModelScope.launch {
            modelInQR = modelRepository.getModelStreamWithHash(hash).first()
        }
    }
}