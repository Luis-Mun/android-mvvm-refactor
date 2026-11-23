# Test plan

## Automated

| Suite | Covers |
| --- | --- |
| `MainViewModelTest` | add (valid/invalid), increment, decrement-to-zero alert, no-op at zero, delete, repository failures, busy state |
| `LoginViewModelTest` | register, duplicate user, short password, login success/failure, repository failure |
| `ValidatorTest` | every validation rule and boundary, including phone number formats |
| `PasswordHasherTest` | correct/incorrect password, unique salts, malformed stored values |
| `ItemDaoTest` (instrumented) | insert, update, delete against real Room |

## Manual checklist (run on emulator/device)

- [ ] App starts on the login screen
- [ ] Register with a short password -> error shown
- [ ] Register, then register same username -> "User already exists"
- [ ] Log in with wrong password -> "Invalid credentials"; correct password -> inventory screen
- [ ] Add item with blank name / non-numeric quantity -> error toast, nothing added
- [ ] Add item -> appears in grid, inputs clear
- [ ] `+` / `-` change quantity; `-` at 0 does nothing
- [ ] Quantity reaching 0 with no alert number set -> phone number dialog opens; invalid number shows an error and keeps the dialog open
- [ ] After saving a number, SMS permission is requested (first time) and the alert is sent; the toast reflects the real result
- [ ] Menu > Log out returns to the login screen; Back then leaves the app instead of reopening the inventory
- [ ] Deny SMS permission -> "SMS notifications are disabled", app keeps working
- [ ] Delete item -> disappears
- [ ] Rotate screen -> list and state preserved, no duplicate toasts
- [ ] Force-stop and relaunch -> items still there (persistence)

## Results (November 22, 2026, Pixel 6 emulator, Android 14)

- `./gradlew testDebugUnitTest`: 31 tests, 0 failures
- `./gradlew connectedDebugAndroidTest`: 3 tests, 0 failures
- Manual checks above: passed. The low-stock text appeared in the emulator's Sent folder addressed to the saved number.
