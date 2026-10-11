# DevFest Ourense 26 · AppFunctions demo

Demo app for the talk *Turn your app into a skill with App Functions* (DevFest Ourense 2026).
An agenda app with favourites. The talks in it are **sample data** (made up titles, times and rooms).

## Branches = checkpoints

| Branch | Tag | State |
| --- | --- | --- |
| `01-base-agenda` | `cp-01-base` | Plain app: Compose + Hilt + DataStore, clean layers, no AppFunctions |
| `02-appfunctions-skill` | `cp-02-skill-*` | The same app, AppFunctions added by following the official Android skill |
| `03-final` / `main` | `cp-04-final` | Hardened for agents: typed results, validation, idempotency, confirmation, `app_metadata` |

If something breaks on stage, `git switch <branch>` and reinstall.

## Build and run

```bash
./gradlew :app:installDebug          # needs a device or emulator
./gradlew :app:testDebugUnitTest
```

Versions are pinned in `gradle/libs.versions.toml` (AGP 9.1.1, Kotlin 2.4.20, Hilt 2.60.1).
`compileSdk 37`, `targetSdk 36`, `minSdk 26`.

## Layers

```
ui/        Compose screen + ViewModel            (driving adapter)
domain/    Talk, TalkRepository, use cases        (business rules)
data/      DefaultTalkRepository, FavouritesStore (DataStore), TalkCatalog (sample data)
di/        Hilt modules
```
