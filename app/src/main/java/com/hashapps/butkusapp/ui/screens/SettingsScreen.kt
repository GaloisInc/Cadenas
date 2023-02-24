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
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.SettingsUiState
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

private const val maxLen = 128
private val urlRegex =
    Regex("""https?://(www\.)?[-a-zA-Z\d@:%._+~#=]{1,256}\.[a-zA-Z\d()]{1,6}\b([-a-zA-Z\d()!@:%_+.~#?&/=]*)""")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsUiState: SettingsUiState,
    onGenKey: () -> Unit,
    onSeedChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onAddUrl: () -> Unit,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onSelectModel: (String) -> Unit,
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
                    value = settingsUiState.secretKey,
                    onValueChange = { },
                    singleLine = true,
                    label = { Text(stringResource(R.string.key_label)) },
                    placeholder = { Text(stringResource(R.string.key_placeholder)) },
                )

                FilledTonalIconButton(
                    onClick = onGenKey,
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
                value = settingsUiState.seedText,
                onValueChange = { onSeedChange(it.take(maxLen)) },
                singleLine = true,
                label = { Text(stringResource(R.string.seed_label)) },
                supportingText = {
                    Text(
                        LocalContext.current.getString(
                            R.string.seed_support,
                            settingsUiState.seedText.length,
                            maxLen,
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
            val urlValid = urlRegex.matches(settingsUiState.modelUrlToAdd)
            val isError = settingsUiState.modelUrlToAdd != "" && !urlValid

            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = settingsUiState.modelUrlToAdd,
                onValueChange = { onUrlChange(it.take(maxLen)) },
                singleLine = true,
                label = { Text(stringResource(R.string.url_label)) },
                trailingIcon = {
                    IconButton(
                        enabled = urlValid && settingsUiState.modelUrlToAdd !in settingsUiState.modelUrls,
                        onClick = onAddUrl,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = stringResource(R.string.add_url),
                        )
                    }
                },
                supportingText = {
                    if (isError) {
                        Text(stringResource(R.string.url_error))
                    } else {
                        Text(
                            LocalContext.current.getString(
                                R.string.url_support,
                                settingsUiState.modelUrlToAdd.length,
                                maxLen,
                            )
                        )
                    }
                },
                isError = isError,
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
                expanded = settingsUiState.urlMenuExpanded,
                onExpandedChange = { onToggleMenu() },
            ) {
                OutlinedTextField(
                    modifier = modifier
                        .menuAnchor()
                        .padding(8.dp)
                        .fillMaxWidth(),
                    value = settingsUiState.selectedModel,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.selected_url_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = settingsUiState.urlMenuExpanded) },
                    singleLine = true,
                )

                ExposedDropdownMenu(
                    expanded = settingsUiState.urlMenuExpanded,
                    onDismissRequest = onDismissMenu,
                ) {
                    settingsUiState.modelUrls.forEach {
                        // TODO: Make the text something more meaningful once we have download machinery
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                onSelectModel(it)
                                onDismissMenu()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreviewDefault() {
    ButkusAppTheme {
        SettingsScreen(
            settingsUiState = SettingsUiState(),
            onGenKey = { },
            onSeedChange = { },
            onUrlChange = { },
            onAddUrl = { },
            onToggleMenu = { },
            onDismissMenu = { },
            onSelectModel = { },
        )
    }
}