package com.hashapps.cadenas

import android.app.Application
import com.hashapps.cadenas.data.AppContainer
import com.hashapps.cadenas.data.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CadenasApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(applicationScope, this)
    }
}