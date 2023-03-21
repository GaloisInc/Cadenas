package com.hashapps.butkusapp.data.profile

import kotlinx.coroutines.flow.Flow

interface ProfilesRepository {
    fun getAllProfilesForModel(modelId: Int): Flow<List<Profile>>
    fun getAllProfilesStream(): Flow<List<Profile>>

    fun getProfileStream(id: Int): Flow<Profile>

    fun getModelForProfileStream(id: Int): Flow<Int>

    suspend fun insertProfile(profile: Profile)

    suspend fun deleteProfile(profile: Profile)

    suspend fun updateProfile(profile: Profile)
}