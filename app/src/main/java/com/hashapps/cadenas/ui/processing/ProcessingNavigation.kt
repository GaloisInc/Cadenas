package com.hashapps.cadenas.ui.processing

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val CHANNEL_ID_ARG = "channelId"
private const val TO_DECODE_ARG = "toDecode"

internal class ProcessingArgs(val channelId: Long, val toDecode: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[CHANNEL_ID_ARG]) as Long,
                checkNotNull(savedStateHandle[TO_DECODE_ARG]) as String
            )
}

const val PROCESSING_ROUTE = "processing"

fun NavGraphBuilder.processingScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = "$PROCESSING_ROUTE/{$CHANNEL_ID_ARG}/{$TO_DECODE_ARG}",
        arguments = listOf(navArgument(CHANNEL_ID_ARG) {
            type = NavType.LongType
        }, navArgument(TO_DECODE_ARG) {
            type = NavType.StringType
        }),
    ) {
        ProcessingScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToProcessing(channelId: Long, toDecode: String, navOptions: NavOptions? = null) {
    this.navigate("$PROCESSING_ROUTE/$channelId/$toDecode", navOptions)
}