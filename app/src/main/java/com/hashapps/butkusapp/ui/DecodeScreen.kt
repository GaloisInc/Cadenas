package com.hashapps.butkusapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.data.DecodeUiState
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

@Composable
fun DecodeScreen(
    uiEnabled: Boolean,
    decodeUiState: DecodeUiState,
    onMessageChanged: (String) -> Unit,
    canDecode: Boolean,
    onDecode: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CompositionLocalProvider(
            LocalTextInputService provides null
        ) {
            OutlinedTextField(
                modifier = modifier.fillMaxWidth(),
                enabled = uiEnabled,
                value = decodeUiState.message,
                onValueChange = onMessageChanged,
                singleLine = false,
                label = { Text(stringResource(R.string.encoded_message_label)) },
                placeholder = { Text(stringResource(R.string.encoded_message_placeholder)) },
            )
        }
        
        Spacer(modifier = modifier.weight(1f))

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = modifier.weight(0.5f),
                enabled = uiEnabled && canDecode,
                onClick = onDecode,
            ) {
                Text(
                    text = stringResource(R.string.decode),
                    style = MaterialTheme.typography.h6,
                )
            }

            Button(
                modifier = modifier.weight(0.5f),
                enabled = uiEnabled,
                onClick = onReset,
            ) {
                Text(
                    text = stringResource(R.string.reset),
                    style = MaterialTheme.typography.h6,
                )
            }
        }

        if (decodeUiState.decodedMessage != null) {
            Divider(thickness = 2.dp, modifier = modifier)

            TextField(
                modifier = modifier.fillMaxWidth(),
                value = decodeUiState.decodedMessage,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.decode_output_label)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DecodeScreenPreviewDefault() {
    val encodeUiState = DecodeUiState()
    ButkusAppTheme {
        DecodeScreen(
            uiEnabled = true,
            decodeUiState = encodeUiState,
            onMessageChanged = { },
            canDecode = true,
            onDecode = { },
            onReset = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DecodeScreenPreviewDecodedMessage() {
    val decodeUiState = DecodeUiState(decodedMessage = "Secret message")
    ButkusAppTheme {
        DecodeScreen(
            uiEnabled = true,
            decodeUiState = decodeUiState,
            onMessageChanged = { },
            canDecode = true,
            onDecode = { },
            onReset = { },
        )
    }
}