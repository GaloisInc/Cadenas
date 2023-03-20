package com.hashapps.butkusapp.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.data.model.Model
import com.hashapps.butkusapp.ui.AppViewModelProvider
import com.hashapps.butkusapp.ui.components.DeleteConfirmationDialog
import com.hashapps.butkusapp.ui.navigation.NavigationDestination
import com.hashapps.butkusapp.ui.settings.SettingsTopAppBar

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
    val models by viewModel.models.collectAsState()

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
            models = models,
            onModelDelete = {
                viewModel.deleteModel(it)
            }
        )
    }
}

@Composable
fun ManageModelsBody(
    models: List<Model>,
    onModelDelete: (Int) -> Unit,
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
            onModelDelete = { onModelDelete(it.id) }
        )
    }
}

@Composable
fun ModelList(
    models: List<Model>,
    onModelDelete: (Model) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = models, key = { it.id }) {
            ButkusModel(
                model = it,
                canDelete = models.size > 1,
                onModelDelete = onModelDelete,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButkusModel(
    model: Model,
    canDelete: Boolean,
    onModelDelete: (Model) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        ListItem(
            headlineText = { Text(model.name) },
            supportingText = { Text(model.uri) },
            trailingContent = {
                IconButton(
                    enabled = canDelete,
                    onClick = { deleteConfirmationRequired = true },
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = stringResource(R.string.delete),
                    )
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