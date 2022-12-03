package com.hashapps.butkusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.hashapps.butkusapp.ui.theme.ButkusAppTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

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

@Composable
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
}
