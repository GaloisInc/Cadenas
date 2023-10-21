package com.hashapps.cadenas.ui.settings.profile

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val PROFILE_ID_ARG = "profileId"

internal class ProfileExportArgs(val profileId: Int) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(checkNotNull(savedStateHandle[PROFILE_ID_ARG]) as Int)
}

const val PROFILE_EXPORT_ROUTE = "profile_export"

fun NavGraphBuilder.profileExportScreen(
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable(
        route = "$PROFILE_EXPORT_ROUTE/{$PROFILE_ID_ARG}",
        arguments = listOf(navArgument(PROFILE_ID_ARG) {
            type = NavType.IntType
        })
    ) {
        ProfileExportScreen(onNavigateBack = onNavigateBack, onNavigateUp = onNavigateUp)
    }
}

fun NavController.navigateToProfileExport(profileId: Int, navOptions: NavOptions? = null) {
    this.navigate("$PROFILE_EXPORT_ROUTE/$profileId", navOptions)
}