package com.hashapps.butkusapp

import android.app.Application
import com.hashapps.butkusapp.data.AppContainer
import com.hashapps.butkusapp.data.AppDataContainer

class ButkusApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}