package com.hashapps.cadenas.ui.channels.import

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val CHANNEL_IMPORT_ROUTE = "channel_import"

fun NavGraphBuilder.channelImportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChannelEdit: (Long) -> Unit,
    onNavigateToAddModel: () -> Unit,
) {
    composable(
        route = CHANNEL_IMPORT_ROUTE,
    ) {
        ChannelImportScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToChannelEdit = onNavigateToChannelEdit,
            onNavigateToAddModel = onNavigateToAddModel,
        )
    }
}

fun NavController.navigateToChannelImport(navOptions: NavOptions? = null) {
    this.navigate(CHANNEL_IMPORT_ROUTE, navOptions)
}