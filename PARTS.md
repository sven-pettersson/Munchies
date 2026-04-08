
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

### Part 3 — Repository & Caching `[ ]`
- `MunchiesRepository` interface
- Implementation with in-memory cache using `StateFlow`
- Cache restaurants + filters on first load
- Background refresh (triggered on app resume via lifecycle callbacks exposed from KMP)
- Expose `Flow<List<Restaurant>>` and `Flow<List<Filter>>`

**Status:** Not started

---

### Part 4 — Filter Logic `[ ]`
- `FilterUseCase` in KMP shared module
- Multi-select active filter state (`Set<String>`)
- `filterRestaurants(restaurants, activeFilters)` pure function
- Exposed as `StateFlow` for the UI to collect

**Status:** Not started

---

### Part 5 — Design Tokens & Theme `[ ]`
- Color tokens (background, surface, primary, text, tag colors)
- Typography tokens (headline, body, label sizes/weights)
- Spacing & shape tokens
- Update `Theme.kt` to wire everything together
- Dark mode support

**Status:** Not started

---

### Part 6 — Restaurant List Screen `[ ]`
- `RestaurantListViewModel` collects from KMP repository + filter use case
- `FilterBar` composable (horizontal scroll, chip per filter with image + name)
- `RestaurantCard` composable (image, name, rating, delivery time, open/closed badge)
- `RestaurantListScreen` composable wiring it all together
- Loading + error + empty states

**Status:** Not started

---

### Part 7 — Restaurant Detail Screen `[ ]`
- `RestaurantDetailViewModel` (fetches open status, resolves filter names)
- `RestaurantDetailScreen` composable (hero image, info, filter tags, open/closed)
- Navigation setup (NavHost, routes, back stack)
- Wire detail into `MainActivity`

**Status:** Not started

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