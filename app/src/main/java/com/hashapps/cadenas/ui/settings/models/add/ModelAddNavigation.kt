package com.hashapps.cadenas.ui.settings.models.add

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val MODEL_URL_ARG = "modelUrl"

internal class ProcessingArgs(val modelUrl: String) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(
            checkNotNull(savedStateHandle[MODEL_URL_ARG]) as String
        )
}

const val MODEL_ADD_ROUTE = "model_add"

fun NavGraphBuilder.modelAddScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = "$MODEL_ADD_ROUTE?$MODEL_URL_ARG={$MODEL_URL_ARG}",
        arguments = listOf(navArgument(MODEL_URL_ARG) {
            type = NavType.StringType
            defaultValue = ""
        }),
    ) {
        ModelAddScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToModelAdd(modelUrl:String, navOptions: NavOptions? = null) {
    this.navigate("$MODEL_ADD_ROUTE?$MODEL_URL_ARG=$modelUrl", navOptions)
}