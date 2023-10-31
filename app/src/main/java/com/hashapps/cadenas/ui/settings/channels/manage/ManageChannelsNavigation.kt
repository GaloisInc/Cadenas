package com.hashapps.cadenas.ui.settings.channels.manage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MANAGE_CHANNELS_ROUTE = "manage_channels"

fun NavGraphBuilder.manageChannelsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToChannelEntry: () -> Unit,
    onNavigateToChannelImport: () -> Unit,
    onNavigateToChannelExport: (Int) -> Unit,
    onNavigateToChannelEdit: (Int) -> Unit,
) {
    composable(MANAGE_CHANNELS_ROUTE) {
        ManageChannelsScreen(
            onNavigateUp = onNavigateUp,
            onNavigateToChannelEntry = onNavigateToChannelEntry,
            onNavigateToChannelImport = onNavigateToChannelImport,
            onNavigateToChannelExport = onNavigateToChannelExport,
            onNavigateToChannelEdit = onNavigateToChannelEdit,
        )
    }
}

fun NavController.navigateToManageChannels(navOptions: NavOptions? = null) {
    this.navigate(MANAGE_CHANNELS_ROUTE, navOptions)
}