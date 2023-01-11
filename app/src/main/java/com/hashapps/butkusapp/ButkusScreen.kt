package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.ui.ButkusViewModel
import com.hashapps.butkusapp.ui.DecodeScreen
import com.hashapps.butkusapp.ui.EncodeScreen
import com.hashapps.butkusapp.ui.SettingsScreen
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

enum class ButkusScreen(@StringRes val title: Int) {
    Encode(title = R.string.encode),
    Decode(title = R.string.decode),
    Settings(title = R.string.settings),
}

@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    onDestinationClicked: (String) -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 24.dp)
    ) {
        ButkusScreen.values().forEach {
            val screen = stringResource(it.title)
            Text(
                text = screen,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.clickable {
                    onDestinationClicked(screen)
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ButkusAppBar(
    uiEnabled: Boolean,
    currentScreen: ButkusScreen,
    onOpenDrawer: () -> Unit,
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
                enabled = uiEnabled,
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.switch_button),
                )
            }
        },
        actions = {
            IconButton(
                onClick = onShare,
                enabled = uiEnabled && canShare,
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
    viewModel: ButkusViewModel = ButkusViewModel(),
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
    if (!viewModel.butkusInitialized) {
        LaunchedEffect(viewModel.butkusInitialized) {
            coroutineScope {
                launch { Butkus.initialize(context) }
            }
            viewModel.butkusInitialized = true
        }
    }

    // Get the actual UI state to control the app view
    val encodeUiState by viewModel.encodeUiState.collectAsState()
    val decodeUiState by viewModel.decodeUiState.collectAsState()
    val settingsUiState by viewModel.settingsUiState.collectAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ButkusAppBar(
                uiEnabled = viewModel.uiEnabled(),
                currentScreen = currentScreen,
                onOpenDrawer = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                canShare = (currentScreen == ButkusScreen.Encode) &&
                        (encodeUiState.encodedMessage != null),
                onShare = { shareMessage(context, encodeUiState.encodedMessage) }
            )
        },
        drawerGesturesEnabled = viewModel.uiEnabled(),
        drawerContent = {
            Drawer(
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
                if (viewModel.isEncoding) {
                    val processingAlert = stringResource(R.string.processing_alert)
                    LaunchedEffect(Unit) {
                        scaffoldState.snackbarHostState.showSnackbar(message = processingAlert)
                        viewModel.encodeMessage()
                        viewModel.isEncoding = false
                    }
                }

                EncodeScreen(
                    uiEnabled = viewModel.uiEnabled(),
                    encodeUiState = encodeUiState,
                    onMessageChanged = {
                        viewModel.updatePlaintextMessage(it)
                    },
                    onTagToAddChanged = {
                        viewModel.updateTagToAdd(it)
                    },
                    onAddTag = {
                        if (encodeUiState.tagToAdd != "") {
                            viewModel.addTag(encodeUiState.tagToAdd)
                            viewModel.updateTagToAdd("")
                        }
                    },
                    onDeleteTag = {
                        { viewModel.removeTag(it) }
                    },
                    canEncode = viewModel.canEncode(),
                    onEncode = { viewModel.isEncoding = true },
                    onReset = { viewModel.resetEncodeState() },
                )
            }

            composable(route = ButkusScreen.Decode.name) {
                DecodeScreen(
                    decodeUiState = decodeUiState,
                    onMessageChanged = {
                        viewModel.updateEncodedMessage(it)
                    },
                    canDecode = viewModel.canDecode(),
                    onDecode = { viewModel.isDecoding = true },
                    onReset = { viewModel.resetDecodeState() },
                )
            }

            composable(route = ButkusScreen.Settings.name) {
                SettingsScreen(
                    settingsUiState = settingsUiState,
                )
            }
        }
    }
}

fun shareMessage(context: Context, message: String?) {
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