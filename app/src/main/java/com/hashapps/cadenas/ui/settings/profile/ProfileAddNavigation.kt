package com.hashapps.cadenas.ui.settings.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val PROFILE_ADD_ROUTE = "profile_entry"

fun NavGraphBuilder.profileAddScreen(
    onNavigateNext: () -> Unit,
    onNavigateUp: () -> Unit,
    firstTime: Boolean = false,
) {
    composable(PROFILE_ADD_ROUTE) {
        ProfileAddScreen(navigateNext = onNavigateNext, navigateUp = onNavigateUp, firstTime = firstTime)
    }
}

fun NavController.navigateToProfileAdd(navOptions: NavOptions? = null) {
    this.navigate(PROFILE_ADD_ROUTE, navOptions)
}