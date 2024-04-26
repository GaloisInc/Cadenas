package com.hashapps.cadenas.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A Cadenas language model.
 *
 * Note that after adding a model, nothing about it can be changed. This helps
 * guarantee that generated QR codes contain the appropriate hash, file paths
 * are predictable, etc.
 *
 * @property[name] The name associated with the model (must be unique)
 * @property[url] The URL this model was acquired from
 * @property[hash] An MD5 hash of the PTL file defining the model weights
 */
@Entity(tableName = "model")
data class Model(
    @PrimaryKey
    val name: String,
    val url: String,
    val hash: String,
)
