package com.hashapps.butkusapp

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.data.DecodeUiState
import com.hashapps.butkusapp.data.EncodeUiState
import com.hashapps.butkusapp.ui.ButkusViewModel
import com.hashapps.butkusapp.ui.DecodeScreen
import com.hashapps.butkusapp.ui.EncodeScreen
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

enum class ButkusScreen(@StringRes val title: Int) {
    Encode(title = R.string.encode),
    Decode(title = R.string.decode),
}

@Composable
fun ButkusAppBar(
    currentScreen: ButkusScreen,
    encodeUiState: EncodeUiState,
    decodeUiState: DecodeUiState,
    onSwitchScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val canSwitchScreen = when (currentScreen) {
        ButkusScreen.Encode -> encodeUiState.canSwitchScreen
        ButkusScreen.Decode -> decodeUiState.canSwitchScreen
    }

    val share = {
        when (currentScreen) {
            ButkusScreen.Encode -> shareMessage(context, encodeUiState.encodedMessage)
            ButkusScreen.Decode -> shareMessage(context, decodeUiState.decodedMessage)
        }
    }

    val canShare = when (currentScreen) {
        ButkusScreen.Encode -> encodeUiState.canShare
        ButkusScreen.Decode -> decodeUiState.canShare
    }

    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        backgroundColor = MaterialTheme.colors.primary,
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onSwitchScreen,
                enabled = canSwitchScreen,
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.switch_button),
                )
            }
        },
        actions = {
            IconButton(
                onClick = share,
                enabled = canShare,
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
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = ButkusScreen.valueOf(
        backStackEntry?.destination?.route ?: ButkusScreen.Encode.name
    )

    val encodeUiState by viewModel.encodeUiState.collectAsState()
    val decodeUiState by viewModel.decodeUiState.collectAsState()

    Scaffold(
        topBar = {
            ButkusAppBar(
                currentScreen = currentScreen,
                encodeUiState = encodeUiState,
                decodeUiState = decodeUiState,
                onSwitchScreen = {
                     when (currentScreen) {
                         ButkusScreen.Encode -> navController.navigate(ButkusScreen.Decode.name)
                         ButkusScreen.Decode -> navController.navigate(ButkusScreen.Encode.name)
                     }
                },
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
                    onEncode = {
                        viewModel.encodeMessage()
                    },
                    onReset = { viewModel.resetEncodeState() },
                )
            }

            composable(route = ButkusScreen.Decode.name) {
                DecodeScreen(
                    decodeUiState = decodeUiState,
                    onMessageChanged = {
                        viewModel.updateEncodedMessage(it)
                    },
                    onDecode = { viewModel.decodeMessage() },
                    onReset = { viewModel.resetDecodeState() },
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