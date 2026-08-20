package com.rubenguc.kuriamindlauncher.domain.usecase

import com.rubenguc.kuriamindlauncher.domain.model.InstalledApp
import com.rubenguc.kuriamindlauncher.domain.repository.AppsRepository

class GetInstalledApps(
    private val repository: AppsRepository
) {
    suspend operator fun invoke(): List<InstalledApp> = repository.getInstalledApps()
}
