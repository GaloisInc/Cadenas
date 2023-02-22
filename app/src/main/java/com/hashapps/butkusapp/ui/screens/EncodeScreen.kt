package com.hashapps.butkusapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.EncodeUiState
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

/** The message encoding screen. Consists of:
 * - Text field for the message to encode
 * - Text field and button to add alphabetic tags to encoded messages
 * - (If tag set nonempty) Scrollable list of TagEntry
 * - (If message encoded) The encoded message, with tags appended to the end
 *   (e.g. adding the tag 'funny' appends '#funny' to the encoded message)
 * - Action button (Encode) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeScreen(
    modifier: Modifier = Modifier,
    encodeUiState: EncodeUiState,
    onPlaintextChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onAddTag: () -> Unit,
    onTagRemove: (String) -> Unit,
    butkusInitialized: Boolean,
    onEncode: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            enabled = !encodeUiState.inProgress,
            value = encodeUiState.message,
            onValueChange = onPlaintextChange,
            singleLine = false,
            label = { Text(stringResource(R.string.plaintext_message_label)) },
            placeholder = { Text(stringResource(R.string.plaintext_message_placeholder)) },
        )

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = modifier.padding(end=8.dp).weight(1f),
                enabled = !encodeUiState.inProgress,
                value = encodeUiState.tagToAdd,
                onValueChange = onTagChange,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.tag_placeholder)) },
            )

            FilledIconButton(
                enabled = !encodeUiState.inProgress,
                onClick = onAddTag,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_tag),
                )
            }
        }

        LazyColumn(modifier = modifier.weight(1f)) {
            items(encodeUiState.addedTags.toList()) { tag ->
                TagEntry(
                    uiEnabled = !encodeUiState.inProgress,
                    tag = tag,
                    onTagRemove = { onTagRemove(tag) },
                )
            }
        }

        if (encodeUiState.encodedMessage != null) {
            Text(
                modifier = modifier.align(Alignment.Start),
                text = LocalContext.current.getString(
                    R.string.encoded_message_length,
                    encodeUiState.encodedMessage.length,
                )
            )

            TextField(
                modifier = modifier.fillMaxWidth(),
                value = encodeUiState.encodedMessage,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.encode_output_label)) }
            )
        }

        if (encodeUiState.inProgress) {
            LinearProgressIndicator(modifier = modifier.fillMaxWidth())
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            enabled = butkusInitialized && !encodeUiState.inProgress && encodeUiState.message.isNotEmpty(),
            onClick = onEncode,
        ) {
            Text(
                text = stringResource(R.string.encode),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
fun TagEntry(
    uiEnabled: Boolean,
    tag: String,
    onTagRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = tag)

        FilledIconButton(
            enabled = uiEnabled,
            onClick = onTagRemove,
        ) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EncodeScreenPreviewDefault() {
    ButkusAppTheme {
        EncodeScreen(
            encodeUiState = EncodeUiState(),
            onPlaintextChange = { },
            onTagChange = { },
            onAddTag = { },
            onTagRemove = { },
            butkusInitialized = true,
            onEncode = { },
        )
    }
}