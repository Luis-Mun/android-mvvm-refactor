# Architecture

```mermaid
flowchart LR
    subgraph View
        LA[LoginActivity]
        MA[MainActivity]
        AD[ItemAdapter]
    end
    subgraph ViewModel
        LVM[LoginViewModel]
        MVM[MainViewModel]
    end
    subgraph Repository
        UR[UserRepository]
        IR[ItemRepository]
    end
    subgraph Data
        UD[UserDao]
        ID[ItemDao]
        DB[(Room / SQLite)]
    end

    LA -->|user actions| LVM
    MA -->|user actions| MVM
    MA --- AD
    LVM -->|LiveData / Events| LA
    MVM -->|LiveData / Events| MA
    LVM --> UR
    MVM --> IR
    UR --> UD
    IR --> ID
    UD --> DB
    ID --> DB
```

## Rules the design enforces

1. **Views never touch the database.** `MainActivity` and `LoginActivity` only observe LiveData and call ViewModel methods.
2. **ViewModels never touch Android framework classes.** They take a repository interface, so tests inject a fake.
3. **Repositories own threading.** Room work runs on a single-thread executor; ViewModels use `postValue` from callbacks.
4. **One-shot UI effects use `Event<T>`.** Toasts and the SMS request are consumed once, so rotation doesn't replay them.
5. **Platform work stays in the View layer.** The ViewModel only *requests* a low-stock alert; `MainActivity` handles the permission prompt and `SmsNotifier` sends the message.

## Low-stock alert flow

```mermaid
sequenceDiagram
    participant U as User
    participant V as MainActivity
    participant VM as MainViewModel
    participant R as ItemRepository
    U->>V: tap "-" on item with qty 1
    V->>VM: adjustQuantity(item, -1)
    VM->>R: updateQuantity(id, 0)
    R-->>VM: onSuccess
    VM-->>V: lowStockAlerts Event("Alert: ... out of stock!")
    V->>V: SEND_SMS granted? (else request permission)
    V->>V: SmsNotifier.send(...)
```
