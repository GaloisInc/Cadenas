package com.hashapps.cadenas.data

import androidx.work.*
import com.hashapps.cadenas.data.model.Model
import com.hashapps.cadenas.data.model.ModelDao
import com.hashapps.cadenas.data.profile.Profile
import com.hashapps.cadenas.data.profile.ProfileDao
import java.io.File
import javax.crypto.KeyGenerator

class ConfigRepository(
    private val internalStorage: File,
    private val workManager: WorkManager,
    private val modelDao: ModelDao,
    private val profileDao: ProfileDao,
) {
    suspend fun insertModel(model: Model) = modelDao.insert(model)
    suspend fun deleteModel(model: Model) = modelDao.delete(model)
    fun getModelNameStream(id: Int) = modelDao.getModelName(id)
    fun getAllModelsStream() = modelDao.getAllModels()

    suspend fun insertProfile(profile: Profile) = profileDao.insert(profile)
    suspend fun updateProfile(profile: Profile) = profileDao.update(profile)
    suspend fun deleteProfile(profile: Profile) = profileDao.delete(profile)
    fun getProfileStream(id: Int) = profileDao.getProfile(id)
    fun getAllProfilesForModel(modelId: Int) = profileDao.getAllProfilesForModel(modelId)

    private companion object {
        val KEYGEN: KeyGenerator = KeyGenerator.getInstance("AES").also { it.init(256) }
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }
    fun genKey() = KEYGEN.generateKey().encoded.toHex()
}