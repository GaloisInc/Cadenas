package com.hashapps.cadenas.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.data.profile.Profile
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.components.DeleteConfirmationDialog
import com.hashapps.cadenas.ui.navigation.NavigationDestination
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

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
                onClick = { navigateToProfileEntry() },
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
            onProfileSelect = { viewModel.selectProfile(it) },
            onProfileEdit = { navigateToProfileEdit(it) },
            onProfileDelete = { viewModel.deleteProfile(it) },
        )
    }
}

@Composable
fun ManageProfilesBody(
    profiles: List<Profile>,
    selectedProfileId: Int?,
    onProfileSelect: (Int) -> Unit,
    onProfileEdit: (Int) -> Unit,
    onProfileDelete: (Profile) -> Unit,
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
            onProfileEdit = onProfileEdit,
            onProfileDelete = { onProfileDelete(it) },
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
        items(profiles, key = { it.id }) {
            CadenasProfile(
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
fun CadenasProfile(
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

        var expanded by remember { mutableStateOf(false) }

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
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart),
                ) {
                    IconButton(
                        onClick = { expanded = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = stringResource(R.string.profile_menu),
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit)) },
                            onClick = { onProfileEdit(profile.id) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete)) },
                            onClick = { deleteConfirmationRequired = true },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.DeleteForever,
                                    contentDescription = null
                                )
                            },
                            enabled = profile.id != selectedProfileId,
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