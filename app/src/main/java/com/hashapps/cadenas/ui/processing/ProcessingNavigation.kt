package com.hashapps.cadenas.ui.processing

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val CHANNEL_ID_ARG = "channelId"

internal class ProcessingArgs(val channelId: Int) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(checkNotNull(savedStateHandle[CHANNEL_ID_ARG]) as Int)
}

const val PROCESSING_ROUTE = "processing"

fun NavGraphBuilder.processingScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = "$PROCESSING_ROUTE/{$CHANNEL_ID_ARG}",
        arguments = listOf(navArgument(CHANNEL_ID_ARG) {
            type = NavType.IntType
        }),
    ) {
        ProcessingScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToProcessing(channelId: Int, navOptions: NavOptions? = null) {
    this.navigate("$PROCESSING_ROUTE/$channelId", navOptions)
}