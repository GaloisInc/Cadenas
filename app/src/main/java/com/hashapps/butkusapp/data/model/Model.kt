package com.hashapps.butkusapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class Model(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val description: String,

    val url: String,
)
