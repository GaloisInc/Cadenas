package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.data.ButkusScreen
import com.hashapps.butkusapp.ui.models.ButkusViewModel
import com.hashapps.butkusapp.ui.models.DecodeViewModel
import com.hashapps.butkusapp.ui.models.EncodeViewModel
import com.hashapps.butkusapp.ui.screens.DecodeScreen
import com.hashapps.butkusapp.ui.screens.EncodeScreen
import com.hashapps.butkusapp.ui.screens.SettingsScreen
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme
import kotlinx.coroutines.launch

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier,
    screen: ButkusScreen,
    selected: Boolean,
    onDestinationClicked: (String) -> Unit,
) {
    val backgroundColor = if (selected) MaterialTheme.colors.secondary else Color.Transparent
    val screenName = stringResource(screen.title)
    val icon = when (screen) {
        ButkusScreen.Encode -> Icons.Filled.Lock
        ButkusScreen.Decode -> Icons.Filled.LockOpen
        ButkusScreen.Settings -> Icons.Filled.Settings
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onDestinationClicked(screenName) }
            .background(color = backgroundColor)
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = screenName)
        Spacer(Modifier.width(8.dp))
        Text(
            text = screenName,
            style = MaterialTheme.typography.h6,
        )
    }
}

@Composable
private fun Drawer(
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
                selected = currentScreen == it,
                onDestinationClicked = onDestinationClicked
            )
        }
    }
}

@Composable
private fun ButkusAppBar(
    currentScreen: ButkusScreen,
    onOpenDrawer: () -> Unit,
    onReset: () -> Unit,
    canShare: Boolean,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        backgroundColor = MaterialTheme.colors.primary,
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onOpenDrawer,
                enabled = ButkusViewModel.SharedViewState.uiEnabled,
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.switch_button),
                )
            }
        },
        actions = {
            IconButton(
                onClick = onReset,
                enabled = ButkusViewModel.SharedViewState.uiEnabled,
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.reset)
                )
            }

            IconButton(
                onClick = onShare,
                enabled = ButkusViewModel.SharedViewState.uiEnabled && canShare,
            ) {
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
    val currentScreen = ButkusScreen.valueOf(
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
            when (currentScreen) {
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
                currentScreen = currentScreen,
                onOpenDrawer = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                onReset = {
                    when (currentScreen) {
                        ButkusScreen.Encode -> encodeViewModel.reset()
                        ButkusScreen.Decode -> decodeViewModel.reset()
                        ButkusScreen.Settings -> {}
                    }
                },
                canShare = currentScreen == ButkusScreen.Encode && ButkusViewModel.SharedViewState.hasShareable,
                onShare = { shareMessage(context, encodeViewModel.encodedMessage) }
            )
        },
        drawerGesturesEnabled = ButkusViewModel.SharedViewState.uiEnabled,
        drawerContent = {
            Drawer(
                currentScreen = currentScreen,
                onDestinationClicked = { route ->
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }

                    navController.navigate(route)
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