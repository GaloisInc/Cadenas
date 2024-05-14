package com.hashapps.cadenas.ui.processing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.channels.ChannelRepository
import com.hashapps.cadenas.ui.cache.Message
import com.hashapps.cadenas.ui.cache.MessageCache
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * View model for the processing screens.
 *
 * @property[processingUiState] The UI state for the processing screen
 */
class ProcessingViewModel(
    savedStateHandle: SavedStateHandle,
    private val channelRepository: ChannelRepository,
    private val messageCache: MessageCache
) : ViewModel() {
    private val processingArgs = ProcessingArgs(savedStateHandle)

    var processingUiState by mutableStateOf(ProcessingUiState())
        private set

    init {
        setTimerState(this)
        viewModelScope.launch {
            val channelName =
                channelRepository.getChannelStream(processingArgs.channelId).map { it.name }.first()
            processingUiState = processingUiState.copy(
                channelName = channelName,
                toProcess = processingArgs.toDecode,
                processingMode = if (processingArgs.toDecode.isEmpty()) {
                    ProcessingMode.Encode
                } else {
                    ProcessingMode.Decode
                }
            )

            val cachingTimeMS =
                channelRepository.getChannelStream(processingArgs.channelId).map { it.cachingTimeMS }.first()
            setTimerChannelInfo(processingArgs.channelId, cachingTimeMS)
        }
    }

    /**
     * Update the UI state.
     *
     * @param[newProcessingUiState] The new UI state
     */
    fun updateProcessingUiState(newProcessingUiState: ProcessingUiState) {
        processingUiState = newProcessingUiState.copy()
    }

    /**
     * Enter encoding mode.
     */
    fun encodingMode() {
        processingUiState = ProcessingUiState(
            channelName = processingUiState.channelName,
            cachedMessages = processingUiState.cachedMessages,
            processingMode = ProcessingMode.Encode
        )
    }

    /**
     * Enter decoding mode.
     */
    fun decodingMode() {
        processingUiState = ProcessingUiState(
            channelName = processingUiState.channelName,
            cachedMessages = processingUiState.cachedMessages,
            processingMode = ProcessingMode.Decode
        )
    }

    /**
     * Encode or decode the input, based on the current processing mode.
     */
    fun processMessage() {
        when (processingUiState.processingMode) {
            ProcessingMode.Encode -> encodeMessage()
            ProcessingMode.Decode -> decodeMessage()
        }
    }

    /**
     * Attempt to encode the input message using the selected messaging
     * channel.
     */
    private fun encodeMessage() {
        viewModelScope.launch {
            processingUiState = processingUiState.copy(inProgress = true, result = null)
            val textCover = channelRepository.createTextCoverForChannel(processingArgs.channelId)
            val encodedMessage = withContext(Dispatchers.Default) {
                textCover.encodeUntilDecodable(processingUiState.toProcess)
            }
            textCover.destroy()
            processingUiState =
                processingUiState.copy(inProgress = false, showEditWarning = true, result = encodedMessage?.coverText)
            messageCache.insertMessage(
                Message(
                    message = processingUiState.toProcess,
                    time = Instant.now(),
                    processingMode = ProcessingMode.Encode,
                    channelId = processingArgs.channelId
                )
            )
        }
    }

    /**
     * Attempt to decode the input message using the selected messaging
     * channel.
     */
    private fun decodeMessage() {
        viewModelScope.launch {
            processingUiState = processingUiState.copy(inProgress = true, result = null)
            val textCover = channelRepository.createTextCoverForChannel(processingArgs.channelId)
            val decodedMessage = withContext(Dispatchers.Default) {
                textCover.decode(processingUiState.toProcess)
            }
            textCover.destroy()
            processingUiState = processingUiState.copy(inProgress = false, result = decodedMessage)
            messageCache.insertMessage(
                Message(
                    message = decodedMessage.toString(),
                    time = Instant.now(),
                    processingMode = ProcessingMode.Decode,
                    channelId = processingArgs.channelId
                )
            )
        }
    }

    /**
     * This is a single timer that will continuously refresh all Processing
     * Views every second.
     */
    private companion object TimedRefresh {
        val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        var currentView: ProcessingViewModel? = null
        var currentChannelId: Long = -1
        var currentCacheTime: Int = 0

        init {
            //start the timer
            scheduler.scheduleAtFixedRate({
                if (currentView != null) {
                    currentView!!.processingUiState = ProcessingUiState(
                        channelName = currentView!!.processingUiState.channelName,
                        cachedMessages = currentView!!.messageCache.updateMessages(currentChannelId, currentCacheTime),
                        toProcess= currentView!!.processingUiState.toProcess,
                        processingMode = currentView!!.processingUiState.processingMode,
                        result = currentView!!.processingUiState.result,
                        inProgress = currentView!!.processingUiState.inProgress,
                        showEditWarning = currentView!!.processingUiState.showEditWarning,
                    )
                }
            }, 0, 1000, TimeUnit.MILLISECONDS)
        }

        /**
         * Modifies the Processing View and UI State that are being updated via the timer thread.
         */
        fun setTimerState(viewModel: ProcessingViewModel) {
            currentView = viewModel
        }
        fun setTimerChannelInfo(channelId: Long, cacheTime: Int) {
            currentChannelId = channelId
            currentCacheTime = cacheTime
        }
    }
}

