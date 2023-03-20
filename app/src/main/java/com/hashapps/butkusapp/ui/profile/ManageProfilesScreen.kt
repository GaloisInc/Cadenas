package com.hashapps.butkusapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.data.profile.Profile
import com.hashapps.butkusapp.ui.AppViewModelProvider
import com.hashapps.butkusapp.ui.components.DeleteConfirmationDialog
import com.hashapps.butkusapp.ui.navigation.NavigationDestination
import com.hashapps.butkusapp.ui.settings.SettingsTopAppBar

object ManageProfilesDestination : NavigationDestination {
    override val route = "manage_profiles"
    override val titleRes = R.string.manage_profiles
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProfilesScreen(
    navigateUp: () -> Unit,
    navigateToProfileEntry: () -> Unit,
    navigateToProfileEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageProfilesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val selectedProfileId by viewModel.selectedProfileId.collectAsState()
    val profiles by viewModel.profiles.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(ManageProfilesDestination.titleRes),
                canNavigateUp = true,
                navigateUp = navigateUp,
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = navigateToProfileEntry,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_item)
                )
            }
        },
    ) { innerPadding ->
        ManageProfilesBody(
            modifier = modifier.padding(innerPadding),
            profiles = profiles,
            selectedProfileId = selectedProfileId,
            onProfileSelect = {
                viewModel.selectProfile(it)
            },
            onProfileEdit = {
                navigateToProfileEdit(it)
            },
            onProfileDelete = {
                viewModel.deleteProfile(it)
            },
        )
    }
}

@Composable
fun ManageProfilesBody(
    profiles: List<Profile>,
    selectedProfileId: Int?,
    onProfileSelect: (Int) -> Unit,
    onProfileEdit: (Int) -> Unit,
    onProfileDelete: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileList(
            profiles = profiles,
            selectedProfileId = selectedProfileId,
            onProfileSelect = { onProfileSelect(it.id) },
            onProfileEdit = { onProfileEdit(it) },
            onProfileDelete = { onProfileDelete(it.id) },
        )
    }


}

@Composable
fun ProfileList(
    profiles: List<Profile>,
    selectedProfileId: Int?,
    onProfileSelect: (Profile) -> Unit,
    onProfileEdit: (Int) -> Unit,
    onProfileDelete: (Profile) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = profiles, key = { it.id }) {
            ButkusProfile(
                profile = it,
                selectedProfileId = selectedProfileId,
                onProfileSelect = onProfileSelect,
                onProfileEdit = onProfileEdit,
                onProfileDelete = onProfileDelete,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButkusProfile(
    profile: Profile,
    selectedProfileId: Int?,
    onProfileSelect: (Profile) -> Unit,
    onProfileEdit: (Int) -> Unit,
    onProfileDelete: (Profile) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        ListItem(
            headlineText = { Text(profile.name) },
            supportingText = { Text(profile.description) },
            leadingContent = {
                RadioButton(
                    selected = profile.id == selectedProfileId,
                    onClick = { onProfileSelect(profile) },
                )
            },
            trailingContent = {
                Row {
                    IconButton(onClick = { onProfileEdit(profile.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit_item),
                        )
                    }

                    IconButton(
                        onClick = { deleteConfirmationRequired = true },
                        enabled = profile.id != selectedProfileId,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            contentDescription = stringResource(R.string.delete),
                        )
                    }
                }
            },
        )


        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                confirmationQuestion = stringResource(R.string.delete_profile_question),
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onProfileDelete(profile)
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
            )
        }
    }
}

