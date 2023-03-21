package com.hashapps.cadenas.ui.model.profile

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
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.navigation.NavigationDestination
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

object ProfileEditDestination : NavigationDestination {
    override val route = "profile_edit"
    override val titleRes = R.string.edit_profile
    const val modelIdArg = "modelId"
    const val profileIdArg = "profileId"
    val routeWithArgs = "$route/{$modelIdArg}/{$profileIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    navigateBack: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
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
            onProfileValueChange = viewModel::updateUiState,
            onKeyGen = viewModel::genKey,
            onSaveClick = {
                viewModel.updateProfile()
                navigateBack()
            },
        )
    }
}

@Composable
fun ProfileEditBody(
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