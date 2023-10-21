package com.hashapps.cadenas.data

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import java.io.ByteArrayOutputStream
import javax.crypto.KeyGenerator

/**
 * Repository class for Cadenas messaging profiles.
 *
 * Loosely wraps around the [ProfileDao] methods, and provides key-generation
 * capabilities for profile creation.
 */
class ProfileRepository(
    private val contentResolver: ContentResolver,
    private val profileDao: ProfileDao
) {
    suspend fun insertProfile(profile: Profile): Unit = profileDao.insert(profile)
    suspend fun updateProfile(profile: Profile): Unit = profileDao.update(profile)
    suspend fun deleteProfile(profile: Profile): Unit = profileDao.delete(profile)

    suspend fun deleteProfilesForModel(model: String): Unit =
        profileDao.deleteProfilesForModel(model)

    fun getProfileStream(id: Int): Flow<Profile> = profileDao.getProfile(id)
    fun getAllProfilesStream(): Flow<List<Profile>> = profileDao.getAllProfiles()

    private companion object {
        val KEYGEN: KeyGenerator = KeyGenerator.getInstance("AES").also { it.init(256) }
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }

    /**
     * Generate and return an AES-256 key as ASCII-Hex.
     */
    fun genKey(): String = KEYGEN.generateKey().encoded.toHex()

    /**
     * Save a profile's QR code to disk.
     */
    fun saveQRForProfile(profile: Profile) {
        val qrCodeBytes = ByteArrayOutputStream()
            .also {
                profile.toQRCode().render()
                    .writeImage(destination = it, format = "WEBP", quality = 50)
            }
            .toByteArray()

        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val qrDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "qr-${profile.id}.webp")
        }
        val qrUri = contentResolver.insert(imageCollection, qrDetails)!!

        contentResolver.openOutputStream(qrUri).use {
            it?.write(qrCodeBytes)
        }
    }
}