package com.hashapps.cadenas

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        container = AppDataContainer(this)
    }

    companion object {
        const val CHANNEL_ID: String = "model_management"
    }
}