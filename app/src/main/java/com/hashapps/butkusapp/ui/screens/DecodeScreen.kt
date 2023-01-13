package com.hashapps.butkusapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.models.ButkusViewModel
import com.hashapps.butkusapp.ui.models.DecodeViewModel
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

@Composable
fun DecodeScreen(
    modifier: Modifier = Modifier,
    decodeViewModel: DecodeViewModel = DecodeViewModel(),
) {
    val decodeUiState by decodeViewModel.decodeUiState.collectAsState()

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
                enabled = ButkusViewModel.SharedViewState.uiEnabled,
                value = decodeUiState.message,
                onValueChange = { decodeViewModel.updateEncodedMessage(it) },
                singleLine = false,
                label = { Text(stringResource(R.string.encoded_message_label)) },
                placeholder = { Text(stringResource(R.string.encoded_message_placeholder)) },
            )
        }
        
        Spacer(modifier = modifier.weight(1f))

        if (decodeUiState.decodedMessage != null) {
            Divider(thickness = 2.dp, modifier = modifier)

            TextField(
                modifier = modifier.fillMaxWidth(),
                value = decodeUiState.decodedMessage!!,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.decode_output_label)) }
            )
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            enabled = ButkusViewModel.SharedViewState.uiEnabled && decodeViewModel.canRun,
            onClick = { ButkusViewModel.SharedViewState.isRunning = true },
        ) {
            Text(
                text = stringResource(R.string.decode),
                style = MaterialTheme.typography.h6,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DecodeScreenPreviewDefault() {
    ButkusAppTheme {
        DecodeScreen()
    }
}