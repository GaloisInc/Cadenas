package com.hashapps.cadenas.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.settings.model.modelAddScreen
import com.hashapps.cadenas.ui.settings.model.navigateToModelAdd
import com.hashapps.cadenas.ui.settings.profile.navigateToProfileAdd
import com.hashapps.cadenas.ui.settings.profile.profileAddScreen
import com.hashapps.cadenas.ui.welcome.INTRO_ROUTE
import com.hashapps.cadenas.ui.welcome.finalScreen
import com.hashapps.cadenas.ui.welcome.introScreen
import com.hashapps.cadenas.ui.welcome.navigateToFinal
import com.hashapps.cadenas.ui.welcome.navigateToProfiles
import com.hashapps.cadenas.ui.welcome.profilesScreen

const val SETUP_GRAPH_ROUTE = "setup"

/**
 * Navigation graph for the first-time setup screens.
 */
fun NavGraphBuilder.firstTimeSetupGraph(
    completeFirstRun: () -> Unit,
    onNavigateToProcessing: () -> Unit,
    navController: NavController,
) {
    navigation(
        startDestination = INTRO_ROUTE,
        route = SETUP_GRAPH_ROUTE,
    ) {
        introScreen { navController.navigateToModelAdd() }
        modelAddScreen(onNavigateNext = { navController.navigateToProfiles() }, firstTime = true)
        profilesScreen { navController.navigateToProfileAdd() }
        profileAddScreen(
            onNavigateNext = { navController.navigateToFinal() },
            onNavigateUp = {},
            firstTime = true
        )
        finalScreen(completeFirstRun, onNavigateToProcessing)
    }
}