package com.hashapps.cadenas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A Cadenas messaging profile.
 *
 * Note that after profile creation, only the name and description may be
 * changed. This limitation exists to prevent shared profiles from getting out
 * of sync - changing the key or seed, for instance, will prevent communicating
 * parties from being able to share messages in any meaningful way.
 *
 * @property[id] Room-generated primary key uniquely identifying the profile
 * @property[name] The name associated with the profile (not necessarily unique)
 * @property[description] A brief description of the profile
 * @property[key] The secret key shared by communicating messengers
 * @property[seed] Seed text for the language model
 * @property[selectedModel] The model associated with the profile
 * @property[tag] The hashtag (without '#') to append to messages encoded using
 * this profile
 */
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