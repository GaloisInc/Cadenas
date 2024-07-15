package com.hashapps.cadenas.ui.settings.models.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.SimCardDownload
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    onNavigateToModelAddDisk: () -> Unit,
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
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopStart),
                    ) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.CreateNewFolder,
                                contentDescription = stringResource(R.string.add_model)
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.from_url)) },
                                onClick = {
                                    expanded = false
                                    onNavigateToModelAdd("")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.CloudDownload,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.from_disk)) },
                                onClick = {
                                    expanded = false
                                    onNavigateToModelAddDisk()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.SimCardDownload,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
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