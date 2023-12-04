package com.hashapps.cadenas.ui.home

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val HOME_ROUTE = "home"

fun NavGraphBuilder.homeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToNewChannel: () -> Unit,
    onNavigateToImportChannel: () -> Unit,
    onNavigateToChannel: (Long) -> Unit,
    onNavigateToExportChannel: (Long) -> Unit,
    onNavigateToEditChannel: (Long) -> Unit,
) {
    composable(
        route = HOME_ROUTE,
        deepLinks = listOf(
            navDeepLink {
                action = Intent.ACTION_SEND
                mimeType = "text/plain"
            }
        ),
    ) {
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