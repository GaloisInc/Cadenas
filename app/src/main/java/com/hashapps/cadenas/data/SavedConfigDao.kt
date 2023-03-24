package com.hashapps.cadenas.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class SavedConfig(
    val modelId: Int,
    val modelDir: String,
    val key: String,
    val seed: String,
)

@Dao
interface SavedConfigDao {
    @Query(
        "SELECT models.id AS modelId, models.name AS modelDir, profiles.key, profiles.seed " +
        "FROM models, profiles " +
        "WHERE profiles.id = :id AND profiles.model_id = models.id"
    )
    fun getConfig(id: Int): Flow<SavedConfig>
}