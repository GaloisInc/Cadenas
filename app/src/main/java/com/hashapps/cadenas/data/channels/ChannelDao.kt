package com.hashapps.cadenas.data.channels

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [Channel]s.
 *
 * Room-required interface to perform queries on the table of [Channel]s.
 */
@Dao
interface ChannelDao {
    /**
     * Insert a [Channel] into the database.
     *
     * @param[channel] The channel to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(channel: Channel): Long

    /**
     * Update a [Channel] in the database.
     *
     * @param[channel] The channel to update
     */
    @Update
    suspend fun update(channel: Channel)

    /**
     * Delete a [Channel] from the database.
     *
     * @param[channel] The channel to delete
     */
    @Delete
    suspend fun delete(channel: Channel)

    @Query("DELETE FROM channel")
    suspend fun deleteAll()

    /**
     * Emit the [Channel] with a given ID.
     *
     * @param[id] The ID of the channel to emit
     * @return A cold flow of [Channel] with the given ID
     */
    @Query("SELECT * FROM channel WHERE id = :id")
    fun getChannel(id: Long): Flow<Channel>

    /**
     * Emit all [Channel]s in the database.
     *
     * @return A cold flow of [List]<[Channel]>, ordered by name
     */
    @Query("SELECT * FROM channel ORDER BY name ASC")
    fun getAllChannels(): Flow<List<Channel>>
}