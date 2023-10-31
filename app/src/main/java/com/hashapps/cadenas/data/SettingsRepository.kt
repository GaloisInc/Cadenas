package com.hashapps.cadenas.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

/**
 * Repository class for Cadenas application settings.
 *
 * While there aren't exactly many personalization/customization options for
 * Cadenas, there are a few persistent settings relevant to normal operation.
 * More specifically, we must keep track of:
 *
 * - Which messaging channel is selected
 *
 * These settings are persisted using the [DataStore]<[Preferences]>, a
 * file-based key-value store. On instantiation, this class launches an
 * application-scoped background job that listens for changes to the
 * selected channel, re-initializing Cadenas when a change occurs.
 *
 * @property[cadenasInitialized] A derived Boolean flag indicating whether or
 * not the Cadenas backend has been initialized or not
 * @property[selectedChannel] The [Channel] selected by the stored integer
 * preference ID
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val modelsDir: File,
    private val channelDao: ChannelDao,
    externalScope: CoroutineScope,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private companion object {
        val SELECTED_CHANNEL = intPreferencesKey("selected_channel")

        const val TAG = "SettingsRepo"
    }

    /**
     * Update which messaging channel is selected.
     *
     * @param[selectedChannel] The ID of the messaging channel that's been
     * selected
     */
    suspend fun saveSelectedChannel(selectedChannel: Int) {
        dataStore.edit {
            it[SELECTED_CHANNEL] = selectedChannel
        }
    }

    private var _cadenasInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val cadenasInitialized: StateFlow<Boolean> = _cadenasInitialized.asStateFlow()

    private var _selectedChannel: MutableStateFlow<Channel?> = MutableStateFlow(null)
    val selectedChannel: StateFlow<Channel?> = _selectedChannel.asStateFlow()

    init {
        externalScope.launch(ioDispatcher) {
            dataStore.data
                .catch {
                    if (it is IOException) {
                        Log.e(TAG, "Error reading settings.", it)
                        emit(emptyPreferences())
                    } else {
                        throw it
                    }
                }.map { preferences ->
                    preferences[SELECTED_CHANNEL]
                }.filterNotNull()
                .flatMapLatest { channelDao.getChannel(it) }
                .collectLatest { channel ->
                    _selectedChannel.update { channel }

                    Cadenas.initialize(
                        CadenasConfig(
                            modelDir = modelsDir.resolve(channel.selectedModel).path,
                            key = channel.key.chunked(2).map { b -> b.toInt(16).toByte() }
                                .toByteArray(),
                            prompt = channel.prompt,
                        )
                    )

                    if (Cadenas.getInstance() != null) {
                        _cadenasInitialized.update { true }
                    }
                }
        }
    }
}