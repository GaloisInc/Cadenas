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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.ui.decode.DecodeDestination
import com.hashapps.butkusapp.ui.decode.DecodeScreen
import com.hashapps.butkusapp.ui.encode.EncodeDestination
import com.hashapps.butkusapp.ui.encode.EncodeScreen
import com.hashapps.butkusapp.ui.settings.SettingsDestination
import com.hashapps.butkusapp.ui.settings.SettingsScreen

@Immutable
data class ButkusViewState(
    @StringRes val title: Int? = null,
    val hasShareable: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButkusApp(
    modifier: Modifier = Modifier,
    vm: ButkusViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: EncodeDestination.route

    var butkusViewState by remember { mutableStateOf(ButkusViewState()) }

    Scaffold(
        topBar = {
            val encodeUiState by vm.encode.uiState.collectAsStateWithLifecycle()
            val decodeUiState by vm.decode.uiState.collectAsStateWithLifecycle()

            CenterAlignedTopAppBar(
                title = {
                    butkusViewState.title?.let {
                        Text(text = stringResource(it))
                    }
                },
                modifier = modifier,
                navigationIcon = {
                    val settings = stringResource(SettingsDestination.titleRes)

                    IconButton(
                        enabled = !(encodeUiState.inProgress || decodeUiState.inProgress),
                        onClick = { navController.navigate(SettingsDestination.route) }
                    ) {
                        Icon(
                            imageVector = SettingsDestination.icon,
                            contentDescription = settings,
                        )
                    }
                },
                actions = {
                    if (butkusViewState.hasShareable) {
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
                },
            )
        },
        bottomBar = {
            NavigationBar {
                val actionScreens = listOf(EncodeDestination, DecodeDestination).zip(
                    listOf(
                        EncodeDestination.icon,
                        DecodeDestination.icon
                    )
                )

                actionScreens.forEach { (nd, icon) ->
                    val title = stringResource(nd.titleRes)
                    NavigationBarItem(
                        selected = currentRoute == nd.route,
                        onClick = { navController.navigate(nd.route) },
                        icon = { Icon(imageVector = icon, contentDescription = null) },
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EncodeDestination.route,
            modifier = modifier.padding(innerPadding),
        ) {
            composable(route = EncodeDestination.route) {
                butkusViewState = ButkusViewState(
                    title = EncodeDestination.titleRes,
                    hasShareable = true,
                )

                EncodeScreen(
                    vm = vm.encode,
                    butkusInitialized = vm.butkusInitialized,
                )
            }

            composable(route = DecodeDestination.route) {
                butkusViewState = ButkusViewState(
                    title = DecodeDestination.titleRes,
                )

                DecodeScreen(
                    vm = vm.decode,
                    butkusInitialized = vm.butkusInitialized,
                )
            }

            composable(route = SettingsDestination.route) {
                butkusViewState = ButkusViewState(
                    title = SettingsDestination.titleRes,
                )

                SettingsScreen()
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