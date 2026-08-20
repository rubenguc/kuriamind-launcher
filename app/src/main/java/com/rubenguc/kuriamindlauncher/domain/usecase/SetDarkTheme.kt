package com.rubenguc.kuriamindlauncher.domain.usecase

import com.rubenguc.kuriamindlauncher.domain.repository.SettingsRepository

class SetDarkTheme(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDarkTheme(enabled)
}
