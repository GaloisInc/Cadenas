package com.hashapps.cadenas.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashapps.cadenas.CadenasApplication
import com.hashapps.cadenas.ui.model.ManageModelsViewModel
import com.hashapps.cadenas.ui.model.ModelAddViewModel
import com.hashapps.cadenas.ui.profile.ManageProfilesViewModel
import com.hashapps.cadenas.ui.profile.ProfileEditViewModel
import com.hashapps.cadenas.ui.profile.ProfileAddViewModel
import com.hashapps.cadenas.ui.processing.ProcessingViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ProcessingViewModel(
                cadenasApplication().container.settingsRepository,
            )
        }

        initializer {
            ProfileAddViewModel(
                this.createSavedStateHandle(),
                cadenasApplication().container.profilesRepository,
            )
        }

        initializer {
            ProfileEditViewModel(
                this.createSavedStateHandle(),
                cadenasApplication().container.profilesRepository,
                cadenasApplication().container.modelsRepository,
            )
        }

        initializer {
            ManageModelsViewModel(
                cadenasApplication().container.modelsRepository,
                cadenasApplication().container.settingsRepository,
            )
        }

        initializer {
            ModelAddViewModel(
                cadenasApplication().container.modelsRepository,
            )
        }

        initializer {
            ManageProfilesViewModel(
                this.createSavedStateHandle(),
                cadenasApplication().container.profilesRepository,
                cadenasApplication().container.settingsRepository,
            )
        }
    }
}

fun CreationExtras.cadenasApplication(): CadenasApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CadenasApplication)