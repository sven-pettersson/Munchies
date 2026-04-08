# Munchies

This is a model test app built for the [Umain Mobile Work Test](https://github.com/eidra-umain/work-test-mobile).

I have used Claude quiet a lot since that will reflect the way I would develop an app from scratch under normal circumstances. I like that the distance between ideas and production becomes a minimum. It has been created from a set of steps and requirements I have set up. The Among those where:

- Dividing the code into a shared KMP part and the android app part. This to make it possible to use KMP as a mutual code divided by the iOS and Android environments
- Create a repository fetching the data from backend and let it live in the KMP environment. This will lessen the coupling between the view and the backend to a minimum. 
- Data is cached in-memory for the lifetime of the app session, reducing repeated network calls. Persistent on-device storage (SQLDelight/DataStore) is a planned next step.
- I have added Jetpack Compose Objects to handle the parts of the views. This to make them reusable and stable.
- I have implemented the Theme catalog to handle values coming from figma design

The parts that I have not yet implemented but should be considered are 

- Setting upp product flavors for the app to make release stable and make the distinction between Prod, Staging and Testing clear. The different types should be set by a Git Branch Strategy and done while deploying. But at the same time making the different types from inside Android Studio can be vital when handling support.
- In conjunction with Backend I would like to introduce feature flags to the app. This to make it possible to add new features in a nice and secure way.
- Setup a deployment strategy including Alpha Testing, Beta Testing and Production.
- Setup a way to fast track changed data from backend. If for example opening times changes for a restaurant it should be reflected asap.
- Maybe add a room database or a database that resides in Firebase to hold data fetched from backend. This to make sure that data is kept between sessions. Used this previous, but then we had quite a lot of data to fetch. 


## Architecture

```
Munchies/
├── app/      # Android — Jetpack Compose UI, design tokens, navigation
└── shared/   # Kotlin Multiplatform — networking, models, caching, filter logic
```

The project follows a strict separation: all business logic lives in the `shared` KMP module. The Android `app` is purely a frontend — it consumes `StateFlow`s exposed by the shared layer and renders them.

### Shared KMP module

| Layer | Responsibility |
|-------|---------------|
| `network/dto` | Serializable DTOs matching the raw API JSON |
| `domain/model` | Clean domain models — no serialization concerns |
| `domain/mapper` | DTO → domain mapping (pure extension functions) |
| `domain/usecase` | `FilterUseCase` — multi-select state + OR filter logic |
| `network` | Ktor `HttpClient`, `MunchiesApi` interface + implementation |
| `repository` | `MunchiesRepository` — in-memory cache via `StateFlow`, parallel fetching, background refresh |
| `di` | Koin `sharedModule` wiring |
| `util` | Sealed `Result<T>` with `map`, `onSuccess`, `onError` helpers |

### Android app module

| Layer | Responsibility |
|-------|---------------|
| `ui/theme` | Design token system — `MunchiesColors`, `Spacing`, `MunchiesRadius`, typography, `MaterialTheme` extensions |
| `ui/screen/restaurantlist` | List screen, `RestaurantListViewModel`, filter bar, restaurant card, skeleton loading |
| `ui/screen/restaurantdetail` | Detail screen, `RestaurantDetailViewModel`, filter tags, open/closed status |
| `ui/components` | Shared shimmer brush utility |
| `navigation` | `MunchiesNavGraph` — `NavHost` with list + detail routes |

---

## Features

- Restaurant list with hero images, ratings, delivery times and open/closed status
- Horizontal filter bar with category images — multi-select, OR logic
- Detail screen with full-width hero, filter tags and prominent open/closed badge
- Shimmer skeleton on initial load, pull-to-refresh on subsequent loads
- Background data refresh whenever the app returns to the foreground
- Full light and dark theme support via design token system
- Error state with retry action, empty state for filtered results

---

## Tech Stack

| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin Multiplatform | 2.3.20 | Shared business logic |
| Ktor | 3.4.2 | HTTP client |
| Kotlinx Serialization | 1.10.0 | JSON parsing |
| Kotlinx Coroutines | 1.10.2 | Async + StateFlow |
| Koin | 4.2.0 | Dependency injection |
| Jetpack Compose BOM | 2026.03.01 | UI framework |
| Compose Navigation | 2.9.7 | Screen navigation |
| Coil | 2.7.0 | Image loading |
| AGP | 9.1.0 | Android Gradle Plugin |

---

## API

Base URL: `https://food-delivery.umain.io/api/v1/`

| Endpoint | Description |
|----------|-------------|
| `GET /restaurants` | All restaurants with filter IDs |
| `GET /filter/{filterId}` | Filter metadata (name, image URL) |
| `GET /open/{restaurantId}` | Current open/closed status |

---

## Setup

1. Clone the repo
2. Open in Android Studio (Meerkat or later recommended)
3. Let Gradle sync
4. Run on emulator or device — minSdk 26

---

## Build Progress

See [PARTS.md](PARTS.md) for the full step-by-step checklist.

### Part 1 — KMP Module Setup ✓
Added the `shared` Kotlin Multiplatform module (Android + JVM targets) using the AGP 9 `com.android.kotlin.multiplatform.library` plugin. Configured Ktor, Kotlinx Serialization, Coroutines, Koin and Coil in the version catalog. The `app` module depends on `shared` for all business logic.

### Part 2 — Network Layer & Data Models ✓
Implemented DTOs, domain models and DTO→domain mappers for restaurants, filters and open status. Set up Ktor `HttpClient` with JSON content negotiation and logging. `MunchiesApi` interface + `MunchiesApiImpl` wraps every call in a `safeCall` block returning `Result<T>`. Tested with `MockEngine` — happy path, HTTP errors and malformed JSON.

### Part 3 — Repository & Caching ✓
`MunchiesRepositoryImpl` caches restaurants, filters and open statuses in `StateFlow`s. On creation it triggers an initial load; `refresh()` is guarded by a `Mutex` to prevent concurrent fetches. Filters are resolved and deduplicated in parallel using `coroutineScope` + `async`. Koin `sharedModule` wires all dependencies. Tested with a fake API covering happy path, error path and deduplication.

### Part 4 — Filter Logic ✓
`FilterUseCase` holds a `StateFlow<Set<String>>` of active filter IDs. `toggleFilter` adds or removes a filter, `clearFilters` resets the selection. `filterRestaurants` applies OR logic — a restaurant matches if it has any of the active filter IDs. Empty selection returns the full list.

### Part 5 — Design Tokens & Theme ✓
`MunchiesColors` semantic token class covers background, surface, open/closed status, star rating and chip states — provided via `CompositionLocal` for light and dark. Typography scale from `headlineLarge` down to `labelSmall` using system sans-serif. `Spacing` (xs–xxl) and `MunchiesRadius` (chip pill, card, image) tokens exposed via `MaterialTheme.spacing` / `.radius` / `.munchiesColors` extensions.

### Part 6 — Restaurant List Screen ✓
`RestaurantListViewModel` combines five repository flows and active filter state into a single `UiState`. `FilterBar` renders a horizontal `LazyRow` of chips with Coil-loaded images. `RestaurantCard` shows hero image, name, rating, delivery time and an open/closed badge. Compose Previews provided for all states — loaded (light + dark), skeleton, error, empty.

### Part 7 — Restaurant Detail Screen ✓
`MunchiesNavGraph` wires list and detail routes with `restaurantId` as a navigation argument. `RestaurantDetailViewModel` resolves all data from the cached repository — no additional network calls on navigation. Detail screen shows a full-width hero image with a floating back button and open/closed badge, followed by name, rating, delivery time and resolved filter tags. Background refresh triggered via `repeatOnLifecycle(STARTED)` on the list screen.

### Part 8 — Polish ✓
Shimmer skeleton (animated `Brush.linearGradient`) replaces the spinner during initial load. `PullToRefreshBox` wraps the list for manual refresh once data is visible. README completed with architecture overview, feature list, tech stack table and setup instructions.
