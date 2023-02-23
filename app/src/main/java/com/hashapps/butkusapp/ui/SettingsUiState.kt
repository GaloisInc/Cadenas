package com.hashapps.butkusapp.ui

import java.util.Base64
import javax.crypto.KeyGenerator

/**
 * Data class for the setting screen's UI state.
 */
data class SettingsUiState (
    /** The secret key for encoding/decoding. */
    val secretKey: String = "",

    /** Butkus seed text. */
    val seedText: String = "",

    /** Model URL to add. */
    val modelUrlToAdd: String = "",

    /** Known model URIs to display in selection menu. */
    val modelUrls: Set<String> = setOf(),

    /** Selected model to fetch. */
    val selectedModel: String = "",
)