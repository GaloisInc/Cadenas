package com.hashapps.butkusapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hashapps.butkusapp.ui.ButkusScreen

/** A drawer menu for navigation between the app screens. */
@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    currentScreen: ButkusScreen,
    onDestinationClicked: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ButkusScreen.values().forEach {
            DrawerItem(
                screen = it,
                selected = it == currentScreen,
                onDestinationClicked = onDestinationClicked
            )
        }
    }
}