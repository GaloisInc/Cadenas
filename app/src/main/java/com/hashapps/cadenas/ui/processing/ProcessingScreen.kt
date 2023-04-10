package com.hashapps.cadenas.ui.processing

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.navigation.NavigationDestination
import com.hashapps.cadenas.ui.settings.SettingsDestination

object ProcessingDestination : NavigationDestination {
    override val route = "processing"
    override val titleRes = R.string.unused
}

object EncodeDestination : NavigationDestination {
    override val route = "encode"
    override val titleRes = R.string.encode
    val icon = Icons.Filled.Lock
}

object DecodeDestination : NavigationDestination {
    override val route = "decode"
    override val titleRes = R.string.decode
    val icon = Icons.Filled.LockOpen
}

@Immutable
data class ProcessingViewState(
    @StringRes val title: Int? = null,
    val onShare: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingScreen(
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProcessingViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: EncodeDestination.route

    var processingViewState by remember { mutableStateOf(ProcessingViewState()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    processingViewState.title?.let {
                        Text(text = stringResource(it))
                    }
                },
                modifier = modifier,
                navigationIcon = {
                    IconButton(
                        enabled = !(viewModel.encodeUiState.inProgress || viewModel.decodeUiState.inProgress),
                        onClick = navigateToSettings,
                    ) {
                        Icon(
                            imageVector = SettingsDestination.icon,
                            contentDescription = stringResource(SettingsDestination.titleRes),
                        )
                    }
                },
                actions = {
                    if (processingViewState.title == EncodeDestination.titleRes) {
                        IconButton(
                            enabled = viewModel.encodeUiState.result != null,
                            onClick = processingViewState.onShare,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = stringResource(R.string.share_button)
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                val processingScreens = listOf(EncodeDestination, DecodeDestination).zip(
                    listOf(
                        EncodeDestination.icon, DecodeDestination.icon
                    )
                )

                processingScreens.forEach { (nd, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == nd.route,
                        onClick = { navController.navigate(nd.route) },
                        icon = { Icon(imageVector = icon, contentDescription = null) },
                        label = { Text(stringResource(nd.titleRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val cadenasInitialized by viewModel.cadenasInitialized.collectAsState()

        val tag by viewModel.tag.collectAsState()
        val formattedTag = if (!tag.isNullOrBlank()) {
            " #$tag"
        } else {
            ""
        }

        NavHost(
            navController = navController,
            startDestination = EncodeDestination.route,
            modifier = modifier.padding(innerPadding),
        ) {
            composable(route = EncodeDestination.route) {
                val context = LocalContext.current
                processingViewState = ProcessingViewState(
                    title = EncodeDestination.titleRes,
                    onShare = { shareMessage(context, viewModel.encodeUiState.result) }
                )

                ProcessingBody(
                    cadenasInitialized = cadenasInitialized,
                    processingUiState = viewModel.encodeUiState,
                    onValueChange = viewModel::updateEncodeUiState,
                    toProcessLabel = stringResource(R.string.plaintext_message_label),
                    toProcessSupport = stringResource(R.string.plaintext_message_support),
                    action = { viewModel.encodeMessage(formattedTag) },
                    actionLabel = stringResource(R.string.encode),
                )
            }

            composable(route = DecodeDestination.route) {
                processingViewState = ProcessingViewState(
                    title = DecodeDestination.titleRes,
                )

                ProcessingBody(
                    cadenasInitialized = cadenasInitialized,
                    processingUiState = viewModel.decodeUiState,
                    onValueChange = viewModel::updateDecodeUiState,
                    toProcessLabel = stringResource(R.string.encoded_message_label),
                    toProcessSupport = stringResource(R.string.encoded_message_support),
                    action = { viewModel.decodeMessage(formattedTag) },
                    actionLabel = stringResource(R.string.decode),
                    preventKeyboardInput = true,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingBody(
    cadenasInitialized: Boolean,
    processingUiState: ProcessingUiState,
    onValueChange: (ProcessingUiState) -> Unit,
    toProcessLabel: String,
    toProcessSupport: String,
    action: () -> Unit,
    actionLabel: String,
    modifier: Modifier = Modifier,
    preventKeyboardInput: Boolean = false,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val focusManager = LocalFocusManager.current

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            val processInputField = @Composable {
                OutlinedTextField(
                    modifier = modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    enabled = !processingUiState.inProgress,
                    value = processingUiState.toProcess,
                    onValueChange = { onValueChange(processingUiState.copy(toProcess = it)) },
                    singleLine = false,
                    label = { Text(toProcessLabel) },
                    trailingIcon = {
                        IconButton(
                            enabled = processingUiState.toProcess.isNotEmpty(),
                            onClick = { onValueChange(processingUiState.copy(toProcess = "")) },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.clear),
                            )
                        }
                    },
                    supportingText = { Text(toProcessSupport) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) },
                    ),
                )
            }
            if (preventKeyboardInput) {
                CompositionLocalProvider(
                    LocalTextInputService provides null
                ) {
                    processInputField()
                }
            } else {
                processInputField()
            }
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            enabled = cadenasInitialized && processingUiState.actionEnabled,
            onClick = action,
        ) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            if (processingUiState.inProgress) {
                LinearProgressIndicator(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
            }

            if (processingUiState.result != null) {
                Row(
                    modifier = modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        LocalContext.current.resources.getQuantityString(
                            R.plurals.result_length,
                            processingUiState.result.length,
                            processingUiState.result.length,
                        )
                    )

                    IconButton(
                        onClick = { onValueChange(processingUiState.copy(result = null)) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.clear),
                        )
                    }
                }

                Divider(thickness = 1.dp)

                SelectionContainer {
                    Text(
                        modifier = modifier.padding(8.dp),
                        text = processingUiState.result,
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
                context.getString(R.string.cadenas_message)
            )
        )
    }
}