package com.hashapps.cadenas.ui.settings.models.manage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.ui.components.DeleteConfirmationDialog
import com.hashapps.cadenas.ui.settings.SettingsTopAppBar

/**
 * Cadenas model-management screen.\
 *
 * Note that deleting a model also deletes any channels associated with that
 * model; they become meaningless without the model, after all. The user is
 * informed of this via confirmation dialog.
 */
@Composable
fun ManageModelsScreen(
    navigateUp: () -> Unit,
    navigateToModelAdd: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageModelsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.manage_models),
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
        ModelList(
            modifier = modifier.padding(innerPadding),
            models = viewModel.availableModels,
            onModelDelete = viewModel::deleteModel,
        )
    }
}

@Composable
private fun ModelList(
    models: List<String>,
    onModelDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        models.forEach {
            CadenasModel(
                model = it,
                onModelDelete = onModelDelete,
            )
        }
    }
}

@Composable
private fun CadenasModel(
    model: String,
    onModelDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        ListItem(
            headlineContent = { Text(model) },
            trailingContent = {
                Box(
                    modifier.wrapContentSize(Alignment.TopStart),
                ) {
                    IconButton(
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