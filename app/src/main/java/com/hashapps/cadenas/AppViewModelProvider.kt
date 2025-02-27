package com.hashapps.cadenas

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashapps.cadenas.ui.home.HomeViewModel
import com.hashapps.cadenas.ui.components.TopViewModel
import com.hashapps.cadenas.ui.settings.models.manage.ManageModelsViewModel
import com.hashapps.cadenas.ui.settings.models.add.ModelAddViewModel
import com.hashapps.cadenas.ui.settings.models.disk.ModelAddDiskViewModel
import com.hashapps.cadenas.ui.processing.ProcessingViewModel
import com.hashapps.cadenas.ui.channels.add.ChannelAddViewModel
import com.hashapps.cadenas.ui.channels.edit.ChannelEditViewModel
import com.hashapps.cadenas.ui.channels.export.ChannelExportViewModel
import com.hashapps.cadenas.ui.channels.import.ChannelImportViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.channelRepository,
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ProcessingViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.channelRepository,
                cadenasApplication().container.modelRepository,
                cadenasApplication().container.messageCache,
            )
        }

        initializer {
            ChannelAddViewModel(
                cadenasApplication().container.channelRepository,
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ChannelImportViewModel(
                cadenasApplication().container.channelRepository,
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ChannelExportViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.channelRepository,
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ChannelEditViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.channelRepository,
                cadenasApplication().container.modelRepository
            )
        }

        initializer {
            ManageModelsViewModel(
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ModelAddViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            TopViewModel(
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ModelAddDiskViewModel(
                cadenasApplication().container.modelRepository,
            )
        }
    }
}

fun CreationExtras.cadenasApplication(): CadenasApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CadenasApplication)