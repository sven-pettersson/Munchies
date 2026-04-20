# Munchies — Architecture Presentation

> Android · Kotlin Multiplatform · Jetpack Compose

---

## 1. Project Context

A food-delivery listing app. Browse restaurants, filter by category, check open/closed status and delivery time.

**Why this stack?**

| Goal | Choice | Reason |
|------|--------|--------|
| Share logic with iOS | KMP (`shared` module) | Same network + domain + cache on both platforms |
| Reactive UI | `StateFlow` + Compose | No manual UI updates, config-change safe |
| Lightweight DI | Koin | No annotation processing — works in KMP `commonMain` |
| HTTP | Ktor | Pure Kotlin, runs in `commonMain` — Retrofit needs OkHttp (Android-only) |

---

## 2. Architecture Overview

```mermaid
graph TD
    subgraph app module  Android only
        UI[Composable Screens]
        VM[ViewModels]
        NAV[Navigation]
        TH[Theme / Design Tokens]
    end

    subgraph shared module  Kotlin Multiplatform
        UC[FilterUseCase]
        REPO[MunchiesRepository]
        API[MunchiesApi]
        DOM[Domain Models]
        MAP[DTO Mappers]
        DI[Koin sharedModule]
    end

    subgraph Backend
        SRV[REST API\n/api/v1]
    end

    UI -->|collectAsState| VM
    VM -->|combine flows| REPO
    VM -->|calls| UC
    REPO -->|safeCall| API
    API -->|HTTP GET| SRV
    SRV -.->|JSON| API
    API --> MAP --> DOM --> REPO
```

### Module boundary — enforced by the build graph

```mermaid
graph LR
    subgraph shared commonMain  compiles on JVM
        A[Domain Models\npure Kotlin data classes]
        B[DTOs  kotlinx.serialization]
        C[Mappers\npure extension functions]
        D[MunchiesApi  Ktor]
        E[MunchiesRepository  StateFlow]
        F[FilterUseCase\npure logic]
        G[Result sealed class]
    end

    subgraph app  Android only
        H[Composables]
        I[ViewModels  androidx.lifecycle]
        J[UiModels]
        K[Theme  Color Spacing Shape]
        L[Navigation graph]
    end

    shared --> app
```

> If `import android.*` appears in `shared`, the JVM target fails to compile. The boundary is not a convention — it is enforced.

---

## 3. Data Flow

```mermaid
sequenceDiagram
    participant UI as Composable
    participant VM as ViewModel
    participant REPO as Repository
    participant API as MunchiesApi
    participant BE as Backend

    UI->>VM: LaunchedEffect onRefresh
    VM->>REPO: refresh()
    REPO->>API: getRestaurants()
    API->>BE: GET /api/v1/restaurants
    BE-->>API: 200 JSON
    API-->>REPO: Result.Success
    REPO->>REPO: DTO to Domain, update StateFlow

    par fetch concurrently
        REPO->>API: getFilter(id) deduplicated
        REPO->>API: getOpenStatus(restaurantId)
    end

    REPO-->>VM: flows emit new values
    VM->>VM: combine to RestaurantListUiState
    VM-->>UI: uiState emits
    UI->>UI: recompose
```

### Screen state machine

```mermaid
stateDiagram-v2
    [*] --> Loading : app start
    Loading --> Loaded : data arrives
    Loading --> Error : network failure
    Error --> Loading : Retry tapped
    Loaded --> Loading : pull-to-refresh
    Loaded --> Loaded : filter toggled - local only
```

---

## 4. Resilience — Surviving Backend Failures

### Error as a value

```mermaid
classDiagram
    class Result~T~ {
        <<sealed>>
    }
    class Success~T~ {
        +data: T
    }
    class Error {
        +message: String
        +cause: Throwable?
    }
    Result <|-- Success
    Result <|-- Error
```

One `safeCall` wrapper in `MunchiesApiImpl` catches every network exception. Nothing above it ever handles a throw.

### Cache as a safety net

```mermaid
graph LR
    START([App starts]) --> EMPTY{Cache empty?}
    EMPTY -->|Yes| FETCH[Fetch from network]
    EMPTY -->|No| SHOW[Show cached data immediately]
    FETCH --> OK{Success?}
    OK -->|Yes| UPDATE[Update cache and rerender]
    OK -->|No| ERR[Show error banner - cache untouched]
    SHOW --> BG[Background refresh]
    BG --> OK
```

The list stays visible when a refresh fails. The error banner appears on top of existing data.

### Concurrency safeguards

| Mechanism | File + line | What it prevents |
|-----------|-------------|-----------------|
| `Mutex` | `MunchiesRepositoryImpl:41` | Overlapping refreshes |
| `SupervisorJob` | `MunchiesRepositoryImpl:23` | One failed child cancelling the whole refresh |
| `toSet()` dedup | `MunchiesRepositoryImpl:62` | Duplicate filter API calls |
| `Boolean?` status | `RestaurantListViewModel:57` | Badge showing Closed before status loads |

---

## 5. API Versioning & Forward Compatibility

### Lenient JSON parsing — already in place

```kotlin
// HttpClientFactory.kt
Json {
    ignoreUnknownKeys = true   // new API fields silently ignored
    isLenient = true           // minor formatting differences accepted
}
```

### DTO defaults — recommendation

```kotlin
@Serializable
data class RestaurantDto(
    val id: String,
    val name: String,
    val rating: Double = 0.0,
    val delivery_time_minutes: Int = -1,
    val filterIds: List<String> = emptyList(),
    val image_url: String = ""
)
```

Old clients survive field removals without crashing.

### Versioned base URL — recommendation

```
/api/v1/restaurants  current clients
/api/v2/restaurants  new clients
```

### Client–server compatibility

```mermaid
graph LR
    subgraph Shipped App Versions
        A1[App v1 - API v1]
        A2[App v2 - API v1 and v2]
        A3[App v3 - API v2]
    end
    subgraph Backend
        B1[API v1 kept alive]
        B2[API v2 additive changes]
    end
    A1 --> B1
    A2 --> B1
    A2 --> B2
    A3 --> B2
```

Keep `v1` alive until the active install base drops below an acceptable threshold.

---

## 6. KMP Boundary Rules

### What is allowed in `shared/commonMain`

- Pure Kotlin data classes (domain models)
- `kotlinx.serialization` DTOs
- Pure extension-function mappers
- Ktor HTTP layer
- `StateFlow`-based repository
- Use cases (pure logic)
- Koin wiring — no Android context needed

### What must stay in `app/`

- Composable screens
- `androidx.lifecycle.ViewModel`
- UiModels (screen-specific domain transformations)
- Design tokens — Color, Spacing, Shape, Typography
- Navigation graph
- Coil image loading
- `SavedStateHandle`
- Any `android.*` import

### PR checklist for shared module changes

| Check | Pass condition |
|-------|---------------|
| No `import android.*` | Zero Android SDK imports |
| No `Context` parameter | Koin constructor injection only |
| No `ViewModel` base class | Plain class + `StateFlow` |
| No Compose imports | UI stays in `:app` |
| No `R.string` or `R.drawable` | No resource references |
| Tests pass without emulator | `./gradlew :shared:test` green |

---

## 7. Testing

All business-logic tests run on the JVM — no emulator required.

```
./gradlew :shared:test
```

```mermaid
graph TD
    subgraph commonTest - JVM - no emulator
        T1[MunchiesApiTest\nhappy path, HTTP errors, malformed JSON]
        T2[MapperTests\nDTO to Domain]
        T3[FilterUseCaseTest\ntoggle, clear, OR filter logic]
        T4[MunchiesRepositoryTest\nparallel fetch, dedup, error handling]
    end
    subgraph Planned
        T5[Room migration tests]
        T6[Compose screenshot tests]
        T7[Navigation flow tests]
    end
```

---

## 8. Trade-offs & What Is Next

| Topic | Current | Next step |
|-------|---------|-----------|
| Persistence | In-memory `StateFlow` — lost on process death | SQLDelight (KMP) or Room + write-through |
| API versioning | `ignoreUnknownKeys` only | Versioned URL + DTO defaults |
| Modularisation | Two modules `:app` and `:shared` | Feature modules `:feature:list`, `:feature:detail` |
| Pagination | Full list from one endpoint | Cursor-based pagination |
| iOS | `commonMain` ready, no iOS targets yet | Add `iosArm64` + Darwin Ktor engine |

---

*Kotlin 2.3 · Compose 2026.03 · Ktor 3.4 · Koin 4.2 · Coroutines 1.10*