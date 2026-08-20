package com.rubenguc.kuriamindlauncher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.rubenguc.kuriamindlauncher.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    private val darkThemeKey = booleanPreferencesKey("dark_theme")

    override val darkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[darkThemeKey] ?: false }

    override suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[darkThemeKey] = enabled
        }
    }
}
