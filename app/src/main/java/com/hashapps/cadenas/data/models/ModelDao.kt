package com.hashapps.cadenas.data.models

import androidx.room.Dao
import androidx.room.Delete
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
     * Delete a [Model] from the database.
     */
    @Delete
    suspend fun delete(model: Model)

    /**
     * Emit the [Model] with a given ID.
     *
     * @param[name] The name of the channel to emit
     * @return A cold flow of [Model] with the given name
     */
    @Query("SELECT * FROM model WHERE name = :name")
    fun getModel(name: String): Flow<Model>

    /**
     * If it exists, emit the [Model] with a given hash.
     *
     * @param[hash] The hash to search for in the database
     * @return A close flow of [Model] with the given name, or null
     */
    @Query("SELECT * FROM model WHERE hash = :hash")
    fun getModelWithHash(hash: String): Flow<Model?>

    /**
     * Emit all [Model]s in the database.
     *
     * @return A cold flow of [List]<[Model]>, ordered by name
     */
    @Query("SELECT * FROM model ORDER BY name ASC")
    fun getAllModels(): Flow<List<Model>>
}