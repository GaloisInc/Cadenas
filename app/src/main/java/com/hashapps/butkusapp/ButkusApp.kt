package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.ui.models.ButkusAppViewModel
import com.hashapps.butkusapp.ui.ButkusScreen
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
@OptIn(ExperimentalMaterial3Api::class)
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
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val butkusInitialized by viewModel.butkusInitialized.collectAsState()
    val encodeUiState by viewModel.encodeUiState.collectAsState()
    val decodeUiState by viewModel.decodeUiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = when (currentScreen) {
            ButkusScreen.Encode -> !encodeUiState.inProgress
            ButkusScreen.Decode -> !decodeUiState.inProgress
            ButkusScreen.Settings -> true
        },
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                ButkusScreen.values().forEach {
                    val screenName = stringResource(it.title)
                    NavigationDrawerItem(
                        label = { Text(screenName) },
                        selected = it == currentScreen,
                        onClick = {
                            navController.navigate(screenName)
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(imageVector = it.icon, contentDescription = screenName)
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(currentScreen.title)) },
                    modifier = modifier,
                    navigationIcon = {
                        IconButton(
                            enabled = when (currentScreen) {
                                ButkusScreen.Encode -> !encodeUiState.inProgress
                                ButkusScreen.Decode -> !decodeUiState.inProgress
                                ButkusScreen.Settings -> true
                            },
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.switch_button),
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            enabled = when (currentScreen) {
                                ButkusScreen.Encode -> !encodeUiState.inProgress
                                ButkusScreen.Decode -> !decodeUiState.inProgress
                                ButkusScreen.Settings -> true
                            },
                            onClick = {
                                when (currentScreen) {
                                    ButkusScreen.Encode -> viewModel.resetEncodeScreen()
                                    ButkusScreen.Decode -> viewModel.resetDecodeScreen()
                                    ButkusScreen.Settings -> {}
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RestartAlt,
                                contentDescription = stringResource(R.string.reset)
                            )
                        }

                        if (currentScreen == ButkusScreen.Encode) {
                            IconButton(
                                onClick = { shareMessage(context, encodeUiState.encodedMessage) },
                                enabled = !encodeUiState.inProgress && encodeUiState.encodedMessage != null,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = stringResource(R.string.share_button),
                                )
                            }
                        }
                    }
                )
            },
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
                        butkusInitialized = butkusInitialized,
                        onEncode = { scope.launch { viewModel.encodeMessage() } },
                    )
                }

                composable(route = ButkusScreen.Decode.name) {
                    DecodeScreen(
                        decodeUiState = decodeUiState,
                        onCoverTextChange = { viewModel.updateEncodedMessage(it) },
                        canDecode = butkusInitialized && !decodeUiState.inProgress && decodeUiState.message.isNotEmpty(),
                        onDecode = { scope.launch { viewModel.decodeMessage() } },
                    )
                }

                composable(route = ButkusScreen.Settings.name) {
                    SettingsScreen()
                }
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