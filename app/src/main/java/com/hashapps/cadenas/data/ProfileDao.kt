package com.hashapps.cadenas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [Profile]s.
 *
 * Room-required interface to perform queries on the table of [Profile]s.
 */
@Dao
interface ProfileDao {
    /**
     * Insert a [Profile] into the database.
     *
     * @param[profile] The profile to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(profile: Profile): Long

    /**
     * Update a [Profile] in the database.
     *
     * @param[profile] The profile to update
     */
    @Update
    suspend fun update(profile: Profile)

    /**
     * Delete a [Profile] from the database.
     *
     * @param[profile] The profile to delete
     */
    @Delete
    suspend fun delete(profile: Profile)

    /**
     * Delete all [Profile]s associated with a given model.
     *
     * @param[model] The name of the model for which all associated profiles
     * should be deleted
     */
    @Query("DELETE FROM profiles WHERE selectedModel = :model")
    suspend fun deleteProfilesForModel(model: String)

    /**
     * Emit the [Profile] with a given ID.
     *
     * @param[id] The ID of the profile to emit
     * @return A cold flow of [Profile] with the given ID
     */
    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getProfile(id: Int): Flow<Profile>

    /**
     * Emit all [Profile]s in the database.
     *
     * @return A cold flow of [List]<[Profile]>, ordered by name
     */
    @Query("SELECT * FROM profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<Profile>>
}