package com.hashapps.cadenas.ui.processing

/**
 * UI state for a message-processing screen.
 *
 * @property[toProcess] The string to be encoded/decoded
 * @property[actionEnabled] Whether or not the main action is enabled
 * @property[result] A nullable string giving the processing result
 * @property[inProgress] Whether or not processing is in-progress
 */
data class ProcessingUiState(
    val toProcess: String = "",
    val actionEnabled: Boolean = false,
    val result: String? = null,
    val inProgress: Boolean = false,
)

/**
 * Return true iff the input is not blank and no processing is in-progress.
 */
fun ProcessingUiState.isValid() = toProcess.isNotBlank() && !inProgress