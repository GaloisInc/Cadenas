package com.hashapps.cadenas.ui.settings.model

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MODEL_ADD_ROUTE = "model_add"

fun NavGraphBuilder.modelAddScreen(
    onNavigateNext: () -> Unit,
    firstTime: Boolean = false,
) {
    composable(MODEL_ADD_ROUTE) {
        ModelAddScreen(onNavigateNext = onNavigateNext, firstTime = firstTime)
    }
}

fun NavController.navigateToModelAdd(navOptions: NavOptions? = null) {
    this.navigate(MODEL_ADD_ROUTE, navOptions)
}