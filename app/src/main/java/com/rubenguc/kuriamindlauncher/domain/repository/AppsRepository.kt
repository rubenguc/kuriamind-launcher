package com.rubenguc.kuriamindlauncher.domain.repository

import com.rubenguc.kuriamindlauncher.domain.model.InstalledApp

interface AppsRepository {
    suspend fun getInstalledApps(): List<InstalledApp>
    fun launchApp(packageName: String): Boolean
}
