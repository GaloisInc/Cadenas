package com.hashapps.butkusapp.ui

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
import com.hashapps.butkusapp.data.EncodeUiState
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

@Composable
fun EncodeScreen(
    encodeUiState: EncodeUiState,
    onMessageChanged: (String) -> Unit,
    onTagToAddChanged: (String) -> Unit,
    onAddTag: () -> Unit,
    onDeleteTag: (String) -> () -> Unit,
    onEncode: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
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
            value = encodeUiState.message,
            onValueChange = onMessageChanged,
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
                value = encodeUiState.tagToAdd,
                onValueChange = onTagToAddChanged,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.tag_placeholder)) },
            )

            OutlinedButton(onClick = onAddTag) {
                Text(
                    text = stringResource(R.string.add_tag),
                    textAlign = TextAlign.Center,
                )
            }
        }

        LazyColumn(modifier = modifier.weight(1f)) {
            items(encodeUiState.addedTags.toList()) { tag ->
                TagEntry(tag, onTagRemove = onDeleteTag(tag))
            }
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = modifier.weight(0.5f),
                onClick = onEncode,
            ) {
                Text(
                    text = stringResource(R.string.encode),
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

        if (encodeUiState.encodedMessage != null) {
            Divider(thickness = 2.dp, modifier = modifier)

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
    }
}

@Composable
fun TagEntry(
    tag: String,
    onTagRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = tag, style = MaterialTheme.typography.caption)
        Button(onClick = onTagRemove) {
            Text(
                text = stringResource(R.string.delete),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EncodeScreenPreviewDefault() {
    val encodeUiState = EncodeUiState()
    ButkusAppTheme {
        EncodeScreen(
            encodeUiState = encodeUiState,
            onMessageChanged = { },
            onTagToAddChanged = { },
            onAddTag = { },
            onDeleteTag = {_ -> { } },
            onEncode = { },
            onReset = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EncodeScreenPreviewNoTagsEncodedMessage() {
    val encodeUiState = EncodeUiState(encodedMessage = "I'm hiding a secret")
    ButkusAppTheme {
        EncodeScreen(
            encodeUiState = encodeUiState,
            onMessageChanged = { },
            onTagToAddChanged = { },
            onAddTag = { },
            onDeleteTag = {_ -> { } },
            onEncode = { },
            onReset = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EncodeScreenPreviewTags() {
    val encodeUiState = EncodeUiState(addedTags = setOf("funny", "meme", "random"))
    ButkusAppTheme {
        EncodeScreen(
            encodeUiState = encodeUiState,
            onMessageChanged = { },
            onTagToAddChanged = { },
            onAddTag = { },
            onDeleteTag = {_ -> { } },
            onEncode = { },
            onReset = { },
        )
    }
}