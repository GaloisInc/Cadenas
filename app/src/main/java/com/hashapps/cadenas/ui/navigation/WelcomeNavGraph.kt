package com.hashapps.cadenas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.settings.model.ModelAddDestination
import com.hashapps.cadenas.ui.settings.model.ModelAddScreen
import com.hashapps.cadenas.ui.settings.profile.ProfileAddDestination
import com.hashapps.cadenas.ui.settings.profile.ProfileAddScreen
import com.hashapps.cadenas.ui.welcome.*

object WelcomeNavDestination : NavigationDestination {
    override val route = "welcome_nav"
    override val titleRes = R.string.unused
}

@Composable
fun WelcomeNavHost(
    completeFirstRun: () -> Unit,
    navigateToProcessing: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = IntroDestination.route,
        modifier = modifier,
    ) {
        composable(route = IntroDestination.route) {
            IntroScreen(navigateToAddModel = { navController.navigate(ModelAddDestination.route) })
        }

        composable(route = ModelAddDestination.route) {
            ModelAddScreen(navigateNext = { navController.navigate(ProfilesDestination.route) }, firstTime = true)
        }

        composable(route = ProfilesDestination.route) {
            ProfilesScreen(navigateToAddProfile = { navController.navigate(ProfileAddDestination.route) })
        }

        composable(route = ProfileAddDestination.route) {
            ProfileAddScreen(navigateNext = { navController.navigate(FinalDestination.route) }, navigateUp = {}, firstTime = true)
        }

        composable(route = FinalDestination.route) {
            FinalScreen(completeFirstRun = completeFirstRun, navigateToProcessing = navigateToProcessing)
        }
    }
}