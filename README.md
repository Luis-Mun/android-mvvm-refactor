# Android MVVM Refactor

An inventory-tracking Android app (Java) refactored from a single-Activity, raw-SQLite design into a layered **MVVM** architecture with **Room**, **LiveData**, and a repository layer.

The original app (`FirstApp_LuisMunoz`, built for SNHU CS-360) did everything inside two Activities: SQL calls, validation, SMS permission handling and list refresh. This repo shows the same functionality reorganized so each layer has one job and the business logic is unit-testable without a device.

## What the app does

- Register / log in (passwords are stored as salted PBKDF2 hashes)
- Add, increment/decrement, and delete inventory items shown in a 2-column grid
- Send an SMS alert when an item's quantity reaches 0 (asks for `SEND_SMS` permission at that moment)

## Architecture

```
View (Activity, XML, Adapter)  ->  ViewModel  ->  Repository (interface)  ->  Room DAO  ->  SQLite
```

| Layer | Package | Responsibility |
| --- | --- | --- |
| View | `ui.login`, `ui.main` | Render state, forward taps, own Android-only work (Toasts, SMS permission) |
| ViewModel | `MainViewModel`, `LoginViewModel` | Validation, app logic, observable state; survives rotation; no `Context` |
| Repository | `data.repository` | Single source of truth; runs DB work off the main thread |
| Data | `data.dao`, `data.database` | Room entities, DAOs, database |
| Model | `model` | `Item`, `User` |
| Utils | `utils` | `Validator`, `PasswordHasher`, `Event`, `SmsNotifier`, `AppConfig` |

See [docs/architecture.md](docs/architecture.md) for a diagram and [docs/refactor-notes.md](docs/refactor-notes.md) for a before/after of what moved where.

## Tech stack

Java 11 · AndroidX AppCompat · RecyclerView (`ListAdapter` + `DiffUtil`) · Lifecycle ViewModel + LiveData · Room 2.6 · JUnit 4

## Build and run

1. Open the project folder in Android Studio (a recent stable release; the project uses AGP 9.x and Gradle 9.x via the wrapper).
2. Let Gradle sync, then run on an emulator or device with API 34+.
3. Register an account on the login screen, log in, and add items.

SMS sending only works on a device/emulator that supports telephony. The alert recipient is a placeholder in `utils/AppConfig.java`.

## Tests

```bash
./gradlew testDebugUnitTest          # JVM unit tests (ViewModels, validation, hashing)
./gradlew connectedDebugAndroidTest  # Room DAO tests, needs a device/emulator
```

- `MainViewModelTest`, `LoginViewModelTest`: use in-memory fake repositories, no Android required
- `ValidatorTest`, `PasswordHasherTest`: pure Java
- `ItemDaoTest` (instrumented): real Room against in-memory SQLite

A manual test checklist (rotation, persistence, error handling) is in [docs/testing.md](docs/testing.md).

## Screenshots

Add screenshots to `screenshots/` after running the app (login, inventory grid, low-stock SMS prompt) and link them here.

## Known limitations / future work

- Items are shared across all users (same as the original app); per-user inventories are a natural next step and are required for the cloud-sync project
- Old data in the legacy `AppDatabase.db` file is not migrated; the Room database uses a new file (`inventory.db`) and users re-register
- Alert phone number is a constant, not a user setting
- Callbacks + executor instead of coroutines/Flow, to keep the project in Java
