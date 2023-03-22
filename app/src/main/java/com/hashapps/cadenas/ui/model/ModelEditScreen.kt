package com.hashapps.cadenas.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.navigation.NavigationDestination
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

object ModelEditDestination : NavigationDestination {
    override val route = "model_edit"
    override val titleRes = R.string.edit_model
    const val modelIdArg = "modelId"
    val routeWithArgs = "$route/{$modelIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelEditScreen(
    navigateBack: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(ModelEditDestination.titleRes),
                canNavigateUp = true,
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        ModelEditBody(
            modifier = modifier.padding(innerPadding),
            modelUiState = viewModel.modelUiState,
            onModelValueChange = viewModel::updateUiState,
            onSaveClick = {
                viewModel.updateModel()
                navigateBack()
            }
        )
    }
}

@Composable
fun ModelEditBody(
    modifier: Modifier = Modifier,
    modelUiState: ModelUiState,
    onModelValueChange: (ModelUiState) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ModelInputForm(
            modelUiState = modelUiState,
            onValueChange = onModelValueChange,
            urlEnabled = false,
        )

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = modelUiState.actionEnabled,
        ) {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
