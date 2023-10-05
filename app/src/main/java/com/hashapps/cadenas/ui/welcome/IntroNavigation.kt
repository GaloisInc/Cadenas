package com.hashapps.cadenas.ui.welcome

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val INTRO_ROUTE = "intro"

fun NavGraphBuilder.introScreen(
    onNavigateToAddModel: () -> Unit,
) {
    composable(INTRO_ROUTE) {
        IntroScreen(onNavigateToAddModel = onNavigateToAddModel)
    }
}