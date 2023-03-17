package com.hashapps.butkusapp.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.navigation.NavigationDestination

//private const val MAX_LEN = 128
//
//private val urlRegex =
//    Regex("""https?://(www\.)?[-a-zA-Z\d@:%._+~#=]{1,256}\.[a-zA-Z\d()]{1,6}\b([-a-zA-Z\d()!@:%_+.~#?&/=]*)""")
//private val SettingsUiState.urlValid get() = urlRegex.matches(modelUrlToAdd)
//private val SettingsUiState.isErrorUrl get() = modelUrlToAdd != "" && !urlValid
//private val SettingsUiState.canAddUrl get () = urlValid && modelUrlToAdd !in modelUrls

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings
    val icon = Icons.Filled.Settings
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateToProcessing: () -> Unit,
    navigateToManageProfiles: () -> Unit,
    navigateToManageModels: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateUp = true,
                navigateUp = navigateToProcessing,
            )
        }
    ) { innerPadding ->
        SettingsBody(
            navigateToManageProfiles = navigateToManageProfiles,
            navigateToManageModels = navigateToManageModels,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBody(
    navigateToManageProfiles: () -> Unit,
    navigateToManageModels: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ListItem(
                headlineText = { Text(stringResource(R.string.manage_profiles)) },
                modifier = Modifier.clickable(onClick = navigateToManageProfiles),
                supportingText = { Text(stringResource(R.string.manage_profiles_support)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.SwitchAccount,
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Filled.NavigateNext,
                        contentDescription = null,
                    )
                }
            )
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ListItem(
                headlineText = { Text(stringResource(R.string.manage_models)) },
                modifier = Modifier.clickable(onClick = navigateToManageModels),
                supportingText = { Text(stringResource(R.string.manage_models_support)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Filled.NavigateNext,
                        contentDescription = null,
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    title: String,
    canNavigateUp: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    selectedProfile: Int? = null,
    canEditItem: Boolean = false,
    navigateToEditItem: (Int) -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateUp) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            }
        },
        actions = {
            if (canEditItem) {
                IconButton(onClick = { navigateToEditItem(selectedProfile!!) }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.edit_item),
                    )
                }
            }
        },
    )
}