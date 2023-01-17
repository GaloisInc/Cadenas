package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.data.ButkusScreen
import com.hashapps.butkusapp.ui.components.ButkusAppBar
import com.hashapps.butkusapp.ui.components.Drawer
import com.hashapps.butkusapp.ui.models.ButkusViewModel
import com.hashapps.butkusapp.ui.models.DecodeViewModel
import com.hashapps.butkusapp.ui.models.EncodeViewModel
import com.hashapps.butkusapp.ui.screens.DecodeScreen
import com.hashapps.butkusapp.ui.screens.EncodeScreen
import com.hashapps.butkusapp.ui.screens.SettingsScreen
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme
import kotlinx.coroutines.launch

/** The Butkus application.
 *
 * Built on a Material design scaffold, the Butkus application consists of
 * three distinct screens:
 *
 * - Message encoding
 * - Message decoding
 * - TODO: Persistent settings */
@Composable
fun ButkusApp(
    modifier: Modifier = Modifier,
    encodeViewModel: EncodeViewModel = EncodeViewModel(),
    decodeViewModel: DecodeViewModel = DecodeViewModel(),
) {
    // Get the app context
    val context = LocalContext.current

    // Get the whole-app coroutine scope
    val scope = rememberCoroutineScope()

    // Navigation setup
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    ButkusViewModel.SharedViewState.currentScreen = ButkusScreen.valueOf(
        backStackEntry?.destination?.route ?: ButkusScreen.Encode.name
    )

    // Manual scaffold state so we control the drawer
    val scaffoldState = ScaffoldState(
        drawerState = rememberDrawerState(DrawerValue.Closed),
        snackbarHostState = SnackbarHostState(),
    )

    // Initialize Butkus
    if (!ButkusViewModel.SharedViewState.butkusInitialized) {
        LaunchedEffect(Unit) {
            Butkus.initialize(context)
            ButkusViewModel.SharedViewState.butkusInitialized = true
        }
    }

    // Listening for action button hits, encode or decode as appropriate
    if (ButkusViewModel.SharedViewState.isRunning) {
        val processingAlert = stringResource(R.string.processing_alert)
        LaunchedEffect(Unit) {
            scaffoldState.snackbarHostState.showSnackbar(message = processingAlert)
            when (ButkusViewModel.SharedViewState.currentScreen) {
                ButkusScreen.Encode -> encodeViewModel.run()
                ButkusScreen.Decode -> decodeViewModel.run()
                else -> {}
            }
            ButkusViewModel.SharedViewState.isRunning = false
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ButkusAppBar(
                onOpenDrawer = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                onReset = {
                    when (ButkusViewModel.SharedViewState.currentScreen) {
                        ButkusScreen.Encode -> encodeViewModel.reset()
                        ButkusScreen.Decode -> decodeViewModel.reset()
                        ButkusScreen.Settings -> {}
                    }
                },
                onShare = { shareMessage(context, encodeViewModel.encodedMessage) }
            )
        },
        drawerGesturesEnabled = ButkusViewModel.SharedViewState.uiEnabled,
        drawerContent = {
            Drawer(
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
                EncodeScreen(encodeViewModel = encodeViewModel)
            }

            composable(route = ButkusScreen.Decode.name) {
                DecodeScreen(decodeViewModel = decodeViewModel)
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

@Preview
@Composable
fun ButkusAppPreview() {
    ButkusAppTheme {
        ButkusApp()
    }
}

@Preview
@Composable
fun ButkusAppDarkPreview() {
    ButkusAppTheme(darkTheme = true) {
        ButkusApp()
    }
}