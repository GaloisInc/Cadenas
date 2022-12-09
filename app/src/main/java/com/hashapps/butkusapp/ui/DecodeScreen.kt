package com.hashapps.butkusapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.data.DecodeUiState

@Composable
fun DecodeScreen(
    decodeUiState: DecodeUiState,
    onMessageChanged: (String) -> Unit,
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
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = decodeUiState.message,
            onValueChange = onMessageChanged,
            singleLine = false,
            label = { Text(stringResource(R.string.encoded_message_label)) },
            placeholder = { Text(stringResource(R.string.encoded_message_placeholder)) },
        )
        
        Spacer(modifier = modifier.weight(1f))

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = modifier.weight(0.5f),
                onClick = onDecode,
            ) {
                Text(
                    text = stringResource(R.string.decode),
                    style = MaterialTheme.typography.h6,
                )
            }

            Button(
                modifier = modifier.weight(0.5f),
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