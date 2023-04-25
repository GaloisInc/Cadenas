package com.hashapps.cadenas.ui.settings.model

/**
 * UI state for the model-adding screen.
 *
 * @property[name] The name of the model
 * @property[url] The model download URL
 * @property[actionEnabled] Whether or not the download action is enabled
 */
data class ModelUiState(
    val name: String = "",
    val url: String = "",
    val actionEnabled: Boolean = false,
)

private val nameRegex =
    Regex("""[a-zA-Z\d]+""")

/**
 * Return true iff the model name is alphanumeric.
 */
fun ModelUiState.isNameValid() = nameRegex.matches(name)

private val urlRegex =
    Regex("""https://(www\.)?[-a-zA-Z\d@:%._+~#=]{1,256}\.[a-zA-Z\d()]{1,6}\b([-a-zA-Z\d()!@:%_+.~#?&/=]*)""")

/**
 * Return true iff the model download URL is a valid HTTPS URL.
 * Regex credit: https://ihateregex.io/expr/url (with modification for mandatory HTTPS.)
 */
fun ModelUiState.isUrlValid() = urlRegex.matches(url)

/**
 * Return true iff the model name and URL are valid.
 */
fun ModelUiState.isValid() =
    isNameValid() && isUrlValid()