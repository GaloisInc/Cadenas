package com.hashapps.butkusapp

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.ui.ButkusViewModel

enum class ButkusScreen(@StringRes val title: Int) {
    Encode(title = R.string.encode),
    Decode(title = R.string.decode),
}

@Composable
fun ButkusAppBar(
    currentScreen: ButkusScreen,
    canSwitchScreen: Boolean,
    switchScreen: () -> Unit,
    share: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canSwitchScreen) {
                IconButton(onClick = switchScreen) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.switch_button),
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = share) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share_button),
                )
            }
        }
    )
}

@Composable
fun ButkusApp(
    modifier: Modifier = Modifier,
    viewModel: ButkusViewModel = ButkusViewModel(),
) {
    val navController = rememberNavController();

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = ButkusScreen.valueOf(
        backStackEntry?.destination?.route ?: ButkusScreen.Encode.name
    )

    Scaffold(
        topBar = {
            ButkusAppBar(
                currentScreen = currentScreen,
                canSwitchScreen = true,
                switchScreen = { /* TODO: Implement switch-screen button */ },
                share = { /* TODO: Implement share button */ },
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = ButkusScreen.Encode.name,
            modifier = modifier.padding(innerPadding),
        ) {
            composable(route = ButkusScreen.Encode.name) {
                // TODO: Composable for the encode screen
            }

            composable(route = ButkusScreen.Decode.name) {
                // TODO: Composable for the decode screen
            }
        }
    }
}