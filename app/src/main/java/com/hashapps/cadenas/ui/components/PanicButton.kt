package com.hashapps.cadenas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.hashapps.cadenas.R

@Composable
fun PanicButton(viewModel: TopViewModel, onNavigateBack: (() -> Unit)? = null)  {
    var panicButtonState by rememberSaveable { mutableStateOf(false) }
    IconButton(
        enabled = true,
        onClick = { panicButtonState = true },
    ) {
        Icon(
            imageVector = Icons.Outlined.Report,
            contentDescription = stringResource(R.string.emergency)
        )
    }

    if (panicButtonState) {
        DeleteConfirmationDialog(
            confirmationQuestion = stringResource(R.string.emergency_message),
            onDeleteConfirm = {
                panicButtonState = false
                onNavigateBack?.invoke()
                viewModel.deleteAllModels()
            },
            onDeleteCancel = { panicButtonState = false },
        )
    }
}
