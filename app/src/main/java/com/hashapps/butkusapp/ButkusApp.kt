package com.hashapps.butkusapp

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hashapps.butkusapp.ui.navigation.ButkusRootNavHost

@Composable
fun ButkusApp(navController: NavHostController = rememberNavController()) {
    ButkusRootNavHost(navController = navController)
}