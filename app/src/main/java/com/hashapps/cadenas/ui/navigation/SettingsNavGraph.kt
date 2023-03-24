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
import com.hashapps.cadenas.ui.model.*
import com.hashapps.cadenas.ui.profile.*
import com.hashapps.cadenas.ui.settings.SettingsDestination
import com.hashapps.cadenas.ui.settings.SettingsScreen

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
                navigateUp = navigateToProcessing,
                navigateToManageModels = { navController.navigate(ManageModelsDestination.route) },
            )
        }

        composable(route = ManageModelsDestination.route) {
            ManageModelsScreen(
                navigateUp = { navController.navigateUp() },
                navigateToModelAdd = { navController.navigate(ModelAddDestination.route) },
                navigateToManageProfiles = { navController.navigate("${ManageProfilesDestination.route}/$it") },
            )
        }

        composable(route = ModelAddDestination.route) {
            ModelAddScreen(
                navigateBack = { navController.popBackStack() },
                navigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = ManageProfilesDestination.routeWithArgs,
            arguments = listOf(navArgument(ManageProfilesDestination.modelIdArg) {
                type = NavType.IntType
            })
        ) {
            ManageProfilesScreen(
                navigateUp = { navController.navigateUp() },
                navigateToProfileEntry = { navController.navigate("${ProfileAddDestination.route}/$it") },
                navigateToProfileEdit = { model, profile -> navController.navigate("${ProfileEditDestination.route}/$model/$profile") },
            )
        }

        composable(
            route = ProfileAddDestination.routeWithArgs,
            arguments = listOf(navArgument(ProfileAddDestination.modelIdArg) {
                type = NavType.IntType
            })
        ) {
            ProfileAddScreen(
                navigateBack = { navController.popBackStack() },
                navigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = ProfileEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ProfileEditDestination.modelIdArg) {
                type = NavType.IntType
            }, navArgument(ProfileEditDestination.profileIdArg) {
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