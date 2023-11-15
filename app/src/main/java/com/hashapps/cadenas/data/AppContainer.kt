package com.hashapps.cadenas.data

import android.content.Context
import androidx.work.WorkManager
import com.hashapps.cadenas.data.channels.ChannelRepository

/**
 * Interface for containers providing data to Cadenas.
 *
 * Implementors must provide repositories for messaging channels, the app's
 * settings (e.g. which messaging channel is active), and the models one may
 * use to encode messages.
 *
 * While there currently isn't a huge benefit to having this interface, it
 * does provide a mechanism by which we may implement additional containers or
 * expose friendlier APIs than the repositories themselves.
 *
 * @property[channelRepository] Repository of Cadenas channels
 * @property[modelRepository] Repository of available language models
 */
interface AppContainer {
    val channelRepository: ChannelRepository
    val modelRepository: ModelRepository
}

/**
 * Core implementor of [AppContainer] for Cadenas.
 *
 * Using the application [Context], initializes all of the Cadenas
 * repositories. Despite being a regular class, only one instance is created
 * during the lifetime of the app, in [com.hashapps.cadenas.CadenasApplication].
 */
class AppDataContainer(
    private val context: Context
) : AppContainer {
    override val channelRepository by lazy {
        ChannelRepository(
            contentResolver = context.contentResolver,
            modelsDir = context.filesDir.resolve("models").also { it.mkdir() },
            channelDao = CadenasDatabase.getDatabase(context).channelDao(),
        )
    }

    override val modelRepository by lazy {
        ModelRepository(
            modelsDir = context.filesDir.resolve("models").also { it.mkdir() },
            workManager = WorkManager.getInstance(context),
        )
    }
}