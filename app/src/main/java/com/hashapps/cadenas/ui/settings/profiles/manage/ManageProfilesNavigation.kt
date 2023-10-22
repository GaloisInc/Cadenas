package com.hashapps.cadenas.ui.settings.profiles.manage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MANAGE_PROFILES_ROUTE = "manage_profiles"

fun NavGraphBuilder.manageProfilesScreen(
    onNavigateUp: () -> Unit,
    onNavigateToProfileEntry: () -> Unit,
    onNavigateToProfileImport: () -> Unit,
    onNavigateToProfileExport: (Int) -> Unit,
    onNavigateToProfileEdit: (Int) -> Unit,
) {
    composable(MANAGE_PROFILES_ROUTE) {
        ManageProfilesScreen(
            onNavigateUp = onNavigateUp,
            onNavigateToProfileEntry = onNavigateToProfileEntry,
            onNavigateToProfileImport = onNavigateToProfileImport,
            onNavigateToProfileExport = onNavigateToProfileExport,
            onNavigateToProfileEdit = onNavigateToProfileEdit,
        )
    }
}

fun NavController.navigateToManageProfiles(navOptions: NavOptions? = null) {
    this.navigate(MANAGE_PROFILES_ROUTE, navOptions)
}