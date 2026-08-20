package com.rubenguc.kuriamindlauncher.domain.model

import androidx.compose.runtime.Immutable

/**
 * A single cell on the home screen, bound to an installed app.
 * Used later for home layout persistence. Icons are resolved at the presentation layer.
 */
@Immutable
data class HomeItem(
    val packageName: String,
    val cellIndex: Int
)
