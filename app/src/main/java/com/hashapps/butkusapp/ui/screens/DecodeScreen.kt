package com.hashapps.butkusapp.ui.screens

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
import com.hashapps.butkusapp.ui.DecodeUiState
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

/** The message decoding screen. Consists of:
 * - Text field for the message to decode. Note that before decoding, trailing
 *   tags of the form '#<tag here>' and a single space preceding them will be
 *   stripped off
 * - (If message decoded) The decoded message
 * - Action button (Decode) */
@Composable
fun DecodeScreen(
    modifier: Modifier = Modifier,
    decodeUiState: DecodeUiState,
    onCoverTextChange: (String) -> Unit,
    canDecode: Boolean,
    onDecode: () -> Unit,
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
                enabled = !decodeUiState.inProgress,
                value = decodeUiState.message,
                onValueChange = onCoverTextChange,
                singleLine = false,
                label = { Text(stringResource(R.string.encoded_message_label)) },
                placeholder = { Text(stringResource(R.string.encoded_message_placeholder)) },
            )
        }
        
        Spacer(modifier = modifier.weight(1f))

        if (decodeUiState.decodedMessage != null) {
            TextField(
                modifier = modifier.fillMaxWidth(),
                value = decodeUiState.decodedMessage,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.decode_output_label)) }
            )
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            enabled = !decodeUiState.inProgress && canDecode,
            onClick = onDecode,
        ) {
            Text(
                text = stringResource(R.string.decode),
                style = MaterialTheme.typography.h6,
            )
        }

        if (decodeUiState.inProgress) {
            LinearProgressIndicator(modifier = modifier.fillMaxWidth())
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
            canDecode = true,
            onDecode = { },
        )
    }
}