package com.hashapps.cadenas.ui.home

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

const val HOME_ROUTE = "home"
const val SAVE_SUCCESS_ARG = "codeSaved"

fun NavGraphBuilder.homeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToNewChannel: () -> Unit,
    onNavigateToImportChannel: () -> Unit,
    onNavigateToChannel: (Long, String) -> Unit,
    onNavigateToExportChannel: (Long) -> Unit,
    onNavigateToEditChannel: (Long) -> Unit,
) {
    composable(
        route = "$HOME_ROUTE?$SAVE_SUCCESS_ARG={$SAVE_SUCCESS_ARG}",
        arguments = listOf(navArgument(SAVE_SUCCESS_ARG) { defaultValue = false }),
        deepLinks = listOf(
            navDeepLink {
                action = Intent.ACTION_SEND
                mimeType = "text/plain"
            }
        ),
    ) {
        from -> val saveSuccessArg = from.arguments?.getBoolean(SAVE_SUCCESS_ARG)

        HomeScreen(
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToNewChannel = onNavigateToNewChannel,
            onNavigateToImportChannel = onNavigateToImportChannel,
            onNavigateToChannel = onNavigateToChannel,
            onNavigateToExportChannel = onNavigateToExportChannel,
            onNavigateToEditChannel = onNavigateToEditChannel,
            savedQRCodeNotificationRequired = if (saveSuccessArg == null) false else saveSuccessArg,
        )
    }
}

fun NavController.navigateToHome(saveSuccess: Boolean?, navOptions: NavOptions? = null) {
    this.navigate("$HOME_ROUTE?$SAVE_SUCCESS_ARG=${saveSuccess}", navOptions)
}
