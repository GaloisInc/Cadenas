package com.hashapps.cadenas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hashapps.cadenas.ui.processing.PROCESSING_ROUTE
import com.hashapps.cadenas.ui.processing.navigateToProcessing
import com.hashapps.cadenas.ui.processing.processingScreen

/**
 * Top-level navigation host for Cadenas.
 */
@Composable
fun CadenasRootNavHost(
    firstTime: Boolean,
    completeFirstRun: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = if (firstTime) SETUP_GRAPH_ROUTE else PROCESSING_ROUTE,
        modifier = modifier,
    ) {
        processingScreen { navController.navigateToSettingsGraph() }

        settingsGraph(onNavigateToProcessing = { navController.navigateToProcessing() }, navController)

        firstTimeSetupGraph(completeFirstRun, onNavigateToProcessing = { navController.navigateToProcessing() }, navController)
    }
}