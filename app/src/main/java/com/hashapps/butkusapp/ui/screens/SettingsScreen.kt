package com.hashapps.butkusapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.models.SettingsUiState
import com.hashapps.butkusapp.ui.models.SettingsViewModel
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

private const val MAX_LEN = 128

private val urlRegex =
    Regex("""https?://(www\.)?[-a-zA-Z\d@:%._+~#=]{1,256}\.[a-zA-Z\d()]{1,6}\b([-a-zA-Z\d()!@:%_+.~#?&/=]*)""")
private val SettingsUiState.urlValid get() = urlRegex.matches(modelUrlToAdd)
private val SettingsUiState.isErrorUrl get() = modelUrlToAdd != "" && !urlValid
private val SettingsUiState.canAddUrl get () = urlValid && modelUrlToAdd !in modelUrls

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val focusManager = LocalFocusManager.current
        val savedSettings by vm.savedSettings.collectAsStateWithLifecycle()
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    modifier = modifier.weight(1f),
                    readOnly = true,
                    value = savedSettings.secretKey,
                    onValueChange = { },
                    singleLine = true,
                    label = { Text(stringResource(R.string.key_label)) },
                    placeholder = { Text(stringResource(R.string.key_placeholder)) },
                )

                FilledTonalIconButton(
                    onClick = vm::genKey,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Key,
                        contentDescription = stringResource(R.string.generate_key)
                    )
                }
            }
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = savedSettings.seedText,
                onValueChange = { vm.updateSeedText(it.take(MAX_LEN)) },
                singleLine = true,
                label = { Text(stringResource(R.string.seed_label)) },
                supportingText = {
                    Text(
                        LocalContext.current.getString(
                            R.string.seed_support,
                            savedSettings.seedText.length,
                            MAX_LEN,
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            )
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = uiState.modelUrlToAdd,
                onValueChange = { vm.updateModelToAdd(it.take(MAX_LEN)) },
                singleLine = true,
                label = { Text(stringResource(R.string.url_label)) },
                trailingIcon = {
                    IconButton(
                        enabled = uiState.canAddUrl,
                        onClick = { vm.addUrl(uiState.modelUrlToAdd) },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = stringResource(R.string.add_url),
                        )
                    }
                },
                supportingText = {
                    if (uiState.isErrorUrl) {
                        Text(stringResource(R.string.url_error))
                    } else {
                        Text(
                            LocalContext.current.getString(
                                R.string.url_support,
                                uiState.modelUrlToAdd.length,
                                MAX_LEN,
                            )
                        )
                    }
                },
                isError = uiState.isErrorUrl,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                ),
            )

            ExposedDropdownMenuBox(
                expanded = uiState.urlMenuExpanded,
                onExpandedChange = { vm.toggleUrlMenu() },
            ) {
                OutlinedTextField(
                    modifier = modifier
                        .menuAnchor()
                        .padding(8.dp)
                        .fillMaxWidth(),
                    value = savedSettings.selectedModel,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.selected_url_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.urlMenuExpanded) },
                    singleLine = true,
                )

                ExposedDropdownMenu(
                    expanded = uiState.urlMenuExpanded,
                    onDismissRequest = vm::dismissUrlMenu,
                ) {
                    uiState.modelUrls.forEach {
                        // TODO: Make the text something more meaningful once we have download machinery
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                vm.selectModelUrl(it)
                                vm.dismissUrlMenu()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            Button(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                onClick = vm::saveSettings,
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Row(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = modifier.weight(0.5f),
                    enabled = true, // TODO: Base this on saved settings
                    onClick = { },
                ) {
                    Text(
                        text = stringResource(R.string.import_label),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                Button(
                    modifier = modifier.weight(0.5f),
                    enabled = true, // TODO: Base this on saved settings
                    onClick = { },
                ) {
                    Text(
                        text = stringResource(R.string.export_label),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreviewDefault() {
    ButkusAppTheme {
        SettingsScreen()
    }
}