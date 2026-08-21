package com.rubenguc.kuriamindlauncher.presentation.theme

/**
 * User-facing theme mode. [SYSTEM] follows the device's dark/light setting,
 * while [LIGHT] and [DARK] force a specific appearance.
 */
enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK;

    /** Resolves this preference to a concrete dark-mode boolean. */
    fun isDark(systemInDark: Boolean): Boolean = when (this) {
        SYSTEM -> systemInDark
        LIGHT -> false
        DARK -> true
    }
}
