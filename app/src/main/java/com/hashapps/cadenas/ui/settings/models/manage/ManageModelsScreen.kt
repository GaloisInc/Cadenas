package com.hashapps.cadenas.ui.settings.models.manage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashapps.cadenas.AppViewModelProvider
import com.hashapps.cadenas.R
import com.hashapps.cadenas.data.models.Model
import com.hashapps.cadenas.ui.components.DeleteConfirmationDialog

/**
 * Cadenas model-management screen.\
 *
 * Note that deleting a model also deletes any channels associated with that
 * model; they become meaningless without the model, after all. The user is
 * informed of this via confirmation dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageModelsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToModelAdd: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageModelsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val models by viewModel.models.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.manage_models)) },
                modifier = modifier,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToModelAdd("") }) {
                        Icon(
                            imageVector = Icons.Filled.CreateNewFolder,
                            contentDescription = stringResource(R.string.add_model)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        ModelList(
            modifier = modifier.padding(innerPadding),
            models = models,
            onModelDelete = viewModel::deleteModel,
        )
    }
}

@Composable
private fun ModelList(
    models: List<Model>,
    onModelDelete: (Model) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (models.isEmpty()) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.model_placeholder),
                )
            }
        } else {
            models.forEach {
                CadenasModel(
                    model = it,
                    onModelDelete = onModelDelete,
                )
            }
        }
    }
}

@Composable
private fun CadenasModel(
    model: Model,
    onModelDelete: (Model) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        ListItem(
            headlineContent = { Text(model.name) },
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