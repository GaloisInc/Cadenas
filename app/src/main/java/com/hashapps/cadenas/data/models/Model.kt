package com.hashapps.cadenas.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A Cadenas language model.
 *
 * Note that after adding a model, nothing about it can be changed. This helps
 * guarantee that generated QR codes contain the appropriate hash, file paths
 * are predictable, etc.
 *
 * @property[id] Room-generated primary key uniquely identifying the model.
 * @property[name] The name associated with the model (must be unique)
 * @property[hash] An MD5 hash of the PTL file defining the model weights
 */
@Entity(tableName = "model")
data class Model(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val hash: String,
)
