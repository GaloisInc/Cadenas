package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ButkusScreen.valueOf(
        backStackEntry?.destination?.route ?: ButkusScreen.Encode.name
    )

    val butkusInitialized by viewModel.butkusInitialized.collectAsState()

    val encodeUiState by viewModel.encodeUiState.collectAsState()
    val decodeUiState by viewModel.decodeUiState.collectAsState()
    val settingsUiState by viewModel.settingsUiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(currentScreen.title)) },
                modifier = modifier,
                navigationIcon = {
                    val settings = stringResource(ButkusScreen.Settings.title)

                    IconButton(
                        enabled = !(encodeUiState.inProgress || decodeUiState.inProgress),
                        onClick = { navController.navigate(settings) }
                    ) {
                        Icon(
                            imageVector = ButkusScreen.Settings.icon,
                            contentDescription = settings,
                        )
                    }
                },
                actions = {
                    when (currentScreen) {
                        ButkusScreen.Encode -> {
                            val context = LocalContext.current

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
                                enabled = encodeUiState.encodedMessage != null,
                                onClick = {
                                    shareMessage(
                                        context,
                                        encodeUiState.encodedMessage!! // Safe by enabled condition
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
        bottomBar = {
            NavigationBar {
                val actionScreens = listOf(ButkusScreen.Encode, ButkusScreen.Decode)

                actionScreens.forEach {
                    val screen = stringResource(it.title)

                    NavigationBarItem(
                        selected = currentScreen == it,
                        onClick = { navController.navigate(screen) },
                        icon = {
                            Icon(imageVector = it.icon, contentDescription = null)
                        },
                        label = { Text(screen) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val scope = rememberCoroutineScope()

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
                    onGenKey = { },
                    maxSeedLen = 128,
                    onSeedChange = { viewModel.updateSeedText(it) },
                    maxUrlLen = 128,
                    onUrlChange = {  },
                )
            }
        }
    }
}

private fun shareMessage(context: Context, message: String) {
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