package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.ui.models.ButkusAppViewModel
import com.hashapps.butkusapp.ui.ButkusScreen
import com.hashapps.butkusapp.ui.components.ButkusAppBar
import com.hashapps.butkusapp.ui.components.Drawer
import com.hashapps.butkusapp.ui.screens.DecodeScreen
import com.hashapps.butkusapp.ui.screens.EncodeScreen
import com.hashapps.butkusapp.ui.screens.SettingsScreen
import kotlinx.coroutines.launch

/** The Butkus application.
 *
 * Built on a Material design scaffold, the Butkus application consists of
 * three distinct screens:
 *
 * - Message encoding
 * - Message decoding
 * - Settings */
@Composable
fun ButkusApp(
    modifier: Modifier = Modifier,
    viewModel: ButkusAppViewModel,
) {
    // Get the app context
    val context = LocalContext.current

    // Get the whole-app coroutine scope
    val scope = rememberCoroutineScope()

    // Navigation setup
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ButkusScreen.valueOf(
        backStackEntry?.destination?.route ?: ButkusScreen.Encode.name
    )

    // Manual scaffold state so we control the drawer
    val scaffoldState = ScaffoldState(
        drawerState = rememberDrawerState(DrawerValue.Closed),
        snackbarHostState = SnackbarHostState(),
    )

    val butkusInitialized by viewModel.butkusInitialized.collectAsState()
    val encodeUiState by viewModel.encodeUiState.collectAsState()
    val decodeUiState by viewModel.decodeUiState.collectAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ButkusAppBar(
                uiEnabled = when (currentScreen) {
                    ButkusScreen.Encode -> !encodeUiState.inProgress
                    ButkusScreen.Decode -> !decodeUiState.inProgress
                    ButkusScreen.Settings -> true
                },
                currentScreen = currentScreen,
                onOpenDrawer = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                onReset = {
                    when (currentScreen) {
                        ButkusScreen.Encode -> viewModel.resetEncodeScreen()
                        ButkusScreen.Decode -> viewModel.resetDecodeScreen()
                        ButkusScreen.Settings -> {}
                    }
                },
                canShare = encodeUiState.encodedMessage != null,
                onShare = { shareMessage(context, encodeUiState.encodedMessage) }
            )
        },
        drawerGesturesEnabled = when (currentScreen) {
            ButkusScreen.Encode -> !encodeUiState.inProgress
            ButkusScreen.Decode -> !decodeUiState.inProgress
            ButkusScreen.Settings -> true
        },
        drawerContent = {
            Drawer(
                currentScreen = currentScreen,
                onDestinationClicked = { route ->
                    navController.navigate(route)
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ButkusScreen.Encode.name,
            modifier = modifier.padding(innerPadding),
        ) {
            composable(route = ButkusScreen.Encode.name) {
                EncodeScreen(
                    encodeUiState = encodeUiState,
                    onPlaintextChange = { viewModel.updatePlaintextMessage(it) },
                    onTagChange = { viewModel.updateTagToAdd(it) },
                    onAddTag = {
                        if (encodeUiState.tagToAdd != "" && encodeUiState.tagToAdd.all { it.isLetter() } && encodeUiState.tagToAdd !in encodeUiState.addedTags) {
                            viewModel.addTag(encodeUiState.tagToAdd)
                            viewModel.updateTagToAdd("")
                        }
                    },
                    onTagRemove = {
                        if (it in encodeUiState.addedTags) {
                            viewModel.removeTag(it)
                        }
                    },
                    canEncode = butkusInitialized && encodeUiState.message.isNotEmpty(),
                    onEncode = { scope.launch { viewModel.encodeMessage() } },
                )
            }

            composable(route = ButkusScreen.Decode.name) {
                DecodeScreen(
                    decodeUiState = decodeUiState,
                    onCoverTextChange = { viewModel.updateEncodedMessage(it) },
                    canDecode = butkusInitialized && decodeUiState.message.isNotEmpty(),
                    onDecode = { scope.launch { viewModel.decodeMessage() } },
                )
            }

            composable(route = ButkusScreen.Settings.name) {
                SettingsScreen()
            }
        }
    }
}

private fun shareMessage(context: Context, message: String?) {
    if (message != null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.butkus_message)
            )
        )
    }
}