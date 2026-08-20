package com.rubenguc.kuriamindlauncher.domain.usecase

import com.rubenguc.kuriamindlauncher.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetDarkTheme(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.darkTheme
}
