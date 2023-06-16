package com.hashapps.cadenas.ui.settings

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
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.navigation.NavigationDestination

/**
 * The [NavigationDestination] for the top-level settings screen.
 */
object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings
    val icon = Icons.Filled.Settings
}

/**
 * Cadenas main settings screen.
 *
 * This view provides navigation back to the
 * [com.hashapps.cadenas.ui.processing.ProcessingScreen]s, as well as to the
 * views for model/profile management.
 *
 * This is intended to be extensible with additional settings destinations;
 * cosmetics of the app UI, import/export for profiles, etc.
 */
@Composable
fun SettingsScreen(
    navigateUp: () -> Unit,
    navigateToManageModels: () -> Unit,
    navigateToManageProfiles: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        SettingsBody(
            navigateToManageModels = navigateToManageModels,
            navigateToManageProfiles = navigateToManageProfiles,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun SettingsBody(
    navigateToManageModels: () -> Unit,
    navigateToManageProfiles: () -> Unit,
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
                headlineContent = { Text(stringResource(R.string.manage_models)) },
                modifier = Modifier.clickable(onClick = navigateToManageModels),
                supportingContent = { Text(stringResource(R.string.manage_models_support)) },
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

            Divider(thickness = 1.dp)

            ListItem(
                headlineContent = { Text(stringResource(R.string.profiles)) },
                modifier = Modifier.clickable(onClick = navigateToManageProfiles),
                supportingContent = { Text(stringResource(R.string.manage_profiles_support)) },
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
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationNeeded: Boolean = true,
    canNavigateUp: Boolean = true,
    navigateUp: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (navigationNeeded) {
                IconButton(
                    enabled = canNavigateUp,
                    onClick = navigateUp,
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            }
        },
    )
}