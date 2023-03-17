package com.hashapps.butkusapp.data.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val description: String,

    val key: String,

    val seed: String,

    @ColumnInfo(name = "model_id")
    val selectedModel: Int,

    val tag: String,
)