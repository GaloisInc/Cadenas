package com.hashapps.cadenas.ui.channels.exporting

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val CHANNEL_ID_ARG = "channelId"

internal class ChannelExportArgs(val channelId: Int) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(checkNotNull(savedStateHandle[CHANNEL_ID_ARG]) as Int)
}

const val CHANNEL_EXPORT_ROUTE = "channel_export"

fun NavGraphBuilder.channelExportScreen(
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable(
        route = "$CHANNEL_EXPORT_ROUTE/{$CHANNEL_ID_ARG}",
        arguments = listOf(navArgument(CHANNEL_ID_ARG) {
            type = NavType.IntType
        })
    ) {
        ChannelExportScreen(onNavigateBack = onNavigateBack, onNavigateUp = onNavigateUp)
    }
}

fun NavController.navigateToChannelExport(channelId: Int, navOptions: NavOptions? = null) {
    this.navigate("$CHANNEL_EXPORT_ROUTE/$channelId", navOptions)
}