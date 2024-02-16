package com.hashapps.cadenas.ui.processing

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.R

/**
 * Cadenas message-processing screen.
 *
 * The main point of interaction with the Cadenas application, this Composable
 * defines the view for message encoding and decoding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProcessingViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = viewModel.processingUiState.channelName)
                },
                modifier = modifier,
                navigationIcon = {
                    IconButton(
                        enabled = !(viewModel.processingUiState.inProgress),
                        onClick = onNavigateBack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    if (viewModel.processingUiState.processingMode == ProcessingMode.Encode) {
                        IconButton(
                            enabled = viewModel.processingUiState.result != null,
                            onClick = { shareMessage(context, viewModel.processingUiState.result) },
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
    ) { innerPadding ->
        ProcessingBody(
            processingUiState = viewModel.processingUiState,
            onValueChange = viewModel::updateProcessingUiState,
            enterEncodeMode = viewModel::encodingMode,
            enterDecodeMode = viewModel::decodingMode,
            toProcessLabel = when (viewModel.processingUiState.processingMode) {
                ProcessingMode.Encode -> stringResource(R.string.plaintext_message_label)
                ProcessingMode.Decode -> stringResource(R.string.encoded_message_label)
            },
            toProcessSupport = when(viewModel.processingUiState.processingMode) {
                ProcessingMode.Encode -> stringResource(R.string.plaintext_message_support)
                ProcessingMode.Decode -> stringResource(R.string.encoded_message_support)
            },
            action = { viewModel.processMessage() },
            modifier = modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProcessingBody(
    processingUiState: ProcessingUiState,
    onValueChange: (ProcessingUiState) -> Unit,
    enterEncodeMode: () -> Unit,
    enterDecodeMode: () -> Unit,
    toProcessLabel: String,
    toProcessSupport: String,
    action: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val focusManager = LocalFocusManager.current

        SingleChoiceSegmentedButtonRow (
            modifier = modifier.fillMaxWidth(),
        ) {
            ProcessingMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    enabled = !processingUiState.inProgress,
                    selected = mode == processingUiState.processingMode,
                    onClick = when (mode) {
                        ProcessingMode.Encode -> enterEncodeMode
                        ProcessingMode.Decode -> enterDecodeMode
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = ProcessingMode.entries.size),
                ) {
                    Text(mode.toString())
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                modifier = Modifier
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
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.clearFocus() },
                ),
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = processingUiState.toProcess.isNotEmpty() && !processingUiState.inProgress,
            onClick = action,
        ) {
            Text(
                text = stringResource(R.string.go),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (processingUiState.inProgress) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
            }

            if (processingUiState.result != null) {
                Row(
                    modifier = Modifier
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

                HorizontalDivider(thickness = 1.dp)

                SelectionContainer {
                    Text(
                        modifier = Modifier.padding(8.dp),
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