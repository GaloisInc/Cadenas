package com.hashapps.cadenas.ui.settings.models.disk

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.hashapps.cadenas.ui.settings.models.isNameValid
import com.hashapps.cadenas.workers.ModelInstallWorker
import kotlinx.coroutines.launch

/**
 * Cadenas model-add screen (from disk).
 *
 * This screen provides an alternative method of installing models: From the
 * device's disk. This requires unzipping a selected ZIP to the application's
 * resources. With permission, Cadenas may remove the source ZIP after
 * installation.
 */
@Composable
fun ModelAddDiskScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelAddDiskViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    var extracting by rememberSaveable { mutableStateOf(false) }
    val workerState by viewModel.modelInstallerState.collectAsState()
    workerState?.also {
        LaunchedEffect(it) {
            val finished = it.state == WorkInfo.State.SUCCEEDED || it.state == WorkInfo.State.FAILED
            if (extracting && finished) {
                extracting = false

                if (it.state == WorkInfo.State.SUCCEEDED) {
                    viewModel.saveModel(it.outputData.getString(ModelInstallWorker.KEY_MODEL_HASH)!!)
                    viewModel.updateUiState(ModelUiState())
                }

                this.launch {
                    snackbarHostState.showSnackbar(
                        it.outputData.getString(
                            ModelInstallWorker.KEY_RESULT_MSG
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
                canNavigateBack = !extracting,
                onNavigateBack = onNavigateBack,
            )
        },
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
                enabled = !extracting,
            )

            Button(
                onClick = {
                    extracting = true
                    viewModel.installModel()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !extracting && viewModel.modelUiState.actionEnabled,
            ) {
                Text(
                    text = stringResource(R.string.install),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            val fileName = workerState?.progress?.getString(ModelInstallWorker.PROGRESS)
            if (extracting && fileName != null) {
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
    val displaySupportText = modelUiState.name == "" || modelUiState.isNameValid(modelNames)
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
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                onValueChange(modelUiState.copy(file = uri.toString()))
            }

        Button(
            onClick = {
                launcher.launch("application/zip")
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = if (modelUiState.file.isEmpty()) {
                    stringResource(R.string.choose_zip)
                } else {
                    stringResource(R.string.zip_chosen)
                },
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}