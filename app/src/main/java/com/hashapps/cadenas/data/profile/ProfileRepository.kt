package com.hashapps.cadenas.data.profile

import javax.crypto.KeyGenerator

class ProfileRepository(private val profileDao: ProfileDao) {
    suspend fun insertProfile(profile: Profile) = profileDao.insert(profile)
    suspend fun updateProfile(profile: Profile) = profileDao.update(profile)
    suspend fun deleteProfile(profile: Profile) = profileDao.delete(profile)

    suspend fun deleteProfilesForModel(model: String) = profileDao.deleteProfilesForModel(model)

    fun getProfileStream(id: Int) = profileDao.getProfile(id)
    fun getAllProfilesStream() = profileDao.getAllProfiles()

    private companion object {
        val KEYGEN: KeyGenerator = KeyGenerator.getInstance("AES").also { it.init(256) }
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }
    fun genKey() = KEYGEN.generateKey().encoded.toHex()
}