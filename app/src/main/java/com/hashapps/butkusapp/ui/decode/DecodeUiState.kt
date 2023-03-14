package com.hashapps.butkusapp.ui.decode

data class DecodeUiState (
    /** The message to decode. */
    val message: String = "",

    /** The decoded message. */
    val decodedMessage: String? = null,

    /** Flag indicating decoding is in-progress. */
    val inProgress: Boolean = false,
)