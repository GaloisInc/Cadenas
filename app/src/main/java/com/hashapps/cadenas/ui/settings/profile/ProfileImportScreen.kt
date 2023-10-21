package com.hashapps.cadenas.ui.settings.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.ui.AppViewModelProvider

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

}