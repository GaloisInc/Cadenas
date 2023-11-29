package com.hashapps.cadenas.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hashapps.cadenas.R

/**
 * Cadenas main settings screen.
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToManageModels: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.settings),
                onNavigateBack = onNavigateBack,
            )
        }
    ) { innerPadding ->
        SettingsBody(
            navigateToManageModels = onNavigateToManageModels,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun SettingsBody(
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
                        imageVector = Icons.AutoMirrored.Filled.NavigateNext,
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
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                enabled = canNavigateBack,
                onClick = onNavigateBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
        actions = actions,
    )
}