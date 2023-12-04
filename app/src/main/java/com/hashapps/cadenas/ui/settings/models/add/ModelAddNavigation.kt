package com.hashapps.cadenas.ui.settings.models.add

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MODEL_ADD_ROUTE = "model_add"

fun NavGraphBuilder.modelAddScreen(
    onNavigateBack: () -> Unit,
) {
    composable(MODEL_ADD_ROUTE) {
        ModelAddScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToModelAdd(navOptions: NavOptions? = null) {
    this.navigate(MODEL_ADD_ROUTE, navOptions)
}