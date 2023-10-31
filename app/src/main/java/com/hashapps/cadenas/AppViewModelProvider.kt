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
import com.hashapps.cadenas.ui.settings.channels.manage.ManageChannelsViewModel
import com.hashapps.cadenas.ui.settings.channels.add.ChannelAddViewModel
import com.hashapps.cadenas.ui.settings.channels.edit.ChannelEditViewModel
import com.hashapps.cadenas.ui.settings.channels.exporting.ChannelExportViewModel
import com.hashapps.cadenas.ui.settings.channels.importing.ChannelImportViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ProcessingViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.settingsRepository,
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
                cadenasApplication().container.channelRepository
            )
        }

        initializer {
            ChannelExportViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.channelRepository,
            )
        }

        initializer {
            ChannelEditViewModel(
                createSavedStateHandle(),
                cadenasApplication().container.channelRepository,
                cadenasApplication().container.modelRepository,
            )
        }

        initializer {
            ManageModelsViewModel(
                ManageModelsUseCase(
                    cadenasApplication().container.modelRepository,
                    cadenasApplication().container.channelRepository
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
            ManageChannelsViewModel(
                cadenasApplication().container.channelRepository,
                cadenasApplication().container.settingsRepository,
            )
        }
    }
}

fun CreationExtras.cadenasApplication(): CadenasApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CadenasApplication)