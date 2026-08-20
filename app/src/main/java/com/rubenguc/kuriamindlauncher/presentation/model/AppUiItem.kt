package com.rubenguc.kuriamindlauncher.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Presentation-layer app model: the pure domain `InstalledApp` plus a
 * pre-loaded icon. Icons are resolved ONCE in the ViewModel (off the main
 * thread), never inside composition, so entering the drawer never triggers a
 * burst of PackageManager work on the UI thread.
 */
@Immutable
data class AppUiItem(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap?,
)
