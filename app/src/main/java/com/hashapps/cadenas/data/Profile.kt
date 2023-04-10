package com.hashapps.cadenas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val description: String,

    val key: String,

    val seed: String,

    val selectedModel: String,

    val tag: String,
)