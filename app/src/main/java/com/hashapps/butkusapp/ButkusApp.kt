package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
 * - Settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButkusApp(
    modifier: Modifier = Modifier,
    viewModel: ButkusAppViewModel,
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ButkusScreen.valueOf(
        backStackEntry?.destination?.route ?: ButkusScreen.Encode.name
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val butkusInitialized by viewModel.butkusInitialized.collectAsState()
    val encodeUiState by viewModel.encodeUiState.collectAsState()
    val decodeUiState by viewModel.decodeUiState.collectAsState()
    val settingsUiState by viewModel.settingsUiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = when (currentScreen) {
            ButkusScreen.Encode -> !encodeUiState.inProgress
            ButkusScreen.Decode -> !decodeUiState.inProgress
            ButkusScreen.Settings -> true
        },
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier.height(8.dp))
                ButkusScreen.values().forEach {
                    val screenName = stringResource(it.title)
                    NavigationDrawerItem(
                        modifier = modifier.padding(start = 8.dp, end = 8.dp),
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
                        when (currentScreen) {
                            ButkusScreen.Encode -> {
                                IconButton(
                                    enabled = !encodeUiState.inProgress,
                                    onClick = { viewModel.resetEncodeScreen() },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.RestartAlt,
                                        contentDescription = stringResource(R.string.reset),
                                    )
                                }

                                IconButton(
                                    enabled = !encodeUiState.inProgress && encodeUiState.encodedMessage != null,
                                    onClick = {
                                        shareMessage(
                                            context,
                                            encodeUiState.encodedMessage
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Send,
                                        contentDescription = stringResource(R.string.share_button),
                                    )
                                }
                            }
                            ButkusScreen.Decode -> {
                                IconButton(
                                    enabled = !decodeUiState.inProgress,
                                    onClick = { viewModel.resetDecodeScreen() },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.RestartAlt,
                                        contentDescription = stringResource(R.string.reset),
                                    )
                                }
                            }
                            ButkusScreen.Settings -> {
                                IconButton(
                                    onClick = { },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Download,
                                        contentDescription = stringResource(R.string.import_label),
                                    )
                                }

                                IconButton(
                                    onClick = { },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Upload,
                                        contentDescription = stringResource(R.string.export_label),
                                    )
                                }
                            }
                        }
                    },
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
                        onAddTag = { viewModel.addTag(encodeUiState.tagToAdd) },
                        onTagRemove = { viewModel.removeTag(it) },
                        butkusInitialized = butkusInitialized,
                        onEncode = { scope.launch { viewModel.encodeMessage() } },
                    )
                }

                composable(route = ButkusScreen.Decode.name) {
                    DecodeScreen(
                        decodeUiState = decodeUiState,
                        onCoverTextChange = { viewModel.updateEncodedMessage(it) },
                        butkusInitialized = butkusInitialized,
                        onDecode = { scope.launch { viewModel.decodeMessage() } },
                    )
                }

                composable(route = ButkusScreen.Settings.name) {
                    SettingsScreen(
                        settingsUiState = settingsUiState,
                        onKeyChange = { },
                        onGenKey = { },
                        onSeedChange = { viewModel.updateSeedText(it) },
                        onRestoreDefaults = { },
                    )
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