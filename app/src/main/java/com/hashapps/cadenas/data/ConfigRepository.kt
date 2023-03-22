package com.hashapps.cadenas.data

import com.hashapps.cadenas.data.model.Model
import com.hashapps.cadenas.data.model.ModelDao
import com.hashapps.cadenas.data.profile.Profile
import com.hashapps.cadenas.data.profile.ProfileDao

class ConfigRepository (
    private val modelDao: ModelDao,
    private val profileDao: ProfileDao,
) {
    suspend fun insertModel(model: Model) = modelDao.insert(model)
    suspend fun updateModel(model: Model) = modelDao.update(model)
    suspend fun deleteModel(model: Model) = modelDao.delete(model)
    fun getModelStream(id: Int) = modelDao.getModel(id)
    fun getModelNameStream(id: Int) = modelDao.getModelName(id)
    fun getAllModelsStream() = modelDao.getAllModels()

    suspend fun insertProfile(profile: Profile) = profileDao.insert(profile)
    suspend fun updateProfile(profile: Profile) = profileDao.update(profile)
    suspend fun deleteProfile(profile: Profile) = profileDao.delete(profile)
    fun getProfileStream(id: Int) = profileDao.getProfile(id)
    fun getAllProfilesForModel(modelId: Int) = profileDao.getAllProfilesForModel(modelId)
}