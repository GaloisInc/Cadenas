package com.hashapps.cadenas.data

import androidx.lifecycle.asFlow
import androidx.work.*
import com.hashapps.cadenas.data.model.Model
import com.hashapps.cadenas.data.model.ModelDao
import com.hashapps.cadenas.data.profile.Profile
import com.hashapps.cadenas.data.profile.ProfileDao
import com.hashapps.cadenas.workers.ModelDownloadWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.File
import javax.crypto.KeyGenerator

class ConfigRepository(
    private val internalStorage: File,
    private val workManager: WorkManager,
    private val modelDao: ModelDao,
    private val profileDao: ProfileDao,
) {
    val modelDownloaderState = workManager
        .getWorkInfosForUniqueWorkLiveData("downloadModel")
        .asFlow()
        .map { it.getOrNull(0) }

    fun fetchModel(model: Model) {
        val data = Data.Builder()

        data.putString(ModelDownloadWorker.KEY_MODEL_URL, model.url)

        val outDir = File(internalStorage, model.name)
        outDir.mkdirs()
        data.putString(ModelDownloadWorker.KEY_MODEL_DIR, outDir.path)

        val modelDownloadWordRequest = OneTimeWorkRequestBuilder<ModelDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(data.build())
            .build()

        workManager
            .enqueueUniqueWork(
                "downloadModel",
                ExistingWorkPolicy.KEEP,
                modelDownloadWordRequest,
            )
    }

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