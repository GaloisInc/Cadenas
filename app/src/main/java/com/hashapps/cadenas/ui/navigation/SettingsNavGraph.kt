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
import com.hashapps.cadenas.ui.settings.settingsScreen

const val SETTINGS_GRAPH_ROUTE = "settings"

/**
 * Navigation graph for the settings screens.
 */
fun NavGraphBuilder.settingsGraph(
    navController: NavController,
) {
    navigation(
        startDestination = SETTINGS_ROUTE,
        route = SETTINGS_GRAPH_ROUTE,
    ) {
        settingsScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToManageModels = { navController.navigateToManageModels() },
        )
        manageModelsScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToModelAdd = { navController.navigateToModelAdd(it) })
        modelAddScreen(onNavigateBack = { navController.popBackStack() })
    }
}

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_GRAPH_ROUTE, navOptions)
}