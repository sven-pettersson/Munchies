

# Munchies — Build Progress

## Overview
KMP + Jetpack Compose implementation of the Umain food delivery work test.
- **Shared KMP module** handles: networking, models, caching, filter logic, error handling
- **Android app** handles: Jetpack Compose UI, design tokens, navigation

---

## Parts

### Part 1 — KMP Module Setup `[x]`
- Add `shared` KMP module to project
- Configure `settings.gradle.kts` and root `build.gradle.kts`
- Update `libs.versions.toml` with Ktor, Kotlinx Serialization, Coroutines, Koin, Coil
- Wire `:shared` into `:app` dependencies
- Add INTERNET permission to manifest

**Status:** Complete

---

### Part 2 — Network Layer & Data Models `[x]`
- DTOs matching the API responses (RestaurantDto, FilterDto, OpenStatusDto)
- Domain models (Restaurant, Filter, OpenStatus)
- Ktor `HttpClient` setup with JSON serialization + logging
- `MunchiesApi` interface + implementation for all 3 endpoints
- Error handling via sealed `Result` type
- `MunchiesApiTest` — MockEngine tests: happy path, HTTP errors, malformed JSON
- `RestaurantMapperTest` / `FilterMapperTest` — pure DTO→domain mapping tests

**Status:** Complete

---

### Part 3 — Repository & Caching `[x]`
- `MunchiesRepository` interface
- `MunchiesRepositoryImpl` — in-memory cache via `StateFlow`, `Mutex`-guarded refresh
- Restaurants, filters (resolved + deduplicated in parallel), and open statuses all cached
- `init {}` triggers load on creation; `refresh()` safe to call on foreground resume
- Koin `sharedModule` wires `HttpClient → MunchiesApi → MunchiesRepository`
- `MunchiesRepositoryTest` — fake API covers happy path, error path, deduplication, loading state

**Status:** Complete

---

### Part 4 — Filter Logic `[x]`
- `FilterUseCase` in KMP shared module
- Multi-select active filter state (`StateFlow<Set<String>>`)
- `toggleFilter(id)` — adds/removes from active set
- `clearFilters()` resets selection
- `filterRestaurants(restaurants, activeFilters)` — OR logic, empty set returns all
- Registered as Koin `single` in `sharedModule`
- `FilterUseCaseTest` — toggle, clear, OR matching, no-filter-ids edge case

**Status:** Complete

---

### Part 5 — Design Tokens & Theme `[x]`
- `MunchiesColors` — semantic color tokens (background, surface, open/closed status, chip states) for light + dark
- `MunchiesTypography` — full type scale (headline → labelSmall) via system sans-serif
- `Spacing` — xs/sm/md/lg/xl/xxl dp tokens via `CompositionLocal`
- `MunchiesRadius` — semantic shape aliases (chip pill, card 16dp, image 12dp)
- `MunchiesTheme` — wires all tokens, sets status bar color, disables dynamic color
- `MaterialTheme.munchiesColors` / `.spacing` / `.radius` extensions for clean composable access

**Status:** Complete

---

### Part 6 — Restaurant List Screen `[x]`
- `RestaurantListUiState` + `RestaurantUiModel` + `FilterUiModel` — Android UI models decoupled from domain
- `RestaurantListViewModel` — combines 5 repository flows + active filter state into single `StateFlow<UiState>`
- `FilterBar` — horizontal `LazyRow` with image + name chips, selected/unselected token colors
- `RestaurantCard` — hero image, name, rating, delivery time, open/closed badge overlay
- `RestaurantListScreen` — loading / error+retry / empty / list states
- `MunchiesApp` + Koin `appModule` for ViewModel registration
- `ic_star` + `ic_clock` vector drawables

**Status:** Complete

---

### Part 7 — Restaurant Detail Screen `[x]`
- `MunchiesNavGraph` — NavHost with list + detail routes, restaurantId as nav arg
- `RestaurantDetailUiState` + `RestaurantDetailUiModel` + `FilterTagUiModel`
- `RestaurantDetailViewModel` — reads entirely from cached repository flows, handles loading/not-found
- `RestaurantDetailScreen` — hero image with floating back + open/closed badge, name, rating, delivery time, filter tags
- Light/dark and loading previews
- Background refresh on `RestaurantListScreen` via `repeatOnLifecycle(STARTED)`
- Detail ViewModel registered in Koin appModule with `SavedStateHandle`

**Status:** Complete

---

### Part 8 — Polish & README `[ ]`
- Loading skeletons / shimmer placeholders
- Proper error snackbar / retry UI
- Open/closed status with color indicator
- README finalization with architecture description, setup instructions, screenshots section

**Status:** Not started

---

## Commit History
| Part | Commit Message |
|------|---------------|
| — | — |