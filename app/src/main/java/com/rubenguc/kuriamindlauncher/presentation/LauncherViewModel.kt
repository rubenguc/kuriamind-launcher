package com.rubenguc.kuriamindlauncher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rubenguc.kuriamindlauncher.data.AppIconLoader
import com.rubenguc.kuriamindlauncher.domain.usecase.GetInstalledApps
import com.rubenguc.kuriamindlauncher.domain.usecase.LaunchApp
import com.rubenguc.kuriamindlauncher.presentation.model.AppUiItem
import com.rubenguc.kuriamindlauncher.presentation.theme.ThemePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Exposes the app list with icons PRE-LOADED off the main thread (always
 * available without ever touching PackageManager during a touch/gesture).
 */
class LauncherViewModel(
    private val getInstalledApps: GetInstalledApps,
    private val launchApp: LaunchApp,
    private val iconLoader: AppIconLoader,
) : ViewModel() {

    private val _apps = MutableStateFlow<List<AppUiItem>>(emptyList())
    val apps: StateFlow<List<AppUiItem>> = _apps.asStateFlow()

    private val _themePreference = MutableStateFlow(ThemePreference.SYSTEM)
    val themePreference: StateFlow<ThemePreference> = _themePreference.asStateFlow()

    fun setThemePreference(preference: ThemePreference) {
        _themePreference.value = preference
    }

    init {
        viewModelScope.launch {
            val installed = getInstalledApps()
            // Resolve every icon here (Default dispatcher), once, so the drawer
            // stays on the main thread during gestures.
            _apps.value = withContext(Dispatchers.Default) {
                installed.map { app ->
                    AppUiItem(
                        packageName = app.packageName,
                        label = app.label,
                        icon = iconLoader.load(app.packageName),
                    )
                }
            }
        }
    }

    fun launch(packageName: String) {
        launchApp(packageName)
    }

    companion object {
        fun Factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LauncherViewModel(
                    getInstalledApps = GetInstalledApps(container.appsRepository),
                    launchApp = LaunchApp(container.appsRepository),
                    iconLoader = container.iconLoader,
                )
            }
        }
    }
}
