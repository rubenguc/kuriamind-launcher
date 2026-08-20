package com.rubenguc.kuriamindlauncher.presentation

import android.content.Context
import com.rubenguc.kuriamindlauncher.data.AppIconLoader
import com.rubenguc.kuriamindlauncher.data.AppsRepositoryImpl
import com.rubenguc.kuriamindlauncher.data.SettingsRepositoryImpl
import com.rubenguc.kuriamindlauncher.domain.repository.AppsRepository
import com.rubenguc.kuriamindlauncher.domain.repository.SettingsRepository

/**
 * Manual dependency injection container. Wires data-layer implementations into
 * the domain interfaces used by the presentation layer. No Hilt for v1.
 */
class AppContainer(context: Context) {
    val appsRepository: AppsRepository = AppsRepositoryImpl(context)
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(context.applicationContext)
    val iconLoader: AppIconLoader = AppIconLoader(context.applicationContext)
}
