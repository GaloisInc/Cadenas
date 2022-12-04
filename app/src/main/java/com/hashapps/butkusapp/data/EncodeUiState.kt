package com.hashapps.butkusapp.data

/**
 * Data class for the encode screen's UI state.
 */
data class EncodeUiState (
    /** The number of characters entered in the message box. */
    val charactersEntered: Int = 0,

    /** The message to encode. */
    val message: String = "",

    /** The message tags. */
    val tags: List<String> = listOf(),

    /** The encoded message. */
    val encodedMessage: String = "",
)