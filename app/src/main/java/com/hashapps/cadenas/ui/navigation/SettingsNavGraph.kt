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
import com.hashapps.cadenas.ui.settings.channels.manage.manageChannelsScreen
import com.hashapps.cadenas.ui.settings.channels.manage.navigateToManageChannels
import com.hashapps.cadenas.ui.settings.channels.add.navigateToChannelAdd
import com.hashapps.cadenas.ui.settings.channels.edit.navigateToChannelEdit
import com.hashapps.cadenas.ui.settings.channels.exporting.navigateToChannelExport
import com.hashapps.cadenas.ui.settings.channels.importing.navigateToChannelImport
import com.hashapps.cadenas.ui.settings.channels.add.channelAddScreen
import com.hashapps.cadenas.ui.settings.channels.edit.channelEditScreen
import com.hashapps.cadenas.ui.settings.channels.exporting.channelExportScreen
import com.hashapps.cadenas.ui.settings.channels.importing.channelImportScreen
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
            onNavigateToManageChannels = { navController.navigateToManageChannels() },
        )
        manageModelsScreen(
            onNavigateUp = { navController.navigateToSettingsScreen() },
            onNavigateToModelAdd = { navController.navigateToModelAdd() })
        modelAddScreen(onNavigateNext = { navController.navigateToManageModels() })
        manageChannelsScreen(
            onNavigateUp = { navController.navigateUp() },
            onNavigateToChannelEntry = { navController.navigateToChannelAdd() },
            onNavigateToChannelImport = { navController.navigateToChannelImport() },
            onNavigateToChannelExport = { navController.navigateToChannelExport(it) },
            onNavigateToChannelEdit = { navController.navigateToChannelEdit(it) })
        channelAddScreen(
            onNavigateNext = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() })
        channelImportScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() },
            onNavigateToChannelEdit = { navController.navigateToChannelEdit(it) })
        channelExportScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() })
        channelEditScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateUp = { navController.navigateUp() })
    }
}

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_GRAPH_ROUTE, navOptions)
}