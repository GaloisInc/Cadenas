package com.hashapps.butkusapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.EncodeUiState
import com.hashapps.butkusapp.ui.components.TagEntry
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

/** The message encoding screen. Consists of:
 * - Text field for the message to encode
 * - Text field and button to add alphabetic tags to encoded messages
 * - (If tag set nonempty) Scrollable list of TagEntry
 * - (If message encoded) The encoded message, with tags appended to the end
 *   (e.g. adding the tag 'funny' appends '#funny' to the encoded message)
 * - Action button (Encode) */
@Composable
fun EncodeScreen(
    modifier: Modifier = Modifier,
    encodeUiState: EncodeUiState,
    onPlaintextChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onAddTag: () -> Unit,
    onTagRemove: (String) -> Unit,
    canEncode: Boolean,
    onEncode: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.padding(end = 8.dp),
                enabled = !encodeUiState.inProgress,
                value = encodeUiState.tagToAdd,
                onValueChange = onTagChange,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.tag_placeholder)) },
            )

            OutlinedButton(
                enabled = !encodeUiState.inProgress,
                onClick = onAddTag,
            ) {
                Text(
                    text = stringResource(R.string.add_tag),
                    textAlign = TextAlign.Center,
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
            TextField(
                modifier = modifier.fillMaxWidth(),
                value = encodeUiState.encodedMessage,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.encode_output_label)) }
            )

            Text(
                text = LocalContext.current.getString(
                    R.string.encoded_message_length,
                    encodeUiState.encodedMessage.length,
                )
            )
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            enabled = !encodeUiState.inProgress && canEncode,
            onClick = onEncode,
        ) {
            Text(
                text = stringResource(R.string.encode),
                style = MaterialTheme.typography.h6,
            )
        }

        if (encodeUiState.inProgress) {
            LinearProgressIndicator(modifier = modifier.fillMaxWidth())
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
            canEncode = true,
            onEncode = { },
        )
    }
}