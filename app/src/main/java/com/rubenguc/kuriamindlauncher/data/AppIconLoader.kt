package com.rubenguc.kuriamindlauncher.data

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import java.util.concurrent.ConcurrentHashMap

/**
 * Loads an app icon as a pre-rasterized [ImageBitmap], cached per package so
 * PackageManager is queried at most once per app. Call from off the main thread
 * (the ViewModel loads icons on Dispatchers.Default). Thread-safe.
 */
class AppIconLoader(private val context: Context) {

    private val cache = ConcurrentHashMap<String, ImageBitmap>()

    fun load(packageName: String): ImageBitmap? {
        cache[packageName]?.let { return it }
        val icon = runCatching {
            context.packageManager
                .getApplicationIcon(packageName)
                .toBitmap()
                .asImageBitmap()
        }.getOrNull() ?: return null
        cache[packageName] = icon
        return icon
    }
}
