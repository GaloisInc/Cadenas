package com.hashapps.butkusapp.ui.processing

data class ProcessingUiState(
    val toProcess: String = "",
    val actionEnabled: Boolean = false,
    val result: String? = null,
    val inProgress: Boolean = false,
)

fun ProcessingUiState.isValid() = toProcess.isNotBlank() && !inProgress
