package com.hashapps.cadenas.ui.processing

import com.hashapps.cadenas.ui.cache.Message

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
 * @property[channelCacheTimeInMS] The time this channel will keep messages in the cache
 * @property[cachedMessages] The messages being displayed from the message cache
 * @property[toProcess] The string to be encoded/decoded
 * @property[processingMode] Either Encode or Decode
 * @property[result] A nullable string giving the processing result
 * @property[inProgress] Whether or not processing is in-progress
 * @property[showEditWarning] Whether or not to show the output-edit warning
 * @property[updateBit] This is just a counter that changes every second to insure the UI is updated
 */
data class ProcessingUiState(
    val channelName: String = "",
    val channelCacheTimeInMS: Int = 0,
    val cachedMessages: List<Message> = emptyList<Message>(),
    val toProcess: String = "",
    val processingMode: ProcessingMode = ProcessingMode.Encode,
    val result: String? = null,
    val inProgress: Boolean = false,
    val showEditWarning: Boolean = false,
    val updateBit: Int = 0,
)