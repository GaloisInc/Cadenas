package com.hashapps.butkusapp.data.model.profile

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

    @Query("SELECT * FROM profiles WHERE model_id = :modelId")
    fun getAllProfilesForModel(modelId: Int): Flow<List<Profile>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getProfile(id: Int): Flow<Profile>
}