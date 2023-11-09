package com.hashapps.cadenas.ui.channels.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar
import com.hashapps.cadenas.ui.channels.ChannelUiState

private const val MAX_LEN = 128

/**
 * Cadenas channel-add screen.
 *
 * Allows the creation of Cadenas channels "from scratch".
 */
@Composable
fun ChannelAddScreen(
    navigateNext: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChannelAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.channel_entry),
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        ChannelAddBody(
            modifier = modifier.padding(innerPadding),
            channelUiState = viewModel.channelUiState,
            models = viewModel.availableModels,
            onChannelValueChange = viewModel::updateUiState,
            onSaveClick = {
                viewModel.saveChannel()
                navigateNext()
            },
        )
    }
}

@Composable
private fun ChannelAddBody(
    channelUiState: ChannelUiState,
    models: List<String>,
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
            models = models,
            onValueChange = onChannelValueChange,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelInputForm(
    channelUiState: ChannelUiState,
    modifier: Modifier = Modifier,
    models: List<String>,
    onValueChange: (ChannelUiState) -> Unit = {},
    editing: Boolean = false,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = channelUiState.name,
            onValueChange = { onValueChange(channelUiState.copy(name = it.take(MAX_LEN))) },
            singleLine = true,
            label = { Text(stringResource(R.string.channel_name_label)) },
            supportingText = { Text(stringResource(R.string.channel_name_support)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = channelUiState.description,
            onValueChange = { onValueChange(channelUiState.copy(description = it.take(MAX_LEN))) },
            singleLine = true,
            label = { Text(stringResource(R.string.channel_description_label)) },
            supportingText = { Text(stringResource(R.string.channel_description_support)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )
    }

    if (!editing) {
        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = channelUiState.prompt,
                onValueChange = { onValueChange(channelUiState.copy(prompt = it.take(MAX_LEN))) },
                singleLine = true,
                label = { Text(stringResource(R.string.prompt_label)) },
                supportingText = {
                    Text(
                        LocalContext.current.getString(
                            R.string.prompt_support,
                            channelUiState.prompt.length,
                            MAX_LEN,
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = {
                    if (models.isNotEmpty()) {
                        expanded = !expanded
                    }
                },
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = channelUiState.selectedModel,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.model)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                )

                ExposedDropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    models.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                onValueChange(channelUiState.copy(selectedModel = it))
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}