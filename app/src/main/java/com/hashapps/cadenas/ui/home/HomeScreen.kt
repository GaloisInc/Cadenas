package com.hashapps.cadenas.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.R
import com.hashapps.cadenas.data.channels.Channel
import com.hashapps.cadenas.ui.components.DeleteConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToNewChannel: () -> Unit,
    onNavigateToImportChannel: () -> Unit,
    onNavigateToChannel: (Long, String) -> Unit,
    onNavigateToExportChannel: (Long) -> Unit,
    onNavigateToEditChannel: (Long) -> Unit,
    modifier: Modifier = Modifier,
    savedQRCodeNotificationRequired: Boolean = false,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val channels by viewModel.channels.collectAsState()
    val sharedText by viewModel.sharedTextState.collectAsState()

    var qrCodeNotificationRequired by rememberSaveable { mutableStateOf(savedQRCodeNotificationRequired) }
    val snackbarHostState = remember { SnackbarHostState() }
    if (qrCodeNotificationRequired) {
        val message = stringResource(R.string.qr_code_save_notification)
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = message
            )
        }
        qrCodeNotificationRequired = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (sharedText.isNotEmpty()) {
                        Text(stringResource(R.string.choose_decoder))
                    } else {
                        Text(stringResource(R.string.app_name))
                    }
                },
                modifier = modifier,
                navigationIcon = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            var expanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopStart),
            ) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.add_channel)) },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Message,
                            contentDescription = null
                        )
                    },
                    onClick = { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.new_label)) },
                        onClick = {
                            expanded = false
                            onNavigateToNewChannel()
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        },
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.import_label)) },
                        onClick = {
                            expanded = false
                            onNavigateToImportChannel()
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.FileDownload, contentDescription = null)
                        },
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        ) { innerPadding ->
        ChannelList(
            modifier = modifier.padding(innerPadding),
            channels = channels,
            toDecode = sharedText,
            onChannelSelect = onNavigateToChannel,
            onChannelExport = onNavigateToExportChannel,
            onChannelEdit = onNavigateToEditChannel,
            onChannelDelete = viewModel::deleteChannel,
        )
    }
}

@Composable
private fun ChannelList(
    channels: List<Channel>,
    toDecode: String,
    onChannelSelect: (Long, String) -> Unit,
    onChannelExport: (Long) -> Unit,
    onChannelEdit: (Long) -> Unit,
    onChannelDelete: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        if (channels.isEmpty()) {
            ElevatedCard(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.channel_placeholder),
                )
            }
        } else {
            channels.forEach {
                Channel(
                    channel = it,
                    toDecode = toDecode,
                    onChannelSelect = onChannelSelect,
                    onChannelExport = onChannelExport,
                    onChannelEdit = onChannelEdit,
                    onChannelDelete = onChannelDelete,
                )

                HorizontalDivider(thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun Channel(
    channel: Channel,
    toDecode: String,
    onChannelSelect: (Long, String) -> Unit,
    onChannelExport: (Long) -> Unit,
    onChannelEdit: (Long) -> Unit,
    onChannelDelete: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onChannelSelect(channel.id, toDecode) },
        headlineContent = { Text(channel.name) },
        supportingContent = { Text(channel.description) },
        leadingContent = {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                val circleColor = MaterialTheme.colorScheme.primaryContainer
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(SolidColor(circleColor))
                }

                val textColor = MaterialTheme.colorScheme.onPrimaryContainer
                val initial = channel.name.take(1).uppercase()
                Text(text = initial, color = textColor)
            }
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