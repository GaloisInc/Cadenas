package com.hashapps.butkusapp.ui

import java.util.Base64
import javax.crypto.KeyGenerator

/**
 * Data class for the setting screen's UI state.
 */
data class SettingsUiState (
    /** The secret key for encoding/decoding */
    val secret_key: String = "",

    /** Butkus seed text */
    val seed_text: String = "",

    /** Currently loaded model URI */
    val model_uri: String = "",

    /** Known model URIs to display in menu */
    val model_uris: Set<String> = setOf(),
)