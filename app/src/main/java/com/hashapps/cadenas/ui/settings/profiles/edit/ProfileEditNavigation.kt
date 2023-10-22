package com.hashapps.cadenas.ui.settings.profiles.edit

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val PROFILE_ID_ARG = "profileId"

internal class ProfileEditArgs(val profileId: Int) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(checkNotNull(savedStateHandle[PROFILE_ID_ARG]) as Int)
}

const val PROFILE_EDIT_ROUTE = "profile_edit"

fun NavGraphBuilder.profileEditScreen(
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable(
        route = "$PROFILE_EDIT_ROUTE/{$PROFILE_ID_ARG}",
        arguments = listOf(navArgument(PROFILE_ID_ARG) {
            type = NavType.IntType
        })
    ) {
        ProfileEditScreen(onNavigateBack = onNavigateBack, onNavigateUp = onNavigateUp)
    }
}

fun NavController.navigateToProfileEdit(profileId: Int, navOptions: NavOptions? = null) {
    this.navigate("$PROFILE_EDIT_ROUTE/$profileId", navOptions)
}