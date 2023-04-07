package com.hashapps.cadenas.ui.model

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.AppViewModelProvider
import com.hashapps.cadenas.ui.components.DeleteConfirmationDialog
import com.hashapps.cadenas.ui.navigation.NavigationDestination
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

object ManageModelsDestination : NavigationDestination {
    override val route = "manage_models"
    override val titleRes = R.string.manage_models
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageModelsScreen(
    navigateUp: () -> Unit,
    navigateToModelAdd: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageModelsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val selectedModel by viewModel.selectedModel.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(ManageModelsDestination.titleRes),
                canNavigateUp = true,
                navigateUp = navigateUp,
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = navigateToModelAdd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_item),
                )
            }
        },
    ) { innerPadding ->
        ManageModelsBody(
            modifier = modifier.padding(innerPadding),
            models = viewModel.availableModels,
            selectedModel = selectedModel,
            onModelDelete = viewModel::deleteModel,
        )
    }
}

@Composable
fun ManageModelsBody(
    models: List<String>,
    selectedModel: String?,
    onModelDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ModelList(
            models = models,
            selectedModel = selectedModel,
            onModelDelete = onModelDelete,
        )
    }
}

@Composable
fun ModelList(
    models: List<String>,
    selectedModel: String?,
    onModelDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = models) {
            CadenasModel(
                model = it,
                selectedModel = selectedModel,
                onModelDelete = onModelDelete,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadenasModel(
    model: String,
    selectedModel: String?,
    onModelDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        ListItem(
            headlineText = { Text(model) },
            trailingContent = {
                Box(
                    modifier.wrapContentSize(Alignment.TopStart),
                ) {
                    IconButton(
                        enabled = model != selectedModel,
                        onClick = { deleteConfirmationRequired = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            contentDescription = stringResource(R.string.delete),
                        )
                    }
                }
            },
        )

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                confirmationQuestion = stringResource(R.string.delete_model_question),
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onModelDelete(model)
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
            )
        }
    }
}