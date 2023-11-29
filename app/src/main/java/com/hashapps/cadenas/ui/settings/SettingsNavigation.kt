package com.hashapps.cadenas.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SETTINGS_ROUTE = "settings_home"

fun NavGraphBuilder.settingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToManageModels: () -> Unit,
) {
    composable(SETTINGS_ROUTE) {
        SettingsScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToManageModels = onNavigateToManageModels,
        )
    }
}

fun NavController.navigateToSettingsScreen(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_ROUTE, navOptions)
}