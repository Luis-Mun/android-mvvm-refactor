# Refactor notes: what moved where

| Original (`FirstApp_LuisMunoz`) | Problem | Now |
| --- | --- | --- |
| `DatabaseHelper` (`SQLiteOpenHelper`, raw SQL, `Cursor`s) | Raw SQL strings, `DROP TABLE` on upgrade, update/delete never written | Room: `AppDatabase`, `ItemDao`, `UserDao`, `Item`/`User` entities |
| `MainActivity` created `DatabaseHelper`, inserted rows, refreshed the grid | UI class owned data access and logic | `MainViewModel` + `RoomItemRepository`; Activity observes `LiveData<List<Item>>` |
| Hard-coded item `"Apples"`, qty `"0"` | Demo behavior baked into the click handler | Name/quantity inputs, validated in `Validator` |
| `quantity` stored as `TEXT`, compared with `.equals("0")` | Stringly-typed | `int quantity`; threshold in `AppConfig` |
| SMS permission + send logic inside `MainActivity`, deprecated `SmsManager.getDefault()` | Mixed concerns, generic fallback message | ViewModel emits an alert event; Activity handles permission via `ActivityResultLauncher`; `SmsNotifier` sends |
| `CustomAdapter` held a `Cursor` and `notifyDataSetChanged()` | Cursor leaks / full redraws | `ItemAdapter` (`ListAdapter` + `DiffUtil`), no data access |
| `LoginActivity` called `DatabaseHelper.checkUser` | Auth logic in the View | `LoginViewModel` + `RoomUserRepository` |
| Passwords stored in plain text | Security flaw | Salted PBKDF2-HMAC-SHA256 (`PasswordHasher`) |
| `activity_dashboard.xml` (weight tracker with dummy rows) | Unused leftover from another assignment | Removed |
| Update / delete not implemented ("add your methods here") | Incomplete CRUD | `+` / `-` / Delete buttons wired through ViewModel and repository |

## Behavior changes to be aware of

- The Room DB is a new file (`inventory.db`); the old SQLite data is not carried over.
- `LoginActivity` now `finish()`es after login so Back doesn't return to the login screen.
- The SMS alert fires when quantity *reaches* 0 (on add or on decrement), not on every update of a zero-quantity item.
