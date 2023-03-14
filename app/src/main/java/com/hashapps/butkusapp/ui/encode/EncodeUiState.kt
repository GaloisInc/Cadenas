package com.hashapps.butkusapp.ui.encode

data class EncodeUiState(
    /** The message to encode. */
    val message: String = "",

    /** The to-be-added tag. */
    val tagToAdd: String = "",

    /** The tags to append to the encoded message. */
    val addedTags: Set<String> = setOf("modnargathsah"),

    /** The encoded message. */
    val encodedMessage: String? = null,

    /** Flag indicating encoding is in-progress.  */
    val inProgress: Boolean = false,
)