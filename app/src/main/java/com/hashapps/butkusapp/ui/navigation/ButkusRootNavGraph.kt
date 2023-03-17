package com.hashapps.butkusapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hashapps.butkusapp.ui.processing.ProcessingDestination
import com.hashapps.butkusapp.ui.processing.ProcessingScreen
import com.hashapps.butkusapp.ui.profile.ManageProfilesDestination
import com.hashapps.butkusapp.ui.settings.SettingsDestination
import com.hashapps.butkusapp.ui.settings.SettingsScreen

@Composable
fun ButkusRootNavHost(
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