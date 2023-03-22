package com.hashapps.cadenas.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.navigation.NavigationDestination
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

private const val MAX_LEN = 128

object ProfileAddDestination : NavigationDestination {
    override val route = "profile_entry"
    override val titleRes = R.string.profile_entry
    const val modelIdArg = "modelId"
    val routeWithArgs = "$route/{$modelIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAddScreen(
    navigateBack: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val modelName by viewModel.modelName.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = LocalContext.current.getString(ProfileAddDestination.titleRes, modelName),
                canNavigateUp = true,
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        ProfileAddBody(
            modifier = modifier.padding(innerPadding),
            profileUiState = viewModel.profileUiState,
            onProfileValueChange = viewModel::updateUiState,
            onKeyGen = viewModel::genKey,
            onSaveClick = {
                viewModel.saveProfile()
                navigateBack()
            },
        )
    }
}

@Composable
fun ProfileAddBody(
    profileUiState: ProfileUiState,
    onProfileValueChange: (ProfileUiState) -> Unit,
    onKeyGen: () -> Unit,
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
            onValueChange = onProfileValueChange,
            onKeyGen = onKeyGen,
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
    onValueChange: (ProfileUiState) -> Unit = {},
    onKeyGen: () -> Unit = {},
    enabled: Boolean = true,
) {
    val focusManager = LocalFocusManager.current

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            enabled = enabled,
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
            enabled = enabled,
            value = profileUiState.description,
            onValueChange = { onValueChange(profileUiState.copy(description = it.take(MAX_LEN))) },
            singleLine = true,
            label = { Text(stringResource(R.string.profile_description_label)) },
            supportingText = { Text(stringResource(R.string.profile_description_support)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        Row(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = modifier.weight(1f),
                enabled = enabled,
                readOnly = true,
                value = profileUiState.key,
                onValueChange = {},
                singleLine = true,
                label = { Text(stringResource(R.string.key_label)) },
                placeholder = { Text(stringResource(R.string.key_placeholder)) },
            )

            FilledTonalIconButton(
                onClick = onKeyGen,
                enabled = enabled,
            ) {
                Icon(
                    imageVector = Icons.Filled.Key,
                    contentDescription = stringResource(R.string.generate_key)
                )
            }
        }

        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            enabled = enabled,
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