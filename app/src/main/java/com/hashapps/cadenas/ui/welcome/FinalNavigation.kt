package com.hashapps.cadenas.ui.welcome

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val FINAL_ROUTE = "final"

fun NavGraphBuilder.finalScreen(
    completeFirstRun: () -> Unit,
    onNavigateToProcessing: () -> Unit,
) {
    composable(FINAL_ROUTE) {
        FinalScreen(completeFirstRun = completeFirstRun, navigateToProcessing = onNavigateToProcessing)
    }
}

fun NavController.navigateToFinal(navOptions: NavOptions? = null) {
    this.navigate(FINAL_ROUTE)
}