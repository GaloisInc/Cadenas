package com.hashapps.cadenas.ui.channels.edit

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val CHANNEL_ID_ARG = "channelId"

internal class ChannelEditArgs(val channelId: Long) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(checkNotNull(savedStateHandle[CHANNEL_ID_ARG]) as Long)
}

const val CHANNEL_EDIT_ROUTE = "channel_edit"

fun NavGraphBuilder.channelEditScreen(
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable(
        route = "$CHANNEL_EDIT_ROUTE/{$CHANNEL_ID_ARG}",
        arguments = listOf(navArgument(CHANNEL_ID_ARG) {
            type = NavType.LongType
        })
    ) {
        ChannelEditScreen(onNavigateBack = onNavigateBack, onNavigateUp = onNavigateUp)
    }
}

fun NavController.navigateToChannelEdit(channelId: Long, navOptions: NavOptions? = null) {
    this.navigate("$CHANNEL_EDIT_ROUTE/$channelId", navOptions)
}