package com.hashapps.butkusapp.data.profile

import kotlinx.coroutines.flow.Flow

class OfflineProfilesRepository(private val profileDao: ProfileDao) : ProfilesRepository {
    override fun getAllProfilesForModel(modelId: Int) = profileDao.getAllProfilesForModel(modelId)

    override fun getAllProfilesStream() = profileDao.getAllProfiles()

    override fun getProfileStream(id: Int) = profileDao.getProfile(id)

    override fun getModelForProfileStream(id: Int) = profileDao.getModelForProfile(id)

    override suspend fun insertProfile(profile: Profile) = profileDao.insert(profile)

    override suspend fun deleteProfile(profile: Profile) = profileDao.delete(profile)

    override suspend fun updateProfile(profile: Profile) = profileDao.update(profile)
}