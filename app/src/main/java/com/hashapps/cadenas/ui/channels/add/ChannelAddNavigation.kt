package com.hashapps.cadenas.ui.channels.add

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val CHANNEL_ADD_ROUTE = "channel_entry"

fun NavGraphBuilder.channelAddScreen(
    onNavigateNext: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToAddModel: () -> Unit,
) {
    composable(CHANNEL_ADD_ROUTE) {
        ChannelAddScreen(navigateNext = onNavigateNext, navigateUp = onNavigateUp, navigateToAddModel = onNavigateToAddModel)
    }
}

fun NavController.navigateToChannelAdd(navOptions: NavOptions? = null) {
    this.navigate(CHANNEL_ADD_ROUTE, navOptions)
}