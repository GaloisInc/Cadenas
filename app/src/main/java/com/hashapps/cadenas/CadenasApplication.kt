package com.hashapps.cadenas

import android.app.Application
import com.hashapps.cadenas.data.AppContainer
import com.hashapps.cadenas.data.AppDataContainer

class CadenasApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}