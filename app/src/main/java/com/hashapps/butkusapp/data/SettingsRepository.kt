package com.hashapps.butkusapp.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hashapps.butkusapp.ui.models.SavedSettings
import com.hashapps.butkusapp.ui.models.SettingsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.crypto.KeyGenerator

data class Settings(
    val secretKey: String,
    val seedText: String,
)

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val SECRET_KEY = stringPreferencesKey("secret_key")
        val SEED_TEXT = stringPreferencesKey("seed_text")
        val SELECTED_MODEL = stringPreferencesKey("selected_model")

        const val TAG = "SettingsRepo"

        val KEYGEN: KeyGenerator = KeyGenerator.getInstance("AES")
    }

    val settings: Flow<SavedSettings> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading settings.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            SavedSettings(
                secretKey = preferences[SECRET_KEY] ?: "",
                seedText = preferences[SEED_TEXT] ?: "",
                selectedModel = preferences[SELECTED_MODEL] ?: "",
            )
        }

    suspend fun saveSettings(savedSettings: SavedSettings) {
        dataStore.edit {
            it[SECRET_KEY] = savedSettings.secretKey
            it[SEED_TEXT] = savedSettings.seedText
            it[SELECTED_MODEL] = savedSettings.selectedModel
        }
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }

    fun genKey(): String {
        return KEYGEN.generateKey().encoded.toHex()
    }
}