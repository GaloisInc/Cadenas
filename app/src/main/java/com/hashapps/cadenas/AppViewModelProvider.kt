package com.hashapps.cadenas

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashapps.cadenas.domain.ManageModelsUseCase
import com.hashapps.cadenas.ui.settings.models.manage.ManageModelsViewModel
import com.hashapps.cadenas.ui.settings.models.add.ModelAddViewModel
import com.hashapps.cadenas.ui.processing.ProcessingViewModel
import com.hashapps.cadenas.ui.settings.profiles.manage.ManageProfilesViewModel
import com.hashapps.cadenas.ui.settings.profiles.add.ProfileAddViewModel
import com.hashapps.cadenas.ui.settings.profiles.edit.ProfileEditViewModel
import com.hashapps.cadenas.ui.settings.profiles.exporting.ProfileExportViewModel
import com.hashapps.cadenas.ui.settings.profiles.importing.ProfileImportViewModel

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
            ProfileImportViewModel(
                cadenasApplication().container.profileRepository
            )
        }

        initializer {
            ProfileExportViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.profileRepository,
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