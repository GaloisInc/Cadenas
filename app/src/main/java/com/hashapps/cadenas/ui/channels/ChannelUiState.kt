package com.hashapps.cadenas.ui.channels

import com.hashapps.cadenas.data.channels.Channel

/**
 * UI state for channel screens.
 *
 * This class exists in (nearly) 1-to-1 correspondence with [Channel], as it is
 * merely the UI state holder for that same data.
 *
 * @property[id] The unique ID of the channel (determined by the corresponding
 * [Channel])
 * @property[name] The name of the channel
 * @property[description] A brief description of the channel
 * @property[key] The secret key shared by messengers using this channel
 * @property[prompt] Prompt text for the language model
 * @property[selectedModel] The model associated with the channel
 * this channel
 * @property[actionEnabled] Whether or not the save action is enabled
 * @property[cachingTimeMS] Num milliseconds keep cache of messages for this channel, 0 = no caching enabled
 */
data class ChannelUiState(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val key: String = "",
    val prompt: String = "",
    val selectedModel: String = "",
    val actionEnabled: Boolean = false,
    val cachingTimeMS: Int = 0, //0 = no caching enabled
)

/**
 * Convert to a [Channel] to be added/updated/removed from the database.
 */
fun ChannelUiState.toChannel(): Channel = Channel(
    id = id,
    name = name,
    description = description,
    key = key,
    prompt = prompt,
    selectedModel = selectedModel,
    cachingTimeMS = cachingTimeMS,
)

/**
 * Convert a [Channel] to [ChannelUiState] to properly display data stored in
 * the database.
 */
fun Channel.toChannelUiState(
    actionEnabled: Boolean = false
): ChannelUiState = ChannelUiState(
    id = id,
    name = name,
    description = description,
    key = key,
    prompt = prompt,
    selectedModel = selectedModel,
    actionEnabled = actionEnabled,
    cachingTimeMS = cachingTimeMS,
)

/**
 * Return true iff all required fields are non-blank.
 */
fun ChannelUiState.isValid() =
    name.isNotBlank() && description.isNotBlank() && key.isNotBlank() && prompt.isNotBlank() && selectedModel.isNotBlank()