package com.hashapps.cadenas.ui.channels.add

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.R
import com.hashapps.cadenas.data.models.Model
import com.hashapps.cadenas.ui.channels.ChannelUiState
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

private const val MAX_LEN = 128

/**
 * Cadenas channel-add screen.
 *
 * Allows the creation of Cadenas channels "from scratch".
 */
@Composable
fun ChannelAddScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddModel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChannelAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val models by viewModel.models.collectAsState()
    Log.d("ChannelAdd", models.toString())

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.channel_entry),
                onNavigateBack = onNavigateBack,
            )
        }
    ) { innerPadding ->
        ChannelAddBody(
            modifier = modifier.padding(innerPadding),
            channelUiState = viewModel.channelUiState,
            models = models,
            onChannelValueChange = viewModel::updateUiState,
            onAddModel = onNavigateToAddModel,
            onSaveClick = {
                viewModel.saveChannel()
                onNavigateBack()
            },
        )
    }
}

@Composable
private fun ChannelAddBody(
    channelUiState: ChannelUiState,
    modifier: Modifier = Modifier,
    models: List<Model>,
    onChannelValueChange: (ChannelUiState) -> Unit,
    onAddModel: () -> Unit = {},
    onSaveClick: () -> Unit,
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
            onAddModel = onAddModel,
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
    models: List<Model>,
    onValueChange: (ChannelUiState) -> Unit = {},
    onAddModel: () -> Unit = {},
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
            onValueChange = { onValueChange(channelUiState.copy(name = it)) },
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
            onValueChange = { onValueChange(channelUiState.copy(description = it)) },
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
                    expanded = !expanded
                },
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
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
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.add_model)) },
                        onClick = {
                            expanded = false
                            onAddModel()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                    models.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                expanded = false
                                onValueChange(channelUiState.copy(selectedModel = it.name))
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}