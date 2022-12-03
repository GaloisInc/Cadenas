package com.hashapps.butkusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
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

@Composable
fun ButkusApp() { }

/*class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize Butkus asynchronously
        //TODO: This doesn't quite do what we want
        val butkusInit = GlobalScope.async { Butkus.initialize(applicationContext) }

        setContentView(R.layout.activity_main)

        //Wait for butkus to be done?
        runBlocking { butkusInit.await() }

        GlobalScope.async {
            val text = Butkus.getInstance().encode("Hello")
            println(text)
        }
    }
}*/
