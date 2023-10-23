package com.hashapps.cadenas.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.AppViewModelProvider

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToNewChannel: () -> Unit,
    onNavigateToImportChannel: () -> Unit,
    onNavigateToChannel: (Int) -> Unit,
    onNavigateToExportChannel: (Int) -> Unit,
    onNavigateToEditChannel: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {}