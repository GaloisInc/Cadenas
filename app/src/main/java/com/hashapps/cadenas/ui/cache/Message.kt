package com.hashapps.cadenas.ui.cache

import com.hashapps.cadenas.ui.processing.ProcessingMode
import java.time.Instant

/**
 * Each decoded or encoded message may be captured here for a short
 * time if caching is turned on.
 *
 * @property[message] The encoded/decoded message (not the cover text!)
 * @property[time] The time this message was encoded/decoded
 * @property[processingMode] either Encode or Decode (where this message came from)
 * @property[channelId] id of the channel for this message (used to fetch time to keep)
 */
data class Message (
    val message: String = "",
    val time: Instant,
    val processingMode: ProcessingMode,
    val channelId: Long,
)