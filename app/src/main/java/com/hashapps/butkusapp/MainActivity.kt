package com.hashapps.butkusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ButkusAppTheme {
                ButkusApp()
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
