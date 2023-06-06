package com.hashapps.cadenas.data

import kotlinx.coroutines.flow.Flow
import javax.crypto.KeyGenerator

/**
 * Repository class for Cadenas messaging profiles.
 *
 * Loosely wraps around the [ProfileDao] methods, and provides key-generation
 * capabilities for profile creation.
 */
class ProfileRepository(private val profileDao: ProfileDao) {
    suspend fun insertProfile(profile: Profile): Unit = profileDao.insert(profile)
    suspend fun updateProfile(profile: Profile): Unit = profileDao.update(profile)
    suspend fun deleteProfile(profile: Profile): Unit = profileDao.delete(profile)

    suspend fun deleteProfilesForModel(model: String): Unit = profileDao.deleteProfilesForModel(model)

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
}