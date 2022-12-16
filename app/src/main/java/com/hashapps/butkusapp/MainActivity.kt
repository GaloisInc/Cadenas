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

/*@Composable
fun ButkusApp(modifier: Modifier = Modifier) {
    // TODO: This is bad and forces the UI to wait to load! For testing only!
    val context = LocalContext.current
    runBlocking {
        coroutineScope {
            val butkusInit = async { Butkus.initialize(context) }
            butkusInit.await()
            println(Butkus.getInstance().encode("Hello"))
        }
    }

    Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(R.string.test_message),
            style = MaterialTheme.typography.body1,
        )
    }
}*/
