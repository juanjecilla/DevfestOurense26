# Demo runbook (≈ 10 min)

Device: an emulator or phone running **Android 16 QPR+ / Android 17 with the `app_function` shell command**.
The first Android 16 GA image (May 2025 build) has the service but **not** the shell command:
`adb shell cmd app_function help` answers `No shell command implementation.` There the adb part cannot work.
Checked image: `system-images;android-37.0;google_apis;arm64-v8a` (userdebug, root: needed by the Testing Agent).
On it `cmd app_function help` works and lists `list-app-functions [--package]`, `execute-app-function` (`--timeout-duration`,
`--brief-yaml`, `--pending-intent-path`), `set-enabled`, `is-enabled`, `purge-allowlist-cache`. The AppFunctions
flags are already `true` (`device_config list | grep app_function`).

### Emulator notes (macOS arm64, emulator 37.2.12, observed 2026-10-11)
- The API 37.0 AVD gets **no HVF** unless you add `-qemu -enable-hvf` (the front-end prints `hvf is not enabled on this
  aarch64 host` and falls back to software emulation, which hangs). Android 16 AVDs get `-enable-hvf` automatically.
- Booting works with `-gpu host` but `surfaceflinger` then **crash-loops** every ~5 s:
  `Assertion failed: !rcEnc->featureInfo()->hasReadColorBufferDma` in `GoldfishMapper::readFromHost`.
  Tried without success: `-gpu swiftshader_indirect`, `-feature -GLDMA,-GLDMA2`. `-gpu guest` is unsupported by the image.
- The AVD needs ~1.2x its data partition free on the host (`-partition-size` / `disk.dataPartition.size`, note the
  config file uses ` = ` with spaces for some AVDs). Keep >= 10 GB free.
- Fallback: a physical device on Android 16 QPR / 17 with the `app_function` shell command.

## Before going on stage
```bash
./gradlew :app:installDebug
scripts/appfunctions-adb.sh check        # must print the help page, not "No shell command implementation"
scripts/appfunctions-adb.sh list         # the four functions, functionId = <Base class>#<function>
scripts/appfunctions-adb.sh clear        # start from zero favourites
```
Open the app on the **Favourites** tab, then put it in the background.

## Beats (match the deck, scene 12)
1. **Scenario**: the agenda app; four functions: `searchTalks`, `getFavourites`, `addToFavorites`, `clearFavorites`.
2. **list**: `scripts/appfunctions-adb.sh list`
3. **read**: `scripts/appfunctions-adb.sh search android 14:00`
4. **write**: `scripts/appfunctions-adb.sh add t08 t09` → open the app: favourites changed.
5. **closed app**: `scripts/appfunctions-adb.sh stop`, then `search` again, same result.
6. **errors / safety**: `add nope` (unknown id → `AppFunctionElementNotFoundException`);
   `clear` is done with `confirmed = true`; without it the function refuses.
7. **Testing Agent** (`github.com/android/appfunctions`, `agent/`, `./run_privileged.sh`): natural language over the same functions.

## Backups
- Branches/tags are checkpoints: `git switch 02-appfunctions-skill` (skill output, minimal then refined KDoc),
  `git switch 01-base-agenda` (before AppFunctions).
- Keep a screen recording or screenshots of each step in `docs/captures/` (add before the event).
