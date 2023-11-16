package com.hashapps.cadenas.ui.channels.export

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

/**
 * Cadenas channel-exporting screen.
 *
 * Cadenas messaging channels may be exported in the form of a QR code,
 * enabling others to import the channel, enabling communication.
 *
 * QR codes are shown for other devices to scan immediately, and can be
 * saved to the device to be shared by other means.
 */
@Composable
fun ChannelExportScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChannelExportViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    var showPermissionError by rememberSaveable { mutableStateOf(false) }

    val permissionRequired = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.saveQRBitmap()
                onNavigateBack()
            } else {
                showPermissionError = true
            }
        }

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = LocalContext.current.getString(
                    R.string.export_channel,
                    viewModel.channelName,
                ),
                onNavigateBack = onNavigateBack,
            )
        },
    ) { innerPadding ->
        val context = LocalContext.current
        ChannelExportBody(
            modifier = modifier.padding(innerPadding),
            qrBitmap = viewModel.qrBitmap,
            onSaveClick = {
                if (permissionRequired) {
                    when (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) {
                        PackageManager.PERMISSION_GRANTED -> {
                            viewModel.saveQRBitmap()
                            onNavigateBack()
                        }

                        else -> {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                } else {
                    viewModel.saveQRBitmap()
                    onNavigateBack()
                }
            },
        )

        if (showPermissionError) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.oops)) },
                text = { Text(stringResource(R.string.storage_permission_error)) },
                modifier = Modifier.padding(16.dp),
                confirmButton = {
                    TextButton(onClick = { showPermissionError = false }) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                }
            )
        }
    }
}

@Composable
private fun ChannelExportBody(
    qrBitmap: ImageBitmap?,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (qrBitmap != null) {
            ElevatedCard(
                modifier = modifier.fillMaxWidth(),
            ) {
                Image(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    bitmap = qrBitmap,
                    contentDescription = stringResource(R.string.qr_description),
                )
            }
        }

        Button(
            onClick = onSaveClick,
            enabled = qrBitmap != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}