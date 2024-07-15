package com.hashapps.cadenas.ui.settings.models.disk

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MODEL_ADD_DISK_ROUTE = "model_add_disk"

fun NavGraphBuilder.modelAddDiskScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = MODEL_ADD_DISK_ROUTE,
    ) {
        ModelAddDiskScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToModelAddDisk(navOptions: NavOptions? = null) {
    this.navigate(MODEL_ADD_DISK_ROUTE, navOptions)
}