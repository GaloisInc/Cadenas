package com.hashapps.cadenas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hashapps.cadenas.ui.CadenasApp
import com.hashapps.cadenas.ui.theme.CadenasAppTheme

/**
 * The Cadenas [activity][ComponentActivity].
 *
 * Defines the activity-creation for Cadenas. In particular, it sets the
 * content of the activity to the [CadenasApp] composable element, which hosts
 * the main navigation graph of the application.
 */
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
