package com.hashapps.cadenas.ui.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val HOME_ROUTE = "home"

fun NavGraphBuilder.homeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToNewChannel: () -> Unit,
    onNavigateToImportChannel: () -> Unit,
    onNavigateToChannel: (Int) -> Unit,
    onNavigateToExportChannel: (Int) -> Unit,
    onNavigateToEditChannel: (Int) -> Unit,
) {
    composable(HOME_ROUTE) {
        HomeScreen(
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToNewChannel = onNavigateToNewChannel,
            onNavigateToImportChannel = onNavigateToImportChannel,
            onNavigateToChannel = onNavigateToChannel,
            onNavigateToExportChannel = onNavigateToExportChannel,
            onNavigateToEditChannel = onNavigateToEditChannel,
        )
    }
}

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(HOME_ROUTE, navOptions)
}