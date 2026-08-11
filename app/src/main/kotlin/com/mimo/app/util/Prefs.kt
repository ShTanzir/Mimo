package com.mimo.app.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mimo_settings")

/**
 * App-wide settings: theme, PIN lock, onboarding completion, master toggle.
 */
class Prefs(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val MASTER_ENABLED = booleanPreferencesKey("master_enabled")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val PIN_ENABLED = booleanPreferencesKey("pin_enabled")
    }

    val onboardingDone: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    val masterEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.MASTER_ENABLED] ?: true }

    val darkTheme: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.DARK_THEME] ?: false }

    val pinEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.PIN_ENABLED] ?: false }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun setMasterEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MASTER_ENABLED] = enabled }
    }

    suspend fun setDarkTheme(dark: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = dark }
    }

    suspend fun setPin(pin: String?) {
        context.dataStore.edit {
            if (pin.isNullOrBlank()) {
                it[Keys.PIN_ENABLED] = false
                it.remove(Keys.PIN_HASH)
            } else {
                it[Keys.PIN_ENABLED] = true
                it[Keys.PIN_HASH] = pin.hashCode().toString()
            }
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val stored = context.dataStore.data.map { it[Keys.PIN_HASH] }.first()
        return stored == pin.hashCode().toString()
    }
}
