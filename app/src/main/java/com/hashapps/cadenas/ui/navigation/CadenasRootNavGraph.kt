package com.hashapps.cadenas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hashapps.cadenas.ui.processing.ProcessingDestination
import com.hashapps.cadenas.ui.processing.ProcessingScreen

@Composable
fun CadenasRootNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ProcessingDestination.route,
        modifier = modifier,
    ) {
        composable(route = ProcessingDestination.route) {
            ProcessingScreen(navigateToSettings = { navController.navigate(SettingsNavDestination.route) })
        }

        composable(route = SettingsNavDestination.route) {
            SettingsNavHost(navigateToProcessing = { navController.navigate(ProcessingDestination.route) })
        }
    }
}