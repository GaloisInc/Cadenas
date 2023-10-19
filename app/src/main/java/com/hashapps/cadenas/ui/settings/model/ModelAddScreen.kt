package com.hashapps.cadenas.ui.settings.model

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar
import com.hashapps.cadenas.workers.ModelDownloadWorker
import kotlinx.coroutines.launch

private const val MAX_LEN = 128

/**
 * Cadenas model-add screen.
 *
 * To encode and decode messages, Cadenas needs access to one or more GPT-2
 * language models. This screen provides the interface to fetch models from the
 * Internet, specifically at HTTPS endpoints. Model downloading is performed as
 * background work that _will not_ be cancelled, even if the application dies.
 *
 * Future versions may support other sources, such as IPFS.
 */
@Composable
fun ModelAddScreen(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    var showProgressIndicator by rememberSaveable { mutableStateOf(false) }

    /*
     * This is a fun block. First, we need some state to track whether a
     * model download has been started - we can't rely on the worker state,
     * since this will still be `SUCCEEDED` if the application is restarted
     * after a successful model download. The problem here is that we wish
     * to show a Snackbar message upon download success and failure, and
     * relying on the state alone would cause such a message to be shown
     * regardless.
     *
     * Additionally, there is some special-case logic for when this screen is
     * interacted with during the first-time setup sequence - rather than
     * simply resetting the UI in that case, we must advance to the next
     * screen.
     */
    var modelDownloadTriggered by rememberSaveable { mutableStateOf(false) }
    val workerState by viewModel.modelDownloaderState.collectAsState()
    workerState?.also {
        LaunchedEffect(it.state) {
            when (it.state) {
                WorkInfo.State.RUNNING -> showProgressIndicator = true
                WorkInfo.State.SUCCEEDED, WorkInfo.State.FAILED -> {
                    if (modelDownloadTriggered) {
                        this.launch {
                            snackbarHostState.showSnackbar(
                                it.outputData.getString(
                                    ModelDownloadWorker.KEY_RESULT_MSG
                                )!!
                            )
                        }

                        if (it.state == WorkInfo.State.SUCCEEDED) {
                            viewModel.updateUiState(ModelUiState())
                        }
                    }

                    showProgressIndicator = false
                    modelDownloadTriggered = false
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.add_model),
                canNavigateUp = !showProgressIndicator,
                navigateUp = onNavigateNext,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ModelInputForm(
                modelUiState = viewModel.modelUiState,
                onValueChange = viewModel::updateUiState,
                enabled = !showProgressIndicator,
            )

            Button(
                onClick = {
                    viewModel.downloadModel()
                    modelDownloadTriggered = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !showProgressIndicator && viewModel.modelUiState.actionEnabled,
            ) {
                Text(
                    text = stringResource(R.string.download),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (showProgressIndicator) {
                LinearProgressIndicator(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ModelInputForm(
    modelUiState: ModelUiState,
    modifier: Modifier = Modifier,
    onValueChange: (ModelUiState) -> Unit = {},
    enabled: Boolean = true,
) {
    val focusManager = LocalFocusManager.current

    var displaySupportText = modelUiState.name == "" || modelUiState.isNameValid()
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            enabled = enabled,
            value = modelUiState.name,
            onValueChange = { onValueChange(modelUiState.copy(name = it)) },
            singleLine = true,
            label = { Text(stringResource(R.string.model_name_label)) },
            supportingText = {
                if (displaySupportText) {
                    Text(stringResource(R.string.model_name_support))
                } else {
                    Text(stringResource(R.string.name_error))
                }
            },
            isError = !displaySupportText,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        displaySupportText = modelUiState.url == "" || modelUiState.isUrlValid()
        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            enabled = enabled,
            value = modelUiState.url,
            onValueChange = { onValueChange(modelUiState.copy(url = it.take(MAX_LEN))) },
            singleLine = true,
            label = { Text(stringResource(R.string.url_label)) },
            supportingText = {
                if (displaySupportText) {
                    Text(
                        LocalContext.current.getString(
                            R.string.url_support,
                            modelUiState.url.length,
                            MAX_LEN,
                        )
                    )
                } else {
                    Text(stringResource(R.string.url_error))
                }
            },
            isError = !displaySupportText,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
    }
}
