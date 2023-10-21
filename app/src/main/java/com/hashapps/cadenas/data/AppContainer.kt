package com.hashapps.cadenas.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

private const val CADENAS_SETTINGS_NAME = "cadenas_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CADENAS_SETTINGS_NAME
)

/**
 * Interface for containers providing data to Cadenas.
 *
 * Implementors must provide repositories for messaging profiles, the app's
 * settings (e.g. which messaging profile is active), and the models one may
 * use to encode messages.
 *
 * While there currently isn't a huge benefit to having this interface, it
 * does provide a mechanism by which we may implement additional containers or
 * expose friendlier APIs than the repositories themselves.
 *
 * @property[profileRepository] Repository of Cadenas profiles
 * @property[settingsRepository] Repository of application settings
 * @property[modelRepository] Repository of available language models
 */
interface AppContainer {
    val profileRepository: ProfileRepository
    val settingsRepository: SettingsRepository
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
    override val profileRepository by lazy {
        ProfileRepository(
            contentResolver = context.contentResolver,
            profileDao = ProfileDatabase.getDatabase(context).profileDao(),
        )
    }

    override val settingsRepository by lazy {
        SettingsRepository(
            dataStore = context.dataStore,
            modelsDir = context.filesDir.resolve("models").also { it.mkdir() },
            profileDao = ProfileDatabase.getDatabase(context).profileDao(),
            externalScope = CoroutineScope(SupervisorJob()),
        )
    }

    override val modelRepository by lazy {
        ModelRepository(
            modelsDir = context.filesDir.resolve("models").also { it.mkdir() },
            workManager = WorkManager.getInstance(context),
        )
    }
}