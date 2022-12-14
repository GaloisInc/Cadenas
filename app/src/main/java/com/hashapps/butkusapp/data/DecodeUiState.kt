package com.hashapps.butkusapp.data

/**
 * Data class for the decode screen's UI state.
 */
data class DecodeUiState (
    /** Whether the drawer can be opened. */
    val canOpenDrawer: Boolean = true,

    /** Whether the user can share to external apps. */
    val canShare: Boolean = false,

    /** The message to decode. */
    val message: String = "",

    /** The decoded message. */
    val decodedMessage: String? = null,
)