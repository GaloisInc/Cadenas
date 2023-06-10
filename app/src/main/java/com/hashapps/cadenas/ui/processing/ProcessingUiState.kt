package com.hashapps.cadenas.ui.processing

/**
 * UI state for a message-processing screen.
 *
 * @property[toProcess] The string to be encoded/decoded
 * @property[result] A nullable string giving the processing result
 * @property[inProgress] Whether or not processing is in-progress
 */
data class ProcessingUiState(
    val toProcess: String = "",
    val result: String? = null,
    val inProgress: Boolean = false,
)