package com.rubenguc.kuriamindlauncher.presentation.drawer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rubenguc.kuriamindlauncher.presentation.components.AppCell
import com.rubenguc.kuriamindlauncher.presentation.model.AppUiItem
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AppDrawer(
    apps: List<AppUiItem>,
    onAppClick: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    val dragOffsetY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        dragOffsetY.snapTo(0f)
    }

    val filtered by remember(apps, query) {
        derivedStateOf {
            if (query.isBlank()) apps
            else apps.filter { it.label.contains(query.trim(), ignoreCase = true) }
        }
    }

    val isAtTop by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0
        }
    }

    val nestedScrollConnection = remember(isAtTop) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isAtTop && available.y > 0 && dragOffsetY.value > 0f) {
                    val newOffset = (dragOffsetY.value + available.y).coerceAtLeast(0f)
                    scope.launch { dragOffsetY.snapTo(newOffset) }
                    return available
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (isAtTop && available.y > 0 && source == NestedScrollSource.Drag) {
                    val newOffset = (dragOffsetY.value + available.y).coerceAtLeast(0f)
                    scope.launch { dragOffsetY.snapTo(newOffset) }
                    return available
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                if (dragOffsetY.value > 120f) {
                    onClose()
                } else {
                    scope.launch { dragOffsetY.animateTo(0f, tween(150)) }
                }
                return super.onPreFling(available)
            }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .offset { IntOffset(0, dragOffsetY.value.roundToInt()) },
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.15f)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ) {
                    Box(modifier = Modifier.padding(2.dp))
                }
            }

            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(minSize = 78.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = filtered,
                        key = { it.packageName }
                    ) { item ->
                        AppCell(
                            item = item,
                            onClick = { onAppClick(item.packageName) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                placeholder = { Text("Search apps") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}