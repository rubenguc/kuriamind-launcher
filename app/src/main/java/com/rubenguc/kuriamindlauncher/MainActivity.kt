package com.rubenguc.kuriamindlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rubenguc.kuriamindlauncher.presentation.AppContainer
import com.rubenguc.kuriamindlauncher.presentation.LauncherViewModel
import com.rubenguc.kuriamindlauncher.presentation.drawer.AppDrawer
import com.rubenguc.kuriamindlauncher.presentation.home.HomeScreen
import com.rubenguc.kuriamindlauncher.presentation.theme.KuriaMindLauncherTheme
import com.rubenguc.kuriamindlauncher.presentation.theme.ThemePreference

class MainActivity : ComponentActivity() {
    lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        container = AppContainer(applicationContext)

        setContent {
            val launcherViewModel: LauncherViewModel = viewModel(
                factory = LauncherViewModel.Factory(container)
            )
            val systemInDark = isSystemInDarkTheme()
            val themePreference by launcherViewModel.themePreference.collectAsStateWithLifecycle()
            val darkTheme = themePreference.isDark(systemInDark)

            KuriaMindLauncherTheme(darkTheme = darkTheme) {
                val apps by launcherViewModel.apps.collectAsStateWithLifecycle()

                var isDrawerOpen by remember { mutableStateOf(false) }

                val screenHeight = with(LocalDensity.current) {
                    LocalWindowInfo.current.containerSize.height.toDp()
                }

                val drawerOffsetY by animateDpAsState(
                    targetValue = if (isDrawerOpen) 0.dp else screenHeight,
                    animationSpec = tween(durationMillis = 250),
                    label = "drawerSlide"
                )

                BackHandler {
                    if (isDrawerOpen) {
                        isDrawerOpen = false
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        HomeScreen(
                            apps = apps,
                            onAppClick = launcherViewModel::launch,
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures { _, dragAmount ->
                                        if (dragAmount < -20f) {
                                            isDrawerOpen = true
                                        }
                                    }
                                }
                        )

                        if (isDrawerOpen) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(y = drawerOffsetY)
                            ) {
                                AppDrawer(
                                    apps = apps,
                                    onAppClick = launcherViewModel::launch,
                                    onClose = { isDrawerOpen = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}