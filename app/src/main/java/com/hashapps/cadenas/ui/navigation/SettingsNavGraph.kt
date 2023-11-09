package com.hashapps.cadenas.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.hashapps.cadenas.ui.settings.SETTINGS_ROUTE
import com.hashapps.cadenas.ui.settings.models.manage.manageModelsScreen
import com.hashapps.cadenas.ui.settings.models.add.modelAddScreen
import com.hashapps.cadenas.ui.settings.models.manage.navigateToManageModels
import com.hashapps.cadenas.ui.settings.models.add.navigateToModelAdd
import com.hashapps.cadenas.ui.settings.navigateToSettingsScreen
import com.hashapps.cadenas.ui.settings.settingsScreen

const val SETTINGS_GRAPH_ROUTE = "settings"

/**
 * Navigation graph for the settings screens.
 */
fun NavGraphBuilder.settingsGraph(
    onNavigateToProcessing: () -> Unit,
    navController: NavController,
) {
    navigation(
        startDestination = SETTINGS_ROUTE,
        route = SETTINGS_GRAPH_ROUTE,
    ) {
        settingsScreen(
            onNavigateUp = onNavigateToProcessing,
            onNavigateToManageModels = { navController.navigateToManageModels() },
        )
        manageModelsScreen(
            onNavigateUp = { navController.navigateToSettingsScreen() },
            onNavigateToModelAdd = { navController.navigateToModelAdd() })
        modelAddScreen(onNavigateNext = { navController.navigateToManageModels() })
    }
}

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_GRAPH_ROUTE, navOptions)
}