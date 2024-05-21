package com.hashapps.cadenas.ui.channels.export

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val CHANNEL_ID_ARG = "channelId"

internal class ChannelExportArgs(val channelId: Long) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[CHANNEL_ID_ARG]) as Long)
}

const val CHANNEL_EXPORT_ROUTE = "channel_export"

fun NavGraphBuilder.channelExportScreen(
    onNavigateBack: () -> Unit,
    onNavigateBackAfterSave: () -> Unit,
) {
    composable(
        route = "$CHANNEL_EXPORT_ROUTE/{$CHANNEL_ID_ARG}",
        arguments = listOf(navArgument(CHANNEL_ID_ARG) {
            type = NavType.LongType
        })
    ) {
        ChannelExportScreen(
            onNavigateBack = onNavigateBack,
            onNavigateBackAfterSave = onNavigateBackAfterSave,
        )
    }
}

fun NavController.navigateToChannelExport(channelId: Long, navOptions: NavOptions? = null) {
    this.navigate("$CHANNEL_EXPORT_ROUTE/$channelId", navOptions)
}