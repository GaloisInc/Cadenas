package com.hashapps.butkusapp.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashapps.butkusapp.ButkusApplication
import com.hashapps.butkusapp.ui.decode.DecodeViewModel
import com.hashapps.butkusapp.ui.encode.EncodeViewModel
import com.hashapps.butkusapp.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            EncodeViewModel()
        }

        initializer {
            SettingsViewModel(butkusApplication().container.settingsRepository)
        }

        initializer {
            DecodeViewModel()
        }
    }
}

fun CreationExtras.butkusApplication(): ButkusApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ButkusApplication)