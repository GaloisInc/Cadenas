package com.hashapps.cadenas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hashapps.cadenas.ui.navigation.CadenasRootNavHost

@Composable
fun CadenasApp(
    navController: NavHostController = rememberNavController(),
    viewModel: CadenasViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val isNotFirstRun by viewModel.isNotFirstRun.collectAsState()

    CadenasRootNavHost(navController = navController, firstTime = !isNotFirstRun, completeFirstRun = viewModel::completeFirstRun)
}