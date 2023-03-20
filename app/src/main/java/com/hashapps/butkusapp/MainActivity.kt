package com.hashapps.butkusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.hashapps.butkusapp.ui.models.ButkusAppViewModel
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: ButkusAppViewModel by viewModels()

        setContent {
            ButkusAppTheme {
                ButkusApp(viewModel = viewModel)
            }
        }
    }
}
