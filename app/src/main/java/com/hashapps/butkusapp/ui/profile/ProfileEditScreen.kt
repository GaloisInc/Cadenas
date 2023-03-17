package com.hashapps.butkusapp.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.AppViewModelProvider
import com.hashapps.butkusapp.ui.navigation.NavigationDestination
import com.hashapps.butkusapp.ui.settings.SettingsTopAppBar

object ProfileEditDestination : NavigationDestination {
    override val route = "profile_edit"
    override val titleRes = R.string.edit_profile
    const val profileIdArg = "profileId"
    val routeWithArgs = "$route/{$profileIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    navigateBack: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val models by viewModel.models.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(ProfileEditDestination.titleRes),
                canNavigateUp = true,
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        ProfileEditBody(
            modifier = modifier.padding(innerPadding),
            profileUiState = viewModel.profileUiState,
            models = models,
            onProfileValueChange = viewModel::updateUiState,
            onKeyGen = viewModel::genKey,
            onSaveClick = {
                viewModel.updateProfile()
                navigateBack()
            },
            onDeleteClick = {
                viewModel.deleteProfile()
                navigateBack()
            },
        )
    }
}

@Composable
fun ProfileEditBody(
    profileUiState: ProfileUiState,
    models: List<String>,
    onProfileValueChange: (ProfileUiState) -> Unit,
    onKeyGen: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        ProfileInputForm(
            profileUiState = profileUiState,
            models = models,
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

        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.delete),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDeleteClick()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}