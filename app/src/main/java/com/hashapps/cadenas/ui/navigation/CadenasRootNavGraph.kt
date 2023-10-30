package com.hashapps.cadenas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hashapps.cadenas.ui.home.HOME_ROUTE
import com.hashapps.cadenas.ui.home.homeScreen
import com.hashapps.cadenas.ui.home.navigateToHome
import com.hashapps.cadenas.ui.processing.navigateToProcessing
import com.hashapps.cadenas.ui.processing.processingScreen
import com.hashapps.cadenas.ui.channels.add.channelAddScreen
import com.hashapps.cadenas.ui.channels.add.navigateToChannelAdd
import com.hashapps.cadenas.ui.channels.edit.channelEditScreen
import com.hashapps.cadenas.ui.channels.edit.navigateToChannelEdit
import com.hashapps.cadenas.ui.channels.export.channelExportScreen
import com.hashapps.cadenas.ui.channels.export.navigateToChannelExport
import com.hashapps.cadenas.ui.channels.import.channelImportScreen
import com.hashapps.cadenas.ui.channels.import.navigateToChannelImport

/**
 * Top-level navigation host for Cadenas (post setup).
 */
@Composable
fun CadenasRootNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        modifier = modifier,
    ) {
        homeScreen(
            onNavigateToSettings = { navController.navigateToSettingsGraph() },
            onNavigateToNewChannel = { navController.navigateToChannelAdd() },
            onNavigateToImportChannel = { navController.navigateToChannelImport() },
            onNavigateToChannel = { navController.navigateToProcessing(it) },
            onNavigateToExportChannel = { navController.navigateToChannelExport(it) },
            onNavigateToEditChannel = { navController.navigateToChannelEdit(it) },
        )
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
        processingScreen { navController.popBackStack() }
        settingsGraph(onNavigateToProcessing = { navController.navigateToHome() }, navController)
    }
}