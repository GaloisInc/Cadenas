package com.hashapps.cadenas.ui.settings.models.disk

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.AppViewModelProvider

/**
 * Cadenas model-add screen (from disk).
 *
 * This screen provides an alternative method of installing models: From the
 * device's disk. This requires unzipping a selected ZIP to the application's
 * resources. With permission, Cadenas may remove the source ZIP after
 * installation.
 */
@Composable
fun ModelAddDiskScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelAddDiskViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

}