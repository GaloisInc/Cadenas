package com.hashapps.butkusapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val focusManager = LocalFocusManager.current

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                enabled = !encodeUiState.inProgress,
                value = encodeUiState.message,
                onValueChange = onPlaintextChange,
                singleLine = false,
                label = { Text(stringResource(R.string.plaintext_message_label)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                ),
            )
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            val tagValid = Regex("""\w*[a-zA-Z]\w*""").matches(encodeUiState.tagToAdd)
            val isError = encodeUiState.tagToAdd != "" && !tagValid
            val canAdd =
                !encodeUiState.inProgress && tagValid && encodeUiState.tagToAdd !in encodeUiState.addedTags

            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                enabled = !encodeUiState.inProgress,
                value = encodeUiState.tagToAdd,
                onValueChange = onTagChange,
                singleLine = true,
                label = { Text(stringResource(R.string.tag_label)) },
                trailingIcon = {
                    IconButton(
                        enabled = canAdd,
                        onClick = onAddTag,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = stringResource(R.string.add_tag),
                        )
                    }
                },
                supportingText = {
                    if (isError) {
                        Text(stringResource(R.string.tag_error))
                    } else {
                        Text(stringResource(R.string.tag_support))
                    }
                },
                isError = isError,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
            )

            LazyRow(
                modifier = modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(encodeUiState.addedTags.toList()) { tag ->
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = { Text(text = tag) },
                        trailingIcon = {
                            Icon(
                                modifier = modifier.clickable(
                                    enabled = !encodeUiState.inProgress
                                ) { onTagRemove(tag) },
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.delete),
                            )
                        },
                    )
                }
            }
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

        if (encodeUiState.inProgress) {
            LinearProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally))
        }

        if (encodeUiState.encodedMessage != null) {
            ElevatedCard(modifier = modifier.fillMaxWidth()) {
                Text(
                    modifier = modifier.padding(8.dp),
                    text = LocalContext.current.getString(
                        R.string.encoded_message_length,
                        encodeUiState.encodedMessage.length,
                    )
                )

                Divider(thickness = 1.dp)

                SelectionContainer {
                    Text(
                        modifier = modifier.padding(8.dp),
                        text = encodeUiState.encodedMessage,
                    )
                }
            }
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