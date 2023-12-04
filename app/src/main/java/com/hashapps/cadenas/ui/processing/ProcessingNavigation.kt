package com.hashapps.cadenas.ui.processing

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

private const val CHANNEL_ID_ARG = "channelId"

internal class ProcessingArgs(val channelId: Long) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[CHANNEL_ID_ARG]) as Long)
}

const val PROCESSING_ROUTE = "processing"

fun NavGraphBuilder.processingScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = "$PROCESSING_ROUTE/{$CHANNEL_ID_ARG}",
        arguments = listOf(navArgument(CHANNEL_ID_ARG) {
            type = NavType.LongType
        }),
    ) {
        ProcessingScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToProcessing(channelId: Long, navOptions: NavOptions? = null) {
    this.navigate("$PROCESSING_ROUTE/$channelId", navOptions)
}