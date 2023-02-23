package com.hashapps.butkusapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    onGenKey: () -> Unit,
    maxSeedLen: Int,
    onSeedChange: (String) -> Unit,
    maxUrlLen: Int,
    onUrlChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    modifier = modifier.weight(1f),
                    enabled = true,
                    readOnly = true,
                    value = settingsUiState.secretKey,
                    onValueChange = { },
                    singleLine = true,
                    label = { Text(stringResource(R.string.key_label)) },
                    placeholder = { Text(stringResource(R.string.key_placeholder)) },
                )

                FilledTonalIconButton(
                    onClick = onGenKey,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Key,
                        contentDescription = stringResource(R.string.generate_key)
                    )
                }
            }
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = settingsUiState.seedText,
                onValueChange = {
                    if (it.length <= maxSeedLen) {
                        onSeedChange(it)
                    }
                },
                singleLine = true,
                label = { Text(stringResource(R.string.seed_label)) },
                supportingText = {
                    Text(
                        LocalContext.current.getString(
                            R.string.char_counter,
                            settingsUiState.seedText.length,
                            maxSeedLen,
                        )
                    )
                },
            )
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreviewDefault() {
    ButkusAppTheme {
        SettingsScreen(
            settingsUiState = SettingsUiState(),
            onGenKey = { },
            maxSeedLen = 128,
            onSeedChange = { },
            maxUrlLen = 128,
            onUrlChange = { },
        )
    }
}