package com.hashapps.butkusapp.data.model.profile

import kotlinx.coroutines.flow.Flow

interface ProfilesRepository {
    fun getAllProfilesForModel(modelId: Int): Flow<List<Profile>>

    fun getProfileStream(id: Int): Flow<Profile>

    suspend fun insertProfile(profile: Profile)

    suspend fun deleteProfile(profile: Profile)

    suspend fun updateProfile(profile: Profile)
}