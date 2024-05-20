package com.hashapps.cadenas.ui.cache

import java.time.Instant


interface MessageCache {
    fun insertMessage(message: Message)
    fun updateMessages(channelId: Long, cacheTime: Int): List<Message>
    fun clearMessages(channelId: Long)
}

/**
 * Instance of cache we'll use to track the encoded and decoded
 * messages.  Currently displayed on each channel processing page,
 * and deleted when the timer for that channel runs out.
 */
class ActiveMessageCache() : MessageCache {
    private var messages: List<Message> = emptyList<Message>()

    /**
     * Add a new message to the cache.
     */
    override fun insertMessage(message: Message) {
        messages = messages.plus(message)
    }

    /**
     * Get all the messages that belong to the given channel.
     * (Removing from cache any that have expired.)
     */
    override fun updateMessages(channelId: Long, cacheTime: Int): List<Message> {
        //if there are any messages that have expired, delete them first
        messages = messages.filter {
            (it.channelId != channelId) ||
            (it.channelId == channelId && it.time.plusMillis(cacheTime.toLong()).isAfter(Instant.now()))
        }
        //now return the list for the given channel
        return messages.filter {
            it.channelId == channelId        }
    }

    /**
     * Remove all messages of the given channel from the chache.
     */
    override fun clearMessages(channelId: Long) {
        messages = messages.filter {
            (it.channelId != channelId)
        }
    }
}
