package com.priorityplanner.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.priorityplanner.app.data.model.ThirdColorOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferenceManager(private val context: Context) {

    private object PreferencesKeys {
        val THIRD_COLOR = stringPreferencesKey("third_color_key")
        val THEME_MODE = stringPreferencesKey("theme_mode_key") // "SYSTEM", "LIGHT", "DARK"
        val ROLLOVER_ENABLED = booleanPreferencesKey("rollover_enabled_key")
    }

    val thirdColorOption: Flow<ThirdColorOption> = context.dataStore.data.map { preferences ->
        val colorId = preferences[PreferencesKeys.THIRD_COLOR] ?: ThirdColorOption.EMERALD_GREEN.id
        ThirdColorOption.fromId(colorId)
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM"
    }

    val isRolloverPromptEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ROLLOVER_ENABLED] ?: true
    }

    suspend fun setThirdColor(option: ThirdColorOption) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THIRD_COLOR] = option.id
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun setRolloverPromptEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ROLLOVER_ENABLED] = enabled
        }
    }
}
