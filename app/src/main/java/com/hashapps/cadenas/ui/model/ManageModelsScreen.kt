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
import com.hashapps.cadenas.data.model.Model
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
    navigateToManageProfiles: (Int) -> Unit,
    navigateToModelEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageModelsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val selectedModelId by viewModel.selectedModelId.collectAsState()
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
            selectedModelId = selectedModelId,
            onManageProfiles = navigateToManageProfiles,
            onModelEdit = navigateToModelEdit,
            onModelDelete = {
                viewModel.deleteModel(it)
            }
        )
    }
}

@Composable
fun ManageModelsBody(
    models: List<Model>,
    selectedModelId: Int?,
    onManageProfiles: (Int) -> Unit,
    onModelEdit: (Int) -> Unit,
    onModelDelete: (Model) -> Unit,
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
            selectedModelId = selectedModelId,
            onManageProfiles = onManageProfiles,
            onModelEdit = onModelEdit,
            onModelDelete = onModelDelete,
        )
    }
}

@Composable
fun ModelList(
    models: List<Model>,
    selectedModelId: Int?,
    onManageProfiles: (Int) -> Unit,
    onModelEdit: (Int) -> Unit,
    onModelDelete: (Model) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = models, key = { it.id }) {
            CadenasModel(
                model = it,
                selectedModelId = selectedModelId,
                onManageProfiles = onManageProfiles,
                onModelEdit = onModelEdit,
                onModelDelete = onModelDelete,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadenasModel(
    model: Model,
    selectedModelId: Int?,
    onManageProfiles: (Int) -> Unit,
    onModelEdit: (Int) -> Unit,
    onModelDelete: (Model) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        var expanded by remember { mutableStateOf(false) }

        ListItem(
            headlineText = { Text(model.name) },
            supportingText = { Text(model.description) },
            leadingContent = {
                if (model.id == selectedModelId) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.model_selected),
                    )
                }
            },
            trailingContent = {
                Box(
                    modifier.wrapContentSize(Alignment.TopStart),
                ) {
                    IconButton(
                        onClick = { expanded = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = stringResource(R.string.model_menu),
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.profiles)) },
                            onClick = { onManageProfiles(model.id) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.SwitchAccount,
                                    contentDescription = null
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit)) },
                            onClick = { onModelEdit(model.id) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete)) },
                            onClick = { deleteConfirmationRequired = true },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.DeleteForever,
                                    contentDescription = null
                                )
                            },
                            enabled = model.id != selectedModelId,
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