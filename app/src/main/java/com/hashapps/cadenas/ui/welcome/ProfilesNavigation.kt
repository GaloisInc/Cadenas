package com.hashapps.cadenas.ui.welcome

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val PROFILES_ROUTE = "profiles"

fun NavGraphBuilder.profilesScreen(
    onNavigateToAddProfile: () -> Unit,
) {
    composable(PROFILES_ROUTE) {
        ProfilesScreen(onNavigateToAddProfile = onNavigateToAddProfile)
    }
}

fun NavController.navigateToProfiles(navOptions: NavOptions? = null) {
    this.navigate(PROFILES_ROUTE, navOptions)
}