package com.hashapps.cadenas.data.channels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.hashapps.cadenas.data.models.Model

/**
 * A Cadenas messaging channel.
 *
 * Note that after channel creation, only the name and description may be
 * changed. This limitation exists to prevent shared channels from getting out
 * of sync - changing the key or prompt, for instance, will make it impossible
 * for parties to exchange messages.
 *
 * @property[id] Room-generated primary key uniquely identifying the channel
 * @property[name] The name associated with the channel (not necessarily unique)
 * @property[description] A brief description of the channel
 * @property[key] The secret key shared by communicating messengers
 * @property[prompt] Prompt text for the language model
 * @property[selectedModel] The model associated with the channel
 * this channel
 */
@Entity(
    tableName = "channel",
    foreignKeys = [
        ForeignKey(
            entity = Model::class,
            parentColumns = ["name"],
            childColumns = ["selectedModel"],
            onDelete = CASCADE,
        )
    ]
)
data class Channel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val key: String,
    val prompt: String,
    val selectedModel: String,
)

fun Channel.isValid(): Boolean {
    return name.isNotBlank() && description.isNotBlank() && key.isNotBlank() && prompt.isNotBlank() && selectedModel.isNotBlank()
}