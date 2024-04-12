package com.hashapps.cadenas.ui.processing

/**
 * State indicating mode of operation (encode or decode).
 */
enum class ProcessingMode {
    Encode, Decode
}

/**
 * UI state for a message-processing screen.
 *
 * @property[channelName] The name of the channel
 * @property[toProcess] The string to be encoded/decoded
 * @property[result] A nullable string giving the processing result
 * @property[inProgress] Whether or not processing is in-progress
 * @property[showEditWarning] Whether or not to show the output-edit warning
 */
data class ProcessingUiState(
    val channelName: String = "",
    val toProcess: String = "",
    val processingMode: ProcessingMode = ProcessingMode.Encode,
    val result: String? = null,
    val inProgress: Boolean = false,
    val showEditWarning: Boolean = false,
)