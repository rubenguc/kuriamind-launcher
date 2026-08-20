package com.rubenguc.kuriamindlauncher.domain.usecase

import com.rubenguc.kuriamindlauncher.domain.repository.AppsRepository

class LaunchApp(
    private val repository: AppsRepository
) {
    operator fun invoke(packageName: String): Boolean = repository.launchApp(packageName)
}
