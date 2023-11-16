package com.hashapps.cadenas.ui.channels.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar
import com.hashapps.cadenas.ui.channels.ChannelUiState
import com.hashapps.cadenas.ui.channels.add.ChannelInputForm

/**
 * Cadenas channel-editing screen.
 *
 * Cadenas messaging channels define the parameters with which messages are
 * encoded and decoded. They are intended to be shared by communicating parties
 * through QR codes.
 *
 * It is crucial that communicating parties agree on all non-cosmetic parts of
 * a messaging channel, namely the model to use, the secret key, and the prompt
 * text. The channel name and description, however, are fully cosmetic and for
 * the benefit of the user - they do _not_ need to be consistent between
 * communicating parties.
 *
 * For this reason, the channel-editing screen is fairly limited - only the
 * name and description may be modified after the channel is created.
 */
@Composable
fun ChannelEditScreen(
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChannelEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.edit_channel),
                navigateUp = onNavigateUp,
            )
        }
    ) { innerPadding ->
        ChannelEditBody(
            modifier = modifier.padding(innerPadding),
            channelUiState = viewModel.channelUiState,
            onChannelValueChange = viewModel::updateUiState,
            onSaveClick = {
                viewModel.updateChannel()
                onNavigateBack()
            },
        )
    }
}

@Composable
private fun ChannelEditBody(
    channelUiState: ChannelUiState,
    onChannelValueChange: (ChannelUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChannelInputForm(
            channelUiState = channelUiState,
            models = listOf(),
            onValueChange = onChannelValueChange,
            editing = true,
        )

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = channelUiState.actionEnabled,
        ) {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}