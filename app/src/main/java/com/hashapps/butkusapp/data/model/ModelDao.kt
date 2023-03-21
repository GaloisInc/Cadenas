package com.hashapps.butkusapp.data.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(model: Model)

    @Delete
    suspend fun delete(model: Model)

    @Query("SELECT * FROM models ORDER BY name ASC")
    fun getAllModels(): Flow<List<Model>>
}