package com.hashapps.cadenas

import android.app.Application
import com.hashapps.cadenas.data.AppContainer
import com.hashapps.cadenas.data.AppDataContainer

/**
 * The _true_ entrypoint of the Cadenas application, constructed before
 * [MainActivity].
 *
 * The [Application] houses an [AppContainer], holding references to all of the
 * data sources driving the UI. This setup ensures that data is available from
 * application startup.
 */
class CadenasApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}