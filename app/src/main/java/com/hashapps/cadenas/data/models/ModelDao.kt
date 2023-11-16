package com.hashapps.cadenas.data.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [Model]s.
 *
 * Room-required interface to perform queries on the table of [Model]s.
 */
@Dao
interface ModelDao {
    /**
     * Insert a [Model] into the database.
     *
     * @param[model] The model to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(model: Model): Long

    /**
     * Delete a [Model] from the database by name.
     *
     * @param[name] The name of the model to delete
     */
    @Query("DELETE FROM model WHERE name = :name")
    suspend fun delete(name: String)

    /**
     * Emit the [Model] with a given ID.
     *
     * @param[name] The name of the channel to emit
     * @return A cold flow of [Model] with the given name
     */
    @Query("SELECT * FROM model WHERE name = :name")
    fun getModel(name: String): Flow<Model>

    /**
     * Emit all [Model]s in the database.
     *
     * @return A cold flow of [List]<[Model]>, ordered by name
     */
    @Query("SELECT * FROM model ORDER BY name ASC")
    fun getAllModels(): Flow<List<Model>>
}