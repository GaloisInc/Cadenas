package com.hashapps.cadenas.ui.settings.models.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar
import com.hashapps.cadenas.ui.settings.models.ModelUiState
import com.hashapps.cadenas.ui.settings.models.SAMPLE_MODEL_NAME
import com.hashapps.cadenas.ui.settings.models.SAMPLE_MODEL_URL
import com.hashapps.cadenas.ui.settings.models.SPANISH_SAMPLE_MODEL_NAME
import com.hashapps.cadenas.ui.settings.models.SPANISH_SAMPLE_MODEL_URL
import com.hashapps.cadenas.ui.settings.models.isNameValid
import com.hashapps.cadenas.ui.settings.models.isUrlValid
import com.hashapps.cadenas.workers.ModelDownloadWorker
import kotlinx.coroutines.launch

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
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    var downloading by rememberSaveable { mutableStateOf(false) }
    val workerState by viewModel.modelDownloaderState.collectAsState()
    workerState?.also {
        LaunchedEffect(it) {
            val finished = it.state == WorkInfo.State.SUCCEEDED || it.state == WorkInfo.State.FAILED
            if (downloading && finished) {
                downloading = false

                if (it.state == WorkInfo.State.SUCCEEDED) {
                    viewModel.saveModel(it.outputData.getString(ModelDownloadWorker.KEY_MODEL_HASH)!!)
                    viewModel.updateUiState(ModelUiState())
                }

                this.launch {
                    snackbarHostState.showSnackbar(
                        it.outputData.getString(
                            ModelDownloadWorker.KEY_RESULT_MSG
                        )!!
                    )
                }
            }
        }
    }

    val modelNames by viewModel.modelNames.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.add_model),
                canNavigateBack = !downloading,
                onNavigateBack = onNavigateBack,
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
                modelNames = modelNames,
                onValueChange = viewModel::updateUiState,
                enabled = !downloading,
            )

            Button(
                onClick = {
                    downloading = true
                    viewModel.downloadModel()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !downloading && viewModel.modelUiState.actionEnabled,
            ) {
                Text(
                    text = stringResource(R.string.download),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                onClick = {
                    viewModel.updateUiState(
                        viewModel.modelUiState.copy(
                            name = SAMPLE_MODEL_NAME,
                            url = SAMPLE_MODEL_URL
                        )
                    )
                    downloading = true
                    viewModel.downloadSampleModel()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !downloading && SAMPLE_MODEL_NAME !in modelNames,
            ) {
                Text(
                    text = stringResource(R.string.download_sample_model),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Button(
                onClick = {
                    viewModel.updateUiState(
                        viewModel.modelUiState.copy(
                            name = SPANISH_SAMPLE_MODEL_NAME,
                            url = SPANISH_SAMPLE_MODEL_URL
                        )
                    )
                    downloading = true
                    viewModel.downloadSpanishSampleModel()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !downloading && SPANISH_SAMPLE_MODEL_NAME !in modelNames,
            ) {
                Text(
                    text = stringResource(R.string.download_sample_spanish_model),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            val fileName = workerState?.progress?.getString(ModelDownloadWorker.PROGRESS)
            if (downloading && fileName != null) {
                LinearProgressIndicator(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                Text(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    text = LocalContext.current.getString(
                        R.string.download_progress,
                        fileName
                    )
                )
            }
        }
    }
}

@Composable
private fun ModelInputForm(
    modelUiState: ModelUiState,
    modifier: Modifier = Modifier,
    modelNames: List<String>,
    onValueChange: (ModelUiState) -> Unit = {},
    enabled: Boolean = true,
) {
    val focusManager = LocalFocusManager.current

    var displaySupportText = modelUiState.name == "" || modelUiState.isNameValid(modelNames)
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
            onValueChange = { onValueChange(modelUiState.copy(url = it)) },
            singleLine = true,
            label = { Text(stringResource(R.string.url_label)) },
            supportingText = {
                if (displaySupportText) {
                    Text(stringResource(R.string.url_support))
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
