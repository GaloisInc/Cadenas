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
import com.hashapps.butkusapp.ui.models.SettingsViewModel
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

private const val maxLen = 128
private val urlRegex =
    Regex("""https?://(www\.)?[-a-zA-Z\d@:%._+~#=]{1,256}\.[a-zA-Z\d()]{1,6}\b([-a-zA-Z\d()!@:%_+.~#?&/=]*)""")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    vm: SettingsViewModel = SettingsViewModel(),
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
                    value = vm.uiState.secretKey,
                    onValueChange = { },
                    singleLine = true,
                    label = { Text(stringResource(R.string.key_label)) },
                    placeholder = { Text(stringResource(R.string.key_placeholder)) },
                )

                FilledTonalIconButton(
                    onClick = { /* TODO: Generate AES-256 key */ },
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
                value = vm.uiState.seedText,
                onValueChange = { vm.updateSeedText(it.take(maxLen)) },
                singleLine = true,
                label = { Text(stringResource(R.string.seed_label)) },
                supportingText = {
                    Text(
                        LocalContext.current.getString(
                            R.string.seed_support,
                            vm.uiState.seedText.length,
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
            val urlValid = urlRegex.matches(vm.uiState.modelUrlToAdd)
            val isError = vm.uiState.modelUrlToAdd != "" && !urlValid

            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = vm.uiState.modelUrlToAdd,
                onValueChange = { vm.updateModelToAdd(it.take(maxLen)) },
                singleLine = true,
                label = { Text(stringResource(R.string.url_label)) },
                trailingIcon = {
                    IconButton(
                        enabled = urlValid && vm.uiState.modelUrlToAdd !in vm.uiState.modelUrls,
                        onClick = { vm.addUrl(vm.uiState.modelUrlToAdd) },
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
                                vm.uiState.modelUrlToAdd.length,
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
                expanded = vm.uiState.urlMenuExpanded,
                onExpandedChange = { vm.toggleUrlMenu() },
            ) {
                OutlinedTextField(
                    modifier = modifier
                        .menuAnchor()
                        .padding(8.dp)
                        .fillMaxWidth(),
                    value = vm.uiState.selectedModel,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.selected_url_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vm.uiState.urlMenuExpanded) },
                    singleLine = true,
                )

                ExposedDropdownMenu(
                    expanded = vm.uiState.urlMenuExpanded,
                    onDismissRequest = vm::dismissUrlMenu,
                ) {
                    vm.uiState.modelUrls.forEach {
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
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreviewDefault() {
    ButkusAppTheme {
        SettingsScreen()
    }
}