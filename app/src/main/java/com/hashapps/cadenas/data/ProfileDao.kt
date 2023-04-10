package com.hashapps.cadenas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(profile: Profile)

    @Update
    suspend fun update(profile: Profile)

    @Delete
    suspend fun delete(profile: Profile)

    @Query("DELETE FROM profiles WHERE selectedModel = :model")
    suspend fun deleteProfilesForModel(model: String)

    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getProfile(id: Int): Flow<Profile>

    @Query("SELECT * FROM profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<Profile>>
}