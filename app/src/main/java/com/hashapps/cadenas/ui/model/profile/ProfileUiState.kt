package com.hashapps.cadenas.ui.model.profile

import com.hashapps.cadenas.data.model.profile.Profile

data class ProfileUiState(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val key: String = "",
    val seed: String = "",
    val tag: String = "",
    val actionEnabled: Boolean = false,
)

fun ProfileUiState.toProfile(selectedModel: Int): Profile = Profile(
    id = id,
    name = name,
    description = description,
    key = key,
    seed = seed,
    selectedModel = selectedModel,
    tag = tag,
)

fun Profile.toProfileUiState(
    actionEnabled: Boolean = false
): ProfileUiState = ProfileUiState(
    id = id,
    name = name,
    description = description,
    key = key,
    seed = seed,
    tag = tag,
    actionEnabled = actionEnabled,
)

private val tagRegex = Regex("""\w*[a-zA-Z]\w*""")
fun ProfileUiState.isTagValid() = tag == "" || tagRegex.matches(tag)

fun ProfileUiState.isValid() =
    name.isNotBlank() && description.isNotBlank() && key.isNotBlank() && seed.isNotBlank() && isTagValid()


