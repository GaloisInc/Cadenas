package com.hashapps.cadenas.ui.settings.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

private const val MAX_LEN = 128

/**
 * Cadenas profile-add screen.
 *
 * Much of the detail about Cadenas profiles can be found in the documentation
 * for [ProfileEditScreen]. Note that a secret encryption key is generated
 * automatically when creating a channel this way, and this cannot be changed.
 *
 * Channels may be shared via QR code; see [ProfileExportScreen].
 */
@Composable
fun ProfileAddScreen(
    navigateNext: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.profile_entry),
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        ProfileAddBody(
            modifier = modifier.padding(innerPadding),
            profileUiState = viewModel.profileUiState,
            models = viewModel.availableModels,
            onProfileValueChange = viewModel::updateUiState,
            onSaveClick = {
                viewModel.saveProfile()
                navigateNext()
            },
        )
    }
}

@Composable
private fun ProfileAddBody(
    profileUiState: ProfileUiState,
    models: List<String>,
    onProfileValueChange: (ProfileUiState) -> Unit,
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
        ProfileInputForm(
            profileUiState = profileUiState,
            models = models,
            onValueChange = onProfileValueChange,
        )

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = profileUiState.actionEnabled,
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
fun ProfileInputForm(
    profileUiState: ProfileUiState,
    modifier: Modifier = Modifier,
    models: List<String>,
    onValueChange: (ProfileUiState) -> Unit = {},
    editing: Boolean = false,
) {
    val focusManager = LocalFocusManager.current

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = profileUiState.name,
            onValueChange = { onValueChange(profileUiState.copy(name = it.take(MAX_LEN))) },
            singleLine = true,
            label = { Text(stringResource(R.string.profile_name_label)) },
            supportingText = { Text(stringResource(R.string.profile_name_support)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = profileUiState.description,
            onValueChange = { onValueChange(profileUiState.copy(description = it.take(MAX_LEN))) },
            singleLine = true,
            label = { Text(stringResource(R.string.profile_description_label)) },
            supportingText = { Text(stringResource(R.string.profile_description_support)) },
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
                value = profileUiState.seed,
                onValueChange = { onValueChange(profileUiState.copy(seed = it.take(MAX_LEN))) },
                singleLine = true,
                label = { Text(stringResource(R.string.seed_label)) },
                supportingText = {
                    Text(
                        LocalContext.current.getString(
                            R.string.seed_support,
                            profileUiState.seed.length,
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
                    value = profileUiState.selectedModel,
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
                                onValueChange(profileUiState.copy(selectedModel = it))
                                expanded = false
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
            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = profileUiState.tag,
                onValueChange = { onValueChange(profileUiState.copy(tag = it)) },
                singleLine = true,
                label = { Text(stringResource(R.string.tag_label)) },
                supportingText = {
                    if (profileUiState.isTagValid()) {
                        Text(stringResource(R.string.tag_support))
                    } else {
                        Text(stringResource(R.string.tag_error))
                    }
                },
                isError = !profileUiState.isTagValid(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
            )
        }
    }
}