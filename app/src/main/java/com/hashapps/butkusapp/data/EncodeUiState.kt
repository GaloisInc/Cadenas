package com.hashapps.butkusapp.data

/**
 * Data class for the encode screen's UI state.
 */
data class EncodeUiState (
    /** Whether the drawer can be opened. */
    val canOpenDrawer: Boolean = true,

    /** Whether the user can share to external apps. */
    val canShare: Boolean = false,

    /** The message to encode. */
    val message: String = "",

    /** The to-be-added tag. */
    val tagToAdd: String = "",

    /** The tags to append to the encoded message. */
    val addedTags: Set<String> = setOf(),

    /** The encoded message. */
    val encodedMessage: String? = null,
)