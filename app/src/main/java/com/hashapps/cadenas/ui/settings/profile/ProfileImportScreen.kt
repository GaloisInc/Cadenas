package com.hashapps.cadenas.ui.settings.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

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
    onNavigateUp: () -> Unit,
    onNavigateProfileEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileImportViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
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
            onImportClick = onNavigateProfileEdit,
        )
    }
}

@Composable
private fun ProfileImportBody(
    onImportClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {

        }

        Button(
            onClick = { onImportClick(0) },
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.import_label),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}