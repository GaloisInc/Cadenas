package com.hashapps.cadenas.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.navigation.NavigationDestination
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

private const val MAX_LEN = 128

object ModelAddDestination : NavigationDestination {
    override val route = "model_add"
    override val titleRes = R.string.add_model
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelAddScreen(
    navigateBack: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelAddViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(ModelAddDestination.titleRes),
                canNavigateUp = true,
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        ModelAddBody(
            modifier = modifier.padding(innerPadding),
            modelUiState = viewModel.modelUiState,
            onModelValueChange = viewModel::updateUiState,
            onDownloadClick = {
                viewModel.saveModel()
                navigateBack()
            },
        )
    }
}

@Composable
fun ModelAddBody(
    modifier: Modifier,
    modelUiState: ModelUiState,
    onModelValueChange: (ModelUiState) -> Unit,
    onDownloadClick: () -> Unit,
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
        )

        Button(
            onClick = onDownloadClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = modelUiState.actionEnabled,
        ) {
            Text(
                text = stringResource(R.string.download),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelInputForm(
    modelUiState: ModelUiState,
    modifier: Modifier = Modifier,
    onValueChange: (ModelUiState) -> Unit = {},
    urlEnabled: Boolean = true,
) {
    val focusManager = LocalFocusManager.current

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        var displaySupportText = modelUiState.name == "" || modelUiState.isNameValid()
        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = modelUiState.name,
            onValueChange = { onValueChange(modelUiState.copy(name = it)) },
            singleLine = true,
            label = { Text(stringResource(R.string.model_name_label)) },
            supportingText = {
                if (displaySupportText) {
                    Text(stringResource(R.string.model_name_support))
                } else {
                    Text(stringResource(R.string.name_error))
                }
            },
            isError = !displaySupportText,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = modelUiState.description,
            onValueChange = { onValueChange(modelUiState.copy(description = it)) },
            singleLine = true,
            label = { Text(stringResource(R.string.model_description_label)) },
            supportingText = { Text(stringResource(R.string.model_description_support)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        displaySupportText = modelUiState.url == "" || modelUiState.isUrlValid()
        OutlinedTextField(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            enabled = urlEnabled,
            value = modelUiState.url,
            onValueChange = { onValueChange(modelUiState.copy(url = it.take(MAX_LEN))) },
            singleLine = true,
            label = { Text(stringResource(R.string.url_label)) },
            supportingText = {
                if (displaySupportText) {
                    Text(
                        LocalContext.current.getString(
                            R.string.url_support,
                            modelUiState.url.length,
                            MAX_LEN,
                        )
                    )
                } else {
                    Text(stringResource(R.string.url_error))
                }
            },
            isError = !displaySupportText,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
    }
}
