package com.hashapps.cadenas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hashapps.cadenas.ui.navigation.CadenasRootNavHost

@Composable
fun CadenasApp(navController: NavHostController = rememberNavController()) {
    CadenasRootNavHost(navController = navController)
}