package com.rubenguc.kuriamindlauncher.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rubenguc.kuriamindlauncher.presentation.components.AppCell
import com.rubenguc.kuriamindlauncher.presentation.model.AppUiItem

@Composable
fun HomeScreen(
    apps: List<AppUiItem>,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val homeApps = apps.take(12)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 84.dp),
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        userScrollEnabled = false,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(homeApps, key = { it.packageName }) { item ->
            AppCell(
                item = item,
                onClick = { onAppClick(item.packageName) }
            )
        }
    }
}
