package com.hashapps.cadenas.ui.processing

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val PROCESSING_ROUTE = "processing"

fun NavGraphBuilder.processingScreen(
    onNavigateToSettings: () -> Unit,
) {
    composable(
        route = PROCESSING_ROUTE,
        deepLinks = listOf(
            navDeepLink {
                action = Intent.ACTION_SEND
                mimeType = "text/plain"
            }
        ),
    ) {
        ProcessingScreen(onNavigateToSettings = onNavigateToSettings)
    }
}

fun NavController.navigateToProcessing(navOptions: NavOptions? = null) {
    this.navigate(PROCESSING_ROUTE, navOptions)
}