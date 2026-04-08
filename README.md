# Munchies

A restaurant discovery app built for the [Umain Mobile Work Test](https://github.com/eidra-umain/work-test-mobile).

## Architecture

```
Munchies/
├── app/          # Android frontend — Jetpack Compose UI, design tokens, navigation
└── shared/       # Kotlin Multiplatform — networking, models, caching, filter logic
```

### Shared KMP module responsibilities
- Ktor HTTP client + all API calls
- DTO → Domain model mapping
- In-memory caching with StateFlow (populated on start, refreshed in background)
- Filter use case (multi-select, pure filtering logic)
- Sealed error types and result wrappers

### Android app responsibilities
- Jetpack Compose screens (list + detail)
- Design token system (colors, typography, spacing, shapes)
- ViewModels consuming KMP StateFlows
- Compose Navigation

## API
Base URL: `https://food-delivery.umain.io/api/v1/`

| Endpoint | Description |
|----------|-------------|
| `GET /restaurants` | All restaurants |
| `GET /filter/{filterId}` | Filter metadata (name, image) |
| `GET /open/{restaurantId}` | Open/closed status |

## Setup
1. Clone the repo
2. Open in Android Studio
3. Run on emulator or device (minSdk 26)

---

## Progress
See [PARTS.md](PARTS.md) for the full build checklist.

### Part 1 — KMP Module Setup ✓
Added the `shared` Kotlin Multiplatform module alongside the Android `app` module. Configured Ktor (networking), Kotlinx Serialization, Coroutines, and Koin (DI) in the version catalog. The `app` module depends on `shared`, which will contain all business logic.