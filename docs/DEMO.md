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
**2026-11-02, Pixel 9 Pro** (Android 17, API 37.1, security patch 2026-10-05, **user build**, no root), USB. Debug APK from `main`: `androidx.appfunctions 1.0.0-beta01` + the real agenda.
All of this worked: `check`, `list` (4 functions, `functionId` = Base class), `search ai 15:00` (t15, t21), `add t15 t21` (`added 2, total 2`), the same `add` again (`added 0`), `favourites`,
`add nope` (1500 element not found), 21 ids (1501 limit exceeded), `search "" 25:99` (1001 invalid argument), `clearFavorites` without `confirmed` (1001, refuses), app force-stopped then
`search "" "" gema` (the call starts the process: t16 Gema Socorro), `search demo 18:00` and `search "" 18:15` (the unconfirmed slot t20), `disable`/`enabled`/`reset`
(1002 function disabled), a break comes back without `speaker` (nullable property omitted). Real outputs: `docs/captures/`.
Not verified: the Testing Agent (see `docs/TESTING-AGENT.md`) and Gemini (EAP).
The phone's `/data` was 100 % full (808 MB free) during the test: free space before the event.

**`LocalTime` as an input (beta01, tested on the device 2026-11-02 with a scratch function, since discarded):** works **only as an object with all four fields**
`{"hour": 14, "minute": 30, "second": 0, "nanoOfSecond": 0}`; the string `"14:30"` and a partial object `{"hour": 14, "minute": 30}` are rejected with code 1001
(`Parameter … should be the type of AppFunctionReferenceTypeMetadata(referenceDataType=java.time.LocalTime …)`). On alpha10 (2026-10-11) it failed outright
(`Unable to deserialize class java.time.LocalTime`); release notes: alpha13 added support, beta01 fixed desugaring. The demo keeps `"HH:mm"` strings: one field an
agent can fill without knowing the object shape. As output, `LocalTime` is serialized as that object too.

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
