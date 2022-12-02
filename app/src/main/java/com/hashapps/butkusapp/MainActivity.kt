package com.hashapps.butkusapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
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
}