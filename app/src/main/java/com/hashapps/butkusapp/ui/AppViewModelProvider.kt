package com.hashapps.butkusapp.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashapps.butkusapp.ButkusApplication
import com.hashapps.butkusapp.ui.model.ManageModelsViewModel
import com.hashapps.butkusapp.ui.model.ModelAddViewModel
import com.hashapps.butkusapp.ui.processing.ProcessingViewModel
import com.hashapps.butkusapp.ui.profile.ManageProfilesViewModel
import com.hashapps.butkusapp.ui.profile.ProfileEditViewModel
import com.hashapps.butkusapp.ui.profile.ProfileEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ProcessingViewModel(
                butkusApplication().container.butkusRepository,
            )
        }

        initializer {
            ManageProfilesViewModel(
                butkusApplication().container.profilesRepository,
                butkusApplication().container.butkusRepository,
            )
        }

        initializer {
            ProfileEntryViewModel(
                butkusApplication().container.profilesRepository,
                butkusApplication().container.modelsRepository,
            )
        }

        initializer {
            ProfileEditViewModel(
                this.createSavedStateHandle(),
                butkusApplication().container.profilesRepository,
                butkusApplication().container.modelsRepository,
            )
        }

        initializer {
            ManageModelsViewModel(
                butkusApplication().container.modelsRepository,
            )
        }

        initializer {
            ModelAddViewModel(
                butkusApplication().container.modelsRepository,
            )
        }
    }
}

fun CreationExtras.butkusApplication(): ButkusApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ButkusApplication)