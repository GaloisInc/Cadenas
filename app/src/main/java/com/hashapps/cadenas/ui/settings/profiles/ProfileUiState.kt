package com.hashapps.cadenas.ui.settings.profiles

import com.hashapps.cadenas.data.Profile

/**
 * UI state for profile-add and profile-edit screens.
 *
 * This class exists in (nearly) 1-to-1 correspondence with [Profile], as it is
 * merely the UI state holder for that same data.
 *
 * @property[id] The unique ID of the profile (determined by the corresponding
 * [Profile])
 * @property[name] The name of the profile
 * @property[description] A brief description of the profile
 * @property[key] The secret key shared by messengers using this profile
 * @property[seed] Seed text for the language model
 * @property[selectedModel] The model associated with the profile
 * @property[tag] The hashtag (without '#') to append to messages encoded using
 * this profile
 * @property[actionEnabled] Whether or not the save action is enabled
 */
data class ProfileUiState(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val key: String = "",
    val seed: String = "",
    val selectedModel: String = "",
    val tag: String = "",
    val actionEnabled: Boolean = false,
)

/**
 * Convert to a [Profile] to be added/updated/removed from the database.
 */
fun ProfileUiState.toProfile(): Profile = Profile(
    id = id,
    name = name,
    description = description,
    key = key,
    seed = seed,
    selectedModel = selectedModel,
    tag = tag,
)

/**
 * Convert a [Profile] to [ProfileUiState] to properly display data stored in
 * the database.
 */
fun Profile.toProfileUiState(
    actionEnabled: Boolean = false
): ProfileUiState = ProfileUiState(
    id = id,
    name = name,
    description = description,
    key = key,
    seed = seed,
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
fun ProfileUiState.isTagValid() = tag == "" || tagRegex.matches(tag)

/**
 * Return true iff all required fields are non-blank and the hashtag is valid.
 */
fun ProfileUiState.isValid() =
    name.isNotBlank() && description.isNotBlank() && key.isNotBlank() && seed.isNotBlank() && selectedModel.isNotBlank() && isTagValid()