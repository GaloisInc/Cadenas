package com.hashapps.cadenas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hashapps.cadenas.ui.CadenasApp
import com.hashapps.cadenas.ui.theme.CadenasAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CadenasAppTheme {
                CadenasApp()
            }
        }
    }
}
