# AGENTS.md

## Project

Single-module Android app — Kotlin + Jetpack Compose (Material3). A minimalist
home-screen launcher (system HOME app). One activity (`MainActivity`).
Package/namespace: `com.rubenguc.kuriamindlauncher`.

## Design (Material 3)

Material 3 is the design system. Build all UI with `androidx.compose.material3`
(`Scaffold`, `Surface`, `TopAppBar`, `NavigationBar`, `LazyVerticalGrid`, `SearchBar`,
`Switch`, etc.) and the M3 theming model (`ColorScheme`, `Typography`, `Shapes`).
Do NOT pull in `androidx.compose.material` (Material 2) or hand-roll styling that
bypasses the theme. Theme lives in `presentation/theme/`, applied via
`KuriaMindLauncherTheme`.

## Architecture (MVVM + Clean Architecture, Compose)

Three layers; dependencies point inward to `domain/` (pure Kotlin, no Android imports):

```
presentation ──▶ domain ◀── data
```

- `presentation/` — MVVM. Compose screens + components (`home/`, `drawer/`,
  `settings/`, shared widgets, `theme/`). Each feature has a ViewModel exposing UI
  state as `StateFlow` (`asStateFlow()`) and running work in `viewModelScope`.
- `domain/` — pure Kotlin: entities (`InstalledApp`, `HomeItem`), repository
  interfaces (`AppsRepository`, `HomeLayoutRepository`, `SettingsRepository`), and thin
  use cases (`GetInstalledApps`, `LaunchApp`, `SaveHomeLayout`, `Get/SetDarkTheme`).
  No Android/Compose imports.
- `data/` — implements `domain` interfaces: `AppsRepositoryImpl` → PackageManager,
  `HomeLayoutRepositoryImpl` / `SettingsRepositoryImpl` → DataStore.

Rule: `presentation` and `data` depend on `domain`, never the reverse. ViewModels use
only `domain` interfaces/use cases. Manual DI (an `AppContainer`) wires `data` impls
into ViewModels — no Hilt for v1.

UX notes:
- App drawer is a lightweight full-screen swipe-up list (NOT a ModalBottomSheet).
- Home is a grid of cells filled by drag-and-drop from the drawer (long-press lifts an
  app; the drawer slides away revealing home). Home placement persists via
  `HomeLayoutRepository`.

## Launcher gotchas (Android-specific)

- `MainActivity` is the system launcher: `ACTION_MAIN` + `CATEGORY_HOME` +
  `CATEGORY_DEFAULT`, with `launchMode="singleTask"`, `stateNotNeeded="true"`,
  `clearTaskOnLaunch="true"`, `excludeFromRecents="true"`.
- targetSdk 37 hides other apps by default. The manifest MUST declare a `<queries>`
  element (MAIN/LAUNCHER intent) or `QUERY_ALL_PACKAGES`, else the app drawer is empty.
- Load app icons/labels with `Dispatchers.Default` (off the main thread).

## Toolchain (very new — read before touching build files)

- Gradle 9.5.0 (wrapper), AGP 9.3.1, Kotlin 2.2.10.
- Compose compiler is the Kotlin plugin `org.jetbrains.kotlin.plugin.compose`
  (applied via `libs.plugins.kotlin.compose`), NOT the old `composeOptions` block.
- AGP 9 DSL: `compileSdk { version = release(37) }` in `app/build.gradle.kts` —
  the old integer `compileSdk = 34` form is gone.
- JDK 25 daemon, auto-resolved by `org.gradle.toolchains.foojay-resolver-convention`
  in `settings.gradle.kts` (Gradle downloads the toolchain on first run; no manual
  JDK install required). Java source/target compatibility stays at 11 via
  `compileOptions` in `app/build.gradle.kts`.

## Build & test

- Build debug APK: `./gradlew assembleDebug`
- Unit tests (JVM): `./gradlew test` (or `./gradlew testDebugUnitTest`)
- One unit test:
  `./gradlew testDebugUnitTest --tests "com.rubenguc.kuriamindlauncher.ExampleUnitTest"`
- Instrumented tests (require an emulator/device): `./gradlew connectedAndroidTest`
- Lint: `./gradlew lint`

## Conventions & gotchas

- All dependencies live in `gradle/libs.versions.toml` (version catalog). Add them
  there and reference via `libs.…` aliases; never hardcode versions in build scripts.
- `repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)` in
  `settings.gradle.kts` means repositories can ONLY be declared in
  `settings.gradle.kts`. A `repositories {}` block in a module fails the build.
- `release { optimization { enable = false } }` in `app/build.gradle.kts` disables
  R8/minification for release builds. Don't re-enable without testing a release build.
- `org.gradle.configuration-cache=true` is set in `gradle.properties`.
- `local.properties` holds `sdk.dir` and is gitignored (machine-specific). Never
  commit it or read it for portable config.
- `.atl/` is gitignored OpenCode tooling (skill-registry cache). Keep it ignored.

## Compose performance conventions

- Annotate models (`InstalledApp`) with `@Immutable` so composables are skippable.
- Filter the drawer with `derivedStateOf`, not in composition — a search keystroke
  must not recompose the whole grid.
- Stable `key = { it.packageName }` in `LazyVerticalGrid`.
- Collect flows with `collectAsStateWithLifecycle()`, not plain `collectAsState()`.

## Planned dependencies (add to `libs.versions.toml`)

Not yet present — required by the launcher plan:
- `androidx.lifecycle:lifecycle-viewmodel-compose` (Compose ViewModel)
- `androidx.lifecycle:lifecycle-runtime-compose` (`collectAsStateWithLifecycle`)
- `androidx.datastore:datastore-preferences` (settings persistence)
