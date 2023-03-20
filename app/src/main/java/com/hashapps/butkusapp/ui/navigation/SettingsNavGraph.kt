package com.hashapps.butkusapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.model.ManageModelsDestination
import com.hashapps.butkusapp.ui.model.ManageModelsScreen
import com.hashapps.butkusapp.ui.profile.*
import com.hashapps.butkusapp.ui.settings.SettingsDestination
import com.hashapps.butkusapp.ui.settings.SettingsScreen

object SettingsNavDestination : NavigationDestination {
    override val route = "settings_nav"
    override val titleRes: Int = R.string.unused
}

@Composable
fun SettingsNavHost(
    navigateToProcessing: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = SettingsDestination.route,
        modifier = modifier,
    ) {
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateToProcessing = navigateToProcessing,
                navigateToManageProfiles = { navController.navigate(ManageProfilesDestination.route) },
                navigateToManageModels = { navController.navigate(ManageModelsDestination.route) },
            )
        }

        composable(route = ManageProfilesDestination.route) {
            ManageProfilesScreen(
                navigateUp = { navController.navigateUp() },
                navigateToProfileEntry = { navController.navigate(ProfileEntryDestination.route) },
                navigateToProfileEdit = { navController.navigate("${ProfileEditDestination.route}/$it") },
            )
        }

        composable(route = ProfileEntryDestination.route) {
            ProfileEntryScreen(
                navigateBack = { navController.popBackStack() },
                navigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = ProfileEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ProfileEditDestination.profileIdArg) {
                type = NavType.IntType
            })
        ) {
            ProfileEditScreen(
                navigateBack = { navController.popBackStack() },
                navigateUp = { navController.navigateUp() },
            )
        }

        composable(route = ManageModelsDestination.route) {
            ManageModelsScreen(
                navigateUp = { navController.navigateUp() },
                navigateToModelAdd = { /*TODO*/ }
            )
        }
    }
}