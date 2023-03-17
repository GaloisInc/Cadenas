package com.hashapps.butkusapp

import android.app.Application
import com.hashapps.butkusapp.data.AppContainer
import com.hashapps.butkusapp.data.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ButkusApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(applicationScope, this)
    }
}