package com.hashapps.cadenas.ui.settings.profiles.manage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
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
import com.hashapps.cadenas.data.Profile
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.ui.components.DeleteConfirmationDialog
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

/**
 * Cadenas profile-management screen.
 *
 * Cadenas allows the creation of any number of messaging profiles, as
 * described in the documentation for [ProfileAddScreen] and
 * [ProfileEditScreen]. These profiles determine how messages are to be encoded
 * and decoded, and are intended to be shared exactly (other than cosmetic
 * details such as name and description) between communicating parties.
 *
 * Profiles can be added at any time; the layout of this and the
 * [com.hashapps.cadenas.ui.settings.models.ManageModelsScreen] are very much
 * the same.
 *
 * Profiles can be edited at any time - this is purely cosmetic, so it can't
 * have an effect on the business of Cadenas. Like models, though, only the
 * profiles that are not currently selected may be deleted.
 */
@Composable
fun ManageProfilesScreen(
    onNavigateUp: () -> Unit,
    onNavigateToProfileEntry: () -> Unit,
    onNavigateToProfileImport: () -> Unit,
    onNavigateToProfileExport: (Int) -> Unit,
    onNavigateToProfileEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageProfilesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val selectedProfile by viewModel.selectedProfile.collectAsState()
    val profiles by viewModel.profiles.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.manage_profiles),
                navigateUp = onNavigateUp,
            )
        },
        floatingActionButton = {
            var expanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart),
            ) {
                LargeFloatingActionButton(
                    onClick = { expanded = true },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Message,
                        contentDescription = stringResource(R.string.add_item)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.new_label)) },
                        onClick = {
                            expanded = false
                            onNavigateToProfileEntry()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                        },
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.import_label)) },
                        onClick = {
                            expanded = false
                            onNavigateToProfileImport()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.FileDownload,
                                contentDescription = null,
                            )
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        ProfileList(
            modifier = modifier.padding(innerPadding),
            profiles = profiles,
            selectedProfileId = selectedProfile?.id,
            onProfileSelect = { viewModel.selectProfile(it.id) },
            onProfileExport = onNavigateToProfileExport,
            onProfileEdit = onNavigateToProfileEdit,
            onProfileDelete = viewModel::deleteProfile,
        )
    }
}

@Composable
private fun ProfileList(
    profiles: List<Profile>,
    selectedProfileId: Int?,
    onProfileSelect: (Profile) -> Unit,
    onProfileExport: (Int) -> Unit,
    onProfileEdit: (Int) -> Unit,
    onProfileDelete: (Profile) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        profiles.forEach {
            Profile(
                profile = it,
                selectedProfileId = selectedProfileId,
                onProfileSelect = onProfileSelect,
                onProfileExport = onProfileExport,
                onProfileEdit = onProfileEdit,
                onProfileDelete = onProfileDelete,
            )
        }
    }
}

@Composable
private fun Profile(
    profile: Profile,
    selectedProfileId: Int?,
    onProfileSelect: (Profile) -> Unit,
    onProfileExport: (Int) -> Unit,
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
            headlineContent = { Text(profile.name) },
            supportingContent = { Text(profile.description) },
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
                            text = { Text(stringResource(R.string.export)) },
                            onClick = {
                                expanded = false
                                onProfileExport(profile.id)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.FileUpload,
                                    contentDescription = null
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit)) },
                            onClick = {
                                expanded = false
                                onProfileEdit(profile.id)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete)) },
                            onClick = {
                                expanded = false
                                deleteConfirmationRequired = true
                            },
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