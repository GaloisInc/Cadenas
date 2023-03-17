package com.hashapps.butkusapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.data.profile.Profile
import com.hashapps.butkusapp.ui.AppViewModelProvider
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
    val selectedProfile by viewModel.selectedProfile.collectAsState()
    val profiles by viewModel.profiles.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(ManageProfilesDestination.titleRes),
                canNavigateUp = true,
                navigateUp = navigateUp,
                selectedProfile = selectedProfile,
                canEditItem = selectedProfile != null,
                navigateToEditItem = navigateToProfileEdit,
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
            selectedProfileId = selectedProfile,
            onProfileClick = {
                viewModel.selectProfile(it)
            },
        )
    }
}

@Composable
fun ManageProfilesBody(
    profiles: List<Profile>,
    selectedProfileId: Int?,
    onProfileClick: (Int) -> Unit,
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
            onProfileClick = { onProfileClick(it.id) }
        )
    }
}

@Composable
fun ProfileList(
    profiles: List<Profile>,
    selectedProfileId: Int?,
    onProfileClick: (Profile) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = profiles, key = { it.id }) {
            ButkusProfile(
                profile = it,
                selectedProfileId = selectedProfileId,
                onProfileClick = onProfileClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButkusProfile(
    profile: Profile,
    selectedProfileId: Int?,
    onProfileClick: (Profile) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        ListItem(
            headlineText = { Text(profile.name) },
            modifier = modifier.selectable(
                selected = profile.id == selectedProfileId,
                onClick = { onProfileClick(profile) },
                role = Role.RadioButton,
            ),
            supportingText = { Text(profile.description) },
            trailingContent = {
                if (profile.id == selectedProfileId) {
                    Icon(
                        imageVector = Icons.Filled.RadioButtonChecked,
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                    )
                }
            }
        )
    }
}