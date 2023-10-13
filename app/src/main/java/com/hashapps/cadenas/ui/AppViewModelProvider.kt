package com.hashapps.cadenas.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashapps.cadenas.CadenasApplication
import com.hashapps.cadenas.domain.ManageModelsUseCase
import com.hashapps.cadenas.ui.settings.model.ManageModelsViewModel
import com.hashapps.cadenas.ui.settings.model.ModelAddViewModel
import com.hashapps.cadenas.ui.processing.ProcessingViewModel
import com.hashapps.cadenas.ui.settings.profile.ManageProfilesViewModel
import com.hashapps.cadenas.ui.settings.profile.ProfileAddViewModel
import com.hashapps.cadenas.ui.settings.profile.ProfileEditViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ProcessingViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.settingsRepository,
            )
        }

        initializer {
            ProfileAddViewModel(
                cadenasApplication().container.profileRepository,
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ProfileEditViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.profileRepository,
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ManageModelsViewModel(
                ManageModelsUseCase(
                    cadenasApplication().container.modelRepository,
                    cadenasApplication().container.profileRepository
                ),
                cadenasApplication().container.settingsRepository,
            )
        }

        initializer {
            ModelAddViewModel(
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ManageProfilesViewModel(
                cadenasApplication().container.profileRepository,
                cadenasApplication().container.settingsRepository,
            )
        }
    }
}

fun CreationExtras.cadenasApplication(): CadenasApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CadenasApplication)