package com.hashapps.butkusapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.hashapps.butkusapp.ui.ButkusViewModel
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: ButkusViewModel by viewModels()

        // If the activity was started via SEND intent with plaintext, update
        // the viewModel to hold the sent text on the Decode screen
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    val message = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (message != null) {
                        viewModel.updateEncodedMessage(message)
                    }
                }
            }
            else -> {}
        }

        setContent {
            ButkusAppTheme {
                ButkusApp(viewModel = viewModel)
            }
        }
    }
}
