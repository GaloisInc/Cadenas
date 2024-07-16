package com.hashapps.cadenas.ui.processing

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.hashapps.cadenas.ui.cache.DisplayMessageCache


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
            toProcessSupport = when (viewModel.processingUiState.processingMode) {
                ProcessingMode.Encode -> stringResource(R.string.plaintext_message_support)
                ProcessingMode.Decode -> stringResource(R.string.encoded_message_support)
            },
            action = { viewModel.processMessage() },
            clearMessageCache = { viewModel.clearMessageCache() },
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
    clearMessageCache: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (processingUiState.showEditWarning && !processingUiState.result.isNullOrBlank()) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.attention)) },
            text = { Text(stringResource(R.string.edit_warning)) },
            modifier = Modifier.padding(16.dp),
            confirmButton = {
                TextButton(
                    onClick = { onValueChange(processingUiState.copy(showEditWarning = false)) }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current

        SingleChoiceSegmentedButtonRow(
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
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ProcessingMode.entries.size
                    ),
                ) {
                    Text(
                        when (mode) {
                            ProcessingMode.Encode -> stringResource(R.string.encode)
                            ProcessingMode.Decode -> stringResource(R.string.decode)
                        }
                    )
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
                text = stringResource(R.string.execute),
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
                SelectionContainer {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = processingUiState.result,
                    )
                }
                if (processingUiState.processingMode == ProcessingMode.Encode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        IconButton(
                            enabled = true,
                            onClick = { saveMessage(context, processingUiState.result) },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentPaste,
                                contentDescription = stringResource(R.string.save_to_clipboard_button)
                            )
                        }
                        IconButton(
                            enabled = true,
                            onClick = { sendTextMessage(context, processingUiState.result) },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Sms,
                                contentDescription = stringResource(R.string.sms_button)
                            )
                        }
                        IconButton(
                            enabled = true,
                            onClick = { onValueChange(processingUiState.copy(result = null)) },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.clear),
                            )
                        }
                    }
                }
            }
        }
        //display the message cache for this channel
        DisplayMessageCache(
            processingUiState.cachedMessages,
            processingUiState.channelCacheTimeInMS,
            clearMessageCache)
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

private fun sendTextMessage(context: Context, message: String?) {
    if (message != null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("smsto:")  // Only SMS apps respond to this.
            type = "text/plain"
            putExtra("sms_body", message)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            shareMessage(context, message)
        }
    }
}

private fun saveMessage(context: Context, message: String?) {
    if (message != null) {
        val clipboardManager = context.getSystemService( Context.CLIPBOARD_SERVICE) as ClipboardManager
        // When setting the clipboard text.
        clipboardManager.setPrimaryClip(ClipData.newPlainText   ("", message))
        // Only show a toast for Android 12 and lower. (Above that there is an indication.)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            Toast.makeText(context, R.string.ok, Toast.LENGTH_SHORT).show()
    }
}