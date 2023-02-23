package com.hashapps.butkusapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.SettingsUiState
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsUiState: SettingsUiState,
    onKeyChange: (String) -> Unit,
    onGenKey: () -> Unit,
    onSeedChange: (String) -> Unit,
    onRestoreDefaults: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = modifier
                    .padding(end = 8.dp)
                    .weight(1f),
                enabled = false,
                value = settingsUiState.secret_key,
                onValueChange = onKeyChange,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.key_placeholder)) },
            )

            Button(
                onClick = onGenKey
            ) {
                Text(text = stringResource(R.string.generate_key))
            }
        }

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = settingsUiState.seed_text,
            onValueChange = onSeedChange,
            singleLine = true,
            placeholder = { Text(stringResource(R.string.seed_placeholder)) },
        )

        Spacer(modifier = modifier.weight(1f))

        FilledTonalButton(
            modifier = modifier.fillMaxWidth(),
            onClick = onRestoreDefaults,
        ) {
            Text(
                text = stringResource(R.string.restore_defaults),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreviewDefault() {
    ButkusAppTheme {
        SettingsScreen(
            settingsUiState = SettingsUiState(),
            onKeyChange = { },
            onGenKey = { },
            onSeedChange = { },
            onRestoreDefaults = { },
        )
    }
}