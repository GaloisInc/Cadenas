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
import com.hashapps.cadenas.ui.settings.profiles.manage.manageProfilesScreen
import com.hashapps.cadenas.ui.settings.profiles.manage.navigateToManageProfiles
import com.hashapps.cadenas.ui.settings.profiles.add.navigateToProfileAdd
import com.hashapps.cadenas.ui.settings.profiles.edit.navigateToProfileEdit
import com.hashapps.cadenas.ui.settings.profiles.exporting.navigateToProfileExport
import com.hashapps.cadenas.ui.settings.profiles.importing.navigateToProfileImport
import com.hashapps.cadenas.ui.settings.profiles.add.profileAddScreen
import com.hashapps.cadenas.ui.settings.profiles.edit.profileEditScreen
import com.hashapps.cadenas.ui.settings.profiles.exporting.profileExportScreen
import com.hashapps.cadenas.ui.settings.profiles.importing.profileImportScreen
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
            onNavigateToProfileImport = { navController.navigateToProfileImport() },
            onNavigateToProfileExport = { navController.navigateToProfileExport(it) },
            onNavigateToProfileEdit = { navController.navigateToProfileEdit(it) })
        profileAddScreen(
            onNavigateNext = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() })
        profileImportScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() },
            onNavigateProfileEdit = { navController.navigateToProfileEdit(it) })
        profileExportScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() })
        profileEditScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() })
    }
}

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_GRAPH_ROUTE, navOptions)
}