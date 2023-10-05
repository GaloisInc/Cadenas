package com.hashapps.cadenas.ui.processing

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val PROCESSING_ROUTE = "processing"

fun NavGraphBuilder.processingScreen(
    onNavigateToSettings: () -> Unit,
) {
    composable(PROCESSING_ROUTE) {
        ProcessingScreen(onNavigateToSettings = onNavigateToSettings)
    }
}

fun NavController.navigateToProcessing(navOptions: NavOptions? = null) {
    this.navigate(PROCESSING_ROUTE, navOptions)
}