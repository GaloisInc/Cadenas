package com.hashapps.cadenas.ui.settings.channels

import com.hashapps.cadenas.data.Channel

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
 * @property[tag] The hashtag (without '#') to append to messages encoded using
 * this channel
 * @property[actionEnabled] Whether or not the save action is enabled
 */
data class ChannelUiState(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val key: String = "",
    val prompt: String = "",
    val selectedModel: String = "",
    val tag: String = "",
    val actionEnabled: Boolean = false,
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
    tag = tag,
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
    tag = tag,
    actionEnabled = actionEnabled,
)

private val tagRegex = Regex("""\w*[a-zA-Z]\w*""")

/**
 * Return true iff the hashtag is valid or empty.
 *
 * 'Valid' means alphanumeric or underscore, with at least one alphabetic
 * character.
 */
fun ChannelUiState.isTagValid() = tag == "" || tagRegex.matches(tag)

/**
 * Return true iff all required fields are non-blank and the hashtag is valid.
 */
fun ChannelUiState.isValid() =
    name.isNotBlank() && description.isNotBlank() && key.isNotBlank() && prompt.isNotBlank() && selectedModel.isNotBlank() && isTagValid()