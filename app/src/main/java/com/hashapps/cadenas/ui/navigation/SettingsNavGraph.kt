package com.hashapps.cadenas.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.hashapps.cadenas.ui.settings.SETTINGS_ROUTE
import com.hashapps.cadenas.ui.settings.model.manageModelsScreen
import com.hashapps.cadenas.ui.settings.model.modelAddScreen
import com.hashapps.cadenas.ui.settings.model.navigateToManageModels
import com.hashapps.cadenas.ui.settings.model.navigateToModelAdd
import com.hashapps.cadenas.ui.settings.navigateToSettingsScreen
import com.hashapps.cadenas.ui.settings.profile.manageProfilesScreen
import com.hashapps.cadenas.ui.settings.profile.navigateToManageProfiles
import com.hashapps.cadenas.ui.settings.profile.navigateToProfileAdd
import com.hashapps.cadenas.ui.settings.profile.navigateToProfileEdit
import com.hashapps.cadenas.ui.settings.profile.profileAddScreen
import com.hashapps.cadenas.ui.settings.profile.profileEditScreen
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
            onNavigateToManageProfiles = { navController.navigateToManageProfiles() },
        )
        manageModelsScreen(
            onNavigateUp = { navController.navigateToSettingsScreen() },
            onNavigateToModelAdd = { navController.navigateToModelAdd() })
        modelAddScreen(onNavigateNext = { navController.navigateToManageModels() })
        manageProfilesScreen(
            onNavigateUp = { navController.navigateUp() },
            onNavigateToProfileEntry = { navController.navigateToProfileAdd() },
            onNavigateToProfileExport = {},
            onNavigateToProfileEdit = { navController.navigateToProfileEdit(it) })
        profileAddScreen(
            onNavigateNext = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() })
        profileEditScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() })
    }
}

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_GRAPH_ROUTE, navOptions)
}