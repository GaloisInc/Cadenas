package com.hashapps.butkusapp.ui

import androidx.lifecycle.ViewModel
import com.hashapps.butkusapp.data.EncodeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ButkusViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EncodeUiState())
    val uiState: StateFlow<EncodeUiState> = _uiState.asStateFlow()

    // TODO: Decode UI state
    // TODO: Methods to update the UI state
}