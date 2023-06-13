package com.hashapps.cadenas.ui.settings.profile

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

/**
 * The [NavigationDestination] for the profile-editing screen.
 */
object ProfileEditDestination : NavigationDestination {
    override val route = "profile_edit"
    override val titleRes = R.string.edit_profile
    const val profileIdArg = "profileId"
    val routeWithArgs = "$route/{$profileIdArg}"
}

/**
 * Cadenas profile-editing screen.
 *
 * Cadenas messaging profiles define the parameters with which messages are
 * encoded and decoded. They are intended to be shared by communicating parties
 * through either text-based formats or QR codes (features that, as of now, are
 * not implemented.)
 *
 * It is crucial that communicating parties agree on all non-cosmetic parts of
 * a messaging profile, namely the model to use, the secret key, and the seed
 * text. The profile name and description, however, are fully cosmetic and for
 * the benefit of the user - they do _not_ need to be consistent between
 * communicating parties.
 *
 * For this reason, the profile-editing screen is fairly limited - only the
 * name and description may be modified after the profile is created. This may
 * change in the future (and in fact was the behavior in earlier versions), but
 * for now it is easiest to prevent users from breaking the agreed-upon aspects
 * of a communication channel.
 */
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
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        ProfileEditBody(
            modifier = modifier.padding(innerPadding),
            profileUiState = viewModel.profileUiState,
            models = viewModel.availableModels,
            onProfileValueChange = viewModel::updateUiState,
            onSaveClick = {
                viewModel.updateProfile()
                navigateBack()
            },
        )
    }
}

@Composable
private fun ProfileEditBody(
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
            editing = true,
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