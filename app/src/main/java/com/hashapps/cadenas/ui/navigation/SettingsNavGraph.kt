package com.hashapps.cadenas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.settings.SettingsDestination
import com.hashapps.cadenas.ui.settings.SettingsScreen
import com.hashapps.cadenas.ui.settings.model.ManageModelsDestination
import com.hashapps.cadenas.ui.settings.model.ManageModelsScreen
import com.hashapps.cadenas.ui.settings.model.ModelAddDestination
import com.hashapps.cadenas.ui.settings.model.ModelAddScreen
import com.hashapps.cadenas.ui.settings.profile.*

/**
 * The [NavigationDestination] for the settings navigation graph.
 *
 * This is a 'special' destination, in that it doesn't represent any specific
 * screen - rather, it acts as a destination for settings _as a whole_ to be
 * used in the root navigation graph.
 */
object SettingsNavDestination : NavigationDestination {
    override val route = "settings_nav"
    override val titleRes: Int = R.string.unused
}

/**
 * Navigation host for Cadenas settings screens.
 */
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
                navigateUp = navigateToProcessing,
                navigateToManageModels = { navController.navigate(ManageModelsDestination.route) },
                navigateToManageProfiles = { navController.navigate(ManageProfilesDestination.route) },
            )
        }

        composable(route = ManageModelsDestination.route) {
            ManageModelsScreen(
                navigateUp = { navController.navigate(SettingsDestination.route) },
                navigateToModelAdd = { navController.navigate(ModelAddDestination.route) },
            )
        }

        composable(route = ModelAddDestination.route) {
            ModelAddScreen(
                onNavigateNext = { navController.navigate(ManageModelsDestination.route) },
            )
        }

        composable(route = ManageProfilesDestination.route) {
            ManageProfilesScreen(
                navigateUp = { navController.navigateUp() },
                navigateToProfileEntry = { navController.navigate(ProfileAddDestination.route) },
                navigateToProfileEdit = { profile -> navController.navigate("${ProfileEditDestination.route}/$profile") },
            )
        }

        composable(route = ProfileAddDestination.route) {
            ProfileAddScreen(
                navigateNext = { navController.popBackStack() },
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
    }
}