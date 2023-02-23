package com.hashapps.butkusapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.DecodeUiState
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

/** The message decoding screen. Consists of:
 * - Text field for the message to decode. Note that before decoding, trailing
 *   tags of the form '#<tag here>' and a single space preceding them will be
 *   stripped off
 * - (If message decoded) The decoded message
 * - Action button (Decode) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecodeScreen(
    modifier: Modifier = Modifier,
    decodeUiState: DecodeUiState,
    onCoverTextChange: (String) -> Unit,
    butkusInitialized: Boolean,
    onDecode: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val focusManager = LocalFocusManager.current

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            CompositionLocalProvider(
                LocalTextInputService provides null
            ) {
                OutlinedTextField(
                    modifier = modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    enabled = !decodeUiState.inProgress,
                    value = decodeUiState.message,
                    onValueChange = onCoverTextChange,
                    singleLine = false,
                    label = { Text(stringResource(R.string.encoded_message_label)) },
                    placeholder = { Text(stringResource(R.string.encoded_message_placeholder)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                )
            }
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            enabled = butkusInitialized && !decodeUiState.inProgress && decodeUiState.message.isNotEmpty(),
            onClick = onDecode,
        ) {
            Text(
                text = stringResource(R.string.decode),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (decodeUiState.inProgress) {
            LinearProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally))
        }

        if (decodeUiState.decodedMessage != null) {
            ElevatedCard(modifier = modifier.fillMaxWidth()) {
                SelectionContainer {
                    Text(
                        modifier = modifier.padding(8.dp),
                        text = decodeUiState.decodedMessage,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DecodeScreenPreviewDefault() {
    ButkusAppTheme {
        DecodeScreen(
            decodeUiState = DecodeUiState(),
            onCoverTextChange = {},
            butkusInitialized = true,
            onDecode = { },
        )
    }
}