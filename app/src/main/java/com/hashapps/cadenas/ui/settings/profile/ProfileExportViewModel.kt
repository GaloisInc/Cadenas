package com.hashapps.cadenas.ui.settings.profile

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.ProfileRepository
import com.hashapps.cadenas.data.toQRCode
import io.github.g0dkar.qrcode.QRCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun QRCode.toByteArray(defaultDispatcher: CoroutineDispatcher = Dispatchers.Default): ByteArray =
    withContext(defaultDispatcher) {
        ByteArrayOutputStream()
            .also {
                render(margin = 25).writeImage(destination = it, quality = 50)
            }
            .toByteArray()
    }

suspend fun QRCode.toImageBitmap(defaultDispatcher: CoroutineDispatcher = Dispatchers.Default): ImageBitmap =
    withContext(defaultDispatcher) {
        val bytes = toByteArray()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
            .also { it.prepareToDraw() }
    }

/**
 * View model for the profile-exporting screen.
 */
class ProfileExportViewModel(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val profileExportArgs = ProfileExportArgs(savedStateHandle)

    var qrBitmap: ImageBitmap? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            qrBitmap = profileRepository.getProfileStream(profileExportArgs.profileId)
                .map { it.toQRCode().toImageBitmap() }
                .first()
        }
    }

    /**
     * Save the profile's QR code as an image.
     */
    fun saveQRBitmap() {
        viewModelScope.launch {
            profileRepository.saveQRBitmap(qrBitmap)
        }
    }
}