package com.rubenguc.kuriamindlauncher.domain.model

import androidx.compose.runtime.Immutable

/**
 * Pure domain model describing a launchable app installed on the device.
 * Kept free of Android types (no Drawable/icon) so the domain stays pure Kotlin.
 */
@Immutable
data class InstalledApp(
    val packageName: String,
    val label: String
)
