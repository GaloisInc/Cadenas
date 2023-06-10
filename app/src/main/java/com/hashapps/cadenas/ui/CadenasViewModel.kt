package com.hashapps.cadenas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CadenasViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val isNotFirstRun = settingsRepository.isNotFirstRun.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = false,
    )

    fun completeFirstRun() {
        viewModelScope.launch {
            settingsRepository.completeFirstRun()
            settingsRepository.saveSelectedProfile(1)
        }
    }
}