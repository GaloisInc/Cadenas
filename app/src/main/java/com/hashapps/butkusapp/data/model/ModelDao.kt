package com.hashapps.butkusapp.data.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(model: Model)

    @Update
    suspend fun update(model: Model)

    @Delete
    suspend fun delete(model: Model)

    @Query("SELECT * FROM models WHERE id = :id")
    fun getModel(id: Int): Flow<Model>

    @Query("SELECT id FROM models WHERE name = :name")
    fun getModelId(name: String): Flow<Int>

    @Query("SELECT name FROM models WHERE id = :id")
    fun getModelName(id: Int): Flow<String>

    @Query("SELECT uri FROM models WHERE id = :id")
    fun getModelUri(id: Int): Flow<String>

    @Query("SELECT * FROM models ORDER BY name ASC")
    fun getAllModels(): Flow<List<Model>>

    @Query("SELECT name FROM models ORDER BY name ASC")
    fun getAllModelNames(): Flow<List<String>>
}