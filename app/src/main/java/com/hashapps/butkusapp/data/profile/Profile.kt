package com.hashapps.butkusapp.data.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.hashapps.butkusapp.data.model.Model

@Entity(
    tableName = "profiles",
    foreignKeys = [
        ForeignKey(
            entity = Model::class,
            parentColumns = ["id"],
            childColumns = ["model_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class Profile (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val description: String,

    val key: String,

    val seed: String,

    @ColumnInfo(name = "model_id", index = true)
    val selectedModel: Int,

    val tag: String,
)