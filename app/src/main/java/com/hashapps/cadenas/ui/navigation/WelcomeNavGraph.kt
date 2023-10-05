package com.hashapps.cadenas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
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

@Composable
fun FirstTimeSetup(
    completeFirstRun: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = INTRO_ROUTE,
        modifier = modifier,
    ) {
        introScreen { navController.navigateToModelAdd() }
        modelAddScreen(onNavigateNext = { navController.navigateToProfiles() }, firstTime = true)
        profilesScreen { navController.navigateToProfileAdd() }
        profileAddScreen(
            onNavigateNext = { navController.navigateToFinal() },
            onNavigateUp = {},
            firstTime = true
        )
        finalScreen(completeFirstRun)
    }
}