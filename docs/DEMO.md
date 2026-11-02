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

## Verified
**Pending (2026-11-02):** after the move to `1.0.0-beta01` and the real agenda the full sequence below must be re-run on the Pixel (it was not
connected; unit tests and the build pass). Until then treat the next block as the alpha10 result of 2026-10-11.

Pixel 9 Pro, Android 17 (API 37.1, patch 2026-09-05, **user build**, no root). Debug APK, **alpha10, sample agenda**, checked 2026-10-11:
`list` (document type `AppFunctionStaticMetadata-<package>`), `search`, `add`, idempotent `add`, `favourites`,
`clear` with and without `confirmed`, app force-stopped and woken by the call, `disable` / `enabled` / `reset`.
Error codes seen: `1001` invalid argument, `1002` disabled, `1500` element not found, `1501` limit exceeded.
Not verified: the Testing Agent (`run_privileged.sh` needs a rootable device) and Gemini (EAP).

**Alpha10 gotcha:** `java.time.LocalTime` as an *input* fails (`Unable to deserialize class java.time.LocalTime`) and as an
*output* is serialized as an object `{hour, minute, second, nanoOfSecond}`. The demo uses `"HH:mm"` strings (narrow types are better for
agents anyway). Release notes (checked 2026-11-02): alpha13 "Support all built-in proxy types (LocalDate, LocalTime, Instant, ZoneId)";
beta01 "Fixed java.time parameter types failing to deserialize in apps with Java library desugaring enabled".

## Before going on stage
```bash
./gradlew :app:installDebug
scripts/appfunctions-adb.sh check        # must print the help page, not "No shell command implementation"
scripts/appfunctions-adb.sh list         # the four functions, functionId = <Base class>#<function>
scripts/appfunctions-adb.sh clear        # start from zero favourites
```
Open the app on the **Favourites** tab, then put it in the background.

## Beats (match the deck, scene 12)
The agenda is the **real DevFest Ourense 2026 one** (`docs/AGENDA-SYNC.md`). `YAML=1` gives compact output (set it for the whole demo).
1. **Scenario**: the agenda app (23 sessions); four functions: `searchTalks(track, after, query)`, `getFavourites`, `addToFavorites`, `clearFavorites`.
2. **list**: `scripts/appfunctions-adb.sh list` (beta01: functions declared with `@AppFunctionDeclaration`).
3. **read**: `scripts/appfunctions-adb.sh search ai 15:00` → the two AI talks after lunch (`t15` 16:00, `t21` 18:30).
4. **write**: `scripts/appfunctions-adb.sh add t15 t21` → open the app: the Favourites tab shows them.
5. **closed app**: `scripts/appfunctions-adb.sh stop`, then `search "" "" gema` (a speaker query: `t16`), same result.
6. **ask about a slot** (audience question): `scripts/appfunctions-adb.sh search demo 18:00` → *Pendiente de confirmar* (18:15), or `search "" 18:15`.
7. **errors / safety**: `add nope` (unknown id → `AppFunctionElementNotFoundException`); `clear` is sent with `confirmed = true`; without it the function refuses.
8. **Testing Agent**: see `docs/TESTING-AGENT.md` (privileged via shell identity, LLM mode needs your own Gemini key).

## Backups
- **Real captured outputs** of every adb step: `docs/captures/` (empty until re-captured, see its README) (regenerate with `SERIAL=<serial> scripts/capture-demo.sh`
  after changing the agenda or functions). The deck's backup scene is generated from them (`scripts/gen-code.py`).
- Branches/tags are checkpoints: `git switch 02-appfunctions-skill` (skill output, minimal then refined KDoc),
  `git switch 01-base-agenda` (before AppFunctions).
- Screen recording of the app UI changing after `add` (do it on the phone before the event).
