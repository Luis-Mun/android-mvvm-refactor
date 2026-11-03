# Test plan

## Automated

| Suite | Covers |
| --- | --- |
| `MainViewModelTest` | add (valid/invalid), increment, decrement-to-zero alert, no-op at zero, delete, repository failures, busy state |
| `LoginViewModelTest` | register, duplicate user, short password, login success/failure, repository failure |
| `ValidatorTest` | every validation rule and boundary |
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
- [ ] Quantity reaching 0 asks for SMS permission (first time) and sends alert when granted
- [ ] Deny SMS permission -> "SMS notifications are disabled", app keeps working
- [ ] Delete item -> disappears
- [ ] Rotate screen -> list and state preserved, no duplicate toasts
- [ ] Force-stop and relaunch -> items still there (persistence)
