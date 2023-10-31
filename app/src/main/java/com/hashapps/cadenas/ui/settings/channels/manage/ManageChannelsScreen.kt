package com.hashapps.cadenas.ui.settings.channels.manage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.data.Channel
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.ui.components.DeleteConfirmationDialog
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

/**
 * Cadenas channel-management screen.
 *
 * Cadenas allows the creation of any number of messaging channels. These
 * channels determine how messages are to be encoded and decoded, and are
 * intended to be shared between communicating parties.
 *
 * Channels can be added at any time, from scratch or by importing from QR.
 *
 * Channels can be edited at any time - this is purely cosmetic, so it can't
 * have an effect on the business of Cadenas.
 */
@Composable
fun ManageChannelsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToChannelEntry: () -> Unit,
    onNavigateToChannelImport: () -> Unit,
    onNavigateToChannelExport: (Int) -> Unit,
    onNavigateToChannelEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageChannelsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val selectedChannel by viewModel.selectedChannel.collectAsState()
    val channels by viewModel.channels.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.manage_channels),
                navigateUp = onNavigateUp,
            )
        },
        floatingActionButton = {
            var expanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart),
            ) {
                LargeFloatingActionButton(
                    onClick = { expanded = true },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Message,
                        contentDescription = stringResource(R.string.add_item)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.new_label)) },
                        onClick = {
                            expanded = false
                            onNavigateToChannelEntry()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                        },
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.import_label)) },
                        onClick = {
                            expanded = false
                            onNavigateToChannelImport()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.FileDownload,
                                contentDescription = null,
                            )
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        ChannelList(
            modifier = modifier.padding(innerPadding),
            channels = channels,
            selectedChannelId = selectedChannel?.id,
            onChannelSelect = { viewModel.selectChannel(it.id) },
            onChannelExport = onNavigateToChannelExport,
            onChannelEdit = onNavigateToChannelEdit,
            onChannelDelete = viewModel::deleteChannel,
        )
    }
}

@Composable
private fun ChannelList(
    channels: List<Channel>,
    selectedChannelId: Int?,
    onChannelSelect: (Channel) -> Unit,
    onChannelExport: (Int) -> Unit,
    onChannelEdit: (Int) -> Unit,
    onChannelDelete: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        channels.forEach {
            Channel(
                channel = it,
                selectedChannelId = selectedChannelId,
                onChannelSelect = onChannelSelect,
                onChannelExport = onChannelExport,
                onChannelEdit = onChannelEdit,
                onChannelDelete = onChannelDelete,
            )
        }
    }
}

@Composable
private fun Channel(
    channel: Channel,
    selectedChannelId: Int?,
    onChannelSelect: (Channel) -> Unit,
    onChannelExport: (Int) -> Unit,
    onChannelEdit: (Int) -> Unit,
    onChannelDelete: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        var expanded by remember { mutableStateOf(false) }

        ListItem(
            headlineContent = { Text(channel.name) },
            supportingContent = { Text(channel.description) },
            leadingContent = {
                RadioButton(
                    selected = channel.id == selectedChannelId,
                    onClick = { onChannelSelect(channel) },
                )
            },
            trailingContent = {
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart),
                ) {
                    IconButton(
                        onClick = { expanded = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = stringResource(R.string.channel_menu),
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.export)) },
                            onClick = {
                                expanded = false
                                onChannelExport(channel.id)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.FileUpload,
                                    contentDescription = null
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit)) },
                            onClick = {
                                expanded = false
                                onChannelEdit(channel.id)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete)) },
                            onClick = {
                                expanded = false
                                deleteConfirmationRequired = true
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.DeleteForever,
                                    contentDescription = null
                                )
                            },
                            enabled = channel.id != selectedChannelId,
                        )
                    }
                }
            },
        )

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                confirmationQuestion = stringResource(R.string.delete_channel_question),
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onChannelDelete(channel)
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
            )
        }
    }
}