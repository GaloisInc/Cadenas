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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.ui.models.EncodeUiState
import com.hashapps.butkusapp.ui.models.EncodeViewModel
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

private val tagRegex = Regex("""\w*[a-zA-Z]\w*""")
private val EncodeUiState.tagValid get() = tagRegex.matches(tagToAdd)
private val EncodeUiState.isErrorTag get() = tagToAdd != "" && !tagValid
private val EncodeUiState.canAddTag get() = !inProgress && tagValid && tagToAdd !in addedTags

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeScreen(
    modifier: Modifier = Modifier,
    vm: EncodeViewModel = viewModel(),
    butkusInitialized: Boolean,
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
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                enabled = !uiState.inProgress,
                value = uiState.message,
                onValueChange = vm::updatePlaintextMessage,
                singleLine = false,
                label = { Text(stringResource(R.string.plaintext_message_label)) },
                trailingIcon = {
                    IconButton(
                        enabled = uiState.message.isNotEmpty(),
                        onClick = vm::clearPlaintextMessage,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.clear_plaintext),
                        )
                    }
                },
                supportingText = {
                    Text(stringResource(R.string.plaintext_message_support))
                },
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
            OutlinedTextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                enabled = !uiState.inProgress,
                value = uiState.tagToAdd,
                onValueChange = vm::updateTagToAdd,
                singleLine = true,
                label = { Text(stringResource(R.string.tag_label)) },
                trailingIcon = {
                    IconButton(
                        enabled = uiState.canAddTag,
                        onClick = { vm.addTag(uiState.tagToAdd) },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_tag),
                        )
                    }
                },
                supportingText = {
                    if (uiState.isErrorTag) {
                        Text(stringResource(R.string.tag_error))
                    } else {
                        Text(stringResource(R.string.tag_support))
                    }
                },
                isError = uiState.isErrorTag,
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
                items(uiState.addedTags.toList()) {
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = { Text(it) },
                        trailingIcon = {
                            Icon(
                                modifier = modifier.clickable(
                                    enabled = !uiState.inProgress
                                ) {
                                    vm.removeTag(it)
                                },
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.delete),
                            )
                        },
                    )
                }
            }
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            Button(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                enabled = butkusInitialized && !uiState.inProgress && uiState.message.isNotEmpty(),
                onClick = vm::encodeMessage,
            ) {
                Text(
                    text = stringResource(R.string.encode),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        if (uiState.inProgress) {
            LinearProgressIndicator(
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )
        }

        if (uiState.encodedMessage != null) {
            ElevatedCard(modifier = modifier.fillMaxWidth()) {
                Row(
                    modifier = modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        LocalContext.current.resources.getQuantityString(
                            R.plurals.result_length,
                            uiState.encodedMessage!!.length,
                            uiState.encodedMessage!!.length,
                        )
                    )

                    IconButton(
                        onClick = vm::clearEncodedMessage,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.clear_encoded_message)
                        )
                    }
                }

                Divider(thickness = 1.dp)

                SelectionContainer {
                    Text(
                        modifier = modifier.padding(8.dp),
                        text = uiState.encodedMessage!!,
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
        EncodeScreen(butkusInitialized = true)
    }
}