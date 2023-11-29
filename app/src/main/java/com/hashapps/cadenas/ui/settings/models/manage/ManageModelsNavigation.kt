package com.hashapps.cadenas.ui.settings.models.manage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MANAGE_MODELS_ROUTE = "manage_models"

fun NavGraphBuilder.manageModelsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToModelAdd: () -> Unit,
) {
    composable(MANAGE_MODELS_ROUTE) {
        ManageModelsScreen(onNavigateBack = onNavigateBack, onNavigateToModelAdd = onNavigateToModelAdd)
    }
}

fun NavController.navigateToManageModels(navOptions: NavOptions? = null) {
    this.navigate(MANAGE_MODELS_ROUTE, navOptions)
}