package com.hashapps.cadenas.ui.settings.model

data class ModelUiState(
    val name: String = "",
    val url: String = "",
    val actionEnabled: Boolean = false,
)

val nameRegex =
    Regex("""[a-zA-Z\d]+""")

fun ModelUiState.isNameValid() = nameRegex.matches(name)

private val urlRegex =
    Regex("""https://(www\.)?[-a-zA-Z\d@:%._+~#=]{1,256}\.[a-zA-Z\d()]{1,6}\b([-a-zA-Z\d()!@:%_+.~#?&/=]*)""")

fun ModelUiState.isUrlValid() = urlRegex.matches(url)

fun ModelUiState.isValid() =
    isNameValid() && isUrlValid()