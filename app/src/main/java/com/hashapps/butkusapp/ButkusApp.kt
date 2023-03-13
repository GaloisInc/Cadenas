package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.ui.models.SettingsViewModel
import com.hashapps.butkusapp.ui.screens.DecodeScreen
import com.hashapps.butkusapp.ui.screens.EncodeScreen
import com.hashapps.butkusapp.ui.screens.SettingsScreen

enum class ButkusScreen(@StringRes val title: Int, val icon: ImageVector) {
    Encode(title = R.string.encode, icon = Icons.Filled.Lock),
    Decode(title = R.string.decode, icon = Icons.Filled.LockOpen),
    Settings(title = R.string.settings, icon = Icons.Filled.Settings),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButkusApp(
    modifier: Modifier = Modifier,
    vm: ButkusViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ButkusScreen.valueOf(
        backStackEntry?.destination?.route ?: ButkusScreen.Encode.name
    )

    Scaffold(
        topBar = {
            val encodeUiState by vm.encode.uiState.collectAsStateWithLifecycle()
            val decodeUiState by vm.decode.uiState.collectAsStateWithLifecycle()

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
                                enabled = encodeUiState.encodedMessage != null,
                                onClick = {
                                    shareMessage(
                                        context,
                                        encodeUiState.encodedMessage!! // Safe by enabled condition
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = stringResource(R.string.share_button),
                                )
                            }
                        }
                        else -> {}
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
        val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
        NavHost(
            navController = navController,
            startDestination = ButkusScreen.Encode.name,
            modifier = modifier.padding(innerPadding),
        ) {
            composable(route = ButkusScreen.Encode.name) {
                EncodeScreen(
                    vm = vm.encode,
                    butkusInitialized = vm.butkusInitialized,
                )
            }

            composable(route = ButkusScreen.Decode.name) {
                DecodeScreen(
                    vm = vm.decode,
                    butkusInitialized = vm.butkusInitialized,
                )
            }

            composable(route = ButkusScreen.Settings.name) {
                SettingsScreen(vm = settingsViewModel)
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