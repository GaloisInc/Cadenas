package com.hashapps.butkusapp.ui.models

import android.app.Application
import com.hashapps.butkusapp.data.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** ButkusViewModel for the settings screen */
class SettingsViewModel(app: Application) : ButkusViewModel(app) {
    private val _settingsUiState = MutableStateFlow(SettingsUiState())

    /** True iff key/seed are non-empty */
    override val runInputNonempty: Boolean
        get() = _settingsUiState.value.secret_key != "" && _settingsUiState.value.seed_text != ""

    /** SettingsViewModel-controlled state, safe from changes from other classes */
    val encodeUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    /**  */
    override suspend fun run() {

    }

    override fun reset() {

    }
}