package com.hashapps.cadenas.ui.settings.profile

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.data.Profile
import com.hashapps.cadenas.data.QRAnalyzer
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar
import java.util.concurrent.Executors

private val profileRegex = Regex("""key:([0-9a-fA-F]{64});prompt:([\p{Print}\s]+);model:([a-zA-Z\d]+)""")

fun String.toProfile(): Profile? {
    return profileRegex.matchEntire(this)?.groupValues?.let {
        Profile(
            name = "",
            description = "",
            key = it[1],
            seed = it[2],
            selectedModel = it[3],
            tag ="",
        )
    }
}

/**
 * Cadenas profile-importing screen.
 *
 * Cadenas messaging profiles may be imported from QR codes, enabling sharing
 * of profiles, enabling communication.
 *
 * Only QR codes containing the proper text data are accepted. These are used
 * to create a new profile in the app database, which is then edited to provide
 * a name and description.
 */
@Composable
fun ProfileImportScreen(
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateProfileEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileImportViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current

    var hasCameraPermission by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            hasCameraPermission = isGranted
        }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.import_profile),
                navigateUp = onNavigateUp,
            )
        },
    ) { innerPadding ->
        ProfileImportBody(
            modifier = modifier.padding(innerPadding),
            onImportClick = { viewModel.saveProfileAndGoToEdit(it, onNavigateProfileEdit) },
        )
        if (!hasCameraPermission) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.oops)) },
                text = { Text(stringResource(R.string.camera_permission_error)) },
                modifier = Modifier.padding(16.dp),
                confirmButton = {
                    TextButton(onClick = onNavigateBack) {
                        Text(text = stringResource(R.string.go_back))
                    }
                },
            )
        }
    }
}

@Composable
private fun ProfileImportBody(
    onImportClick: (Profile) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        var newProfile: Profile? by remember { mutableStateOf(null) }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                val localContext = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current

                var previewBoth by remember {
                    mutableStateOf<Preview?>(null)
                }
                AndroidView(
                    factory = { context ->
                        PreviewView(context).apply {
                            this.scaleType = PreviewView.ScaleType.FILL_CENTER
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { previewView ->
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        val cameraExecutor = Executors.newSingleThreadExecutor()
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(localContext)

                        cameraProviderFuture.addListener({
                            previewBoth = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraProvider = cameraProviderFuture.get()
                            val barcodeAnalyzer = QRAnalyzer { barcodes ->
                                barcodes.forEach { barcode ->
                                    barcode.rawValue?.let { barcodeValue ->
                                        newProfile = barcodeValue.toProfile()
                                    }
                                }
                            }
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(cameraExecutor, barcodeAnalyzer)
                                }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    previewBoth,
                                    imageAnalysis,
                                )
                            } catch (e: Exception) {
                                Log.e("ProfileImportScreen", "CameraPreview: ${e.localizedMessage}")
                            }
                        }, ContextCompat.getMainExecutor(localContext))
                    },
                )
            }
        }

        if (newProfile != null) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.channel_found)) },
                text = { Text(stringResource(R.string.finish_import)) },
                modifier = Modifier.padding(16.dp),
                confirmButton = {
                    TextButton(onClick = { onImportClick(newProfile!!) }) {
                        Text(stringResource(R.string.next))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { newProfile = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}