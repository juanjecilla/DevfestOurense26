# AppFunctions Testing Agent

Official tool: `github.com/android/appfunctions` → `agent/` (Apache-2.0). Package `com.example.appfunctions.agent`.
Read from its README and `run_privileged.sh` on 2026-10-11 (**not yet run by us**).

## What it is
Manual debugging with deterministic inputs, test automation, and an **LLM agent** to evaluate functions.

## How privilege works
`run_privileged.sh` builds the app, installs it and launches `ShellIdentityInstrumentation`
(`am instrument …`), so the app runs with the **shell identity** and can call the privileged AppFunctions APIs.
This does **not** need root: it should work on a `user` build such as the Pixel 9 Pro used for the demo
(verified 2026-11-02).

## Run (verified 2026-11-02 on a Pixel 9 Pro, Android 17 / API 37.1, user build, no root)
Source cloned and built outside this repo (commit `0bb367a2`, 2026-10-30; Gradle 9.6.1 wrapper; needs `local.properties` with `sdk.dir`):
```bash
git clone --depth 1 https://github.com/android/appfunctions ~/Projects/Demos/_external/appfunctions-samples
cd ~/Projects/Demos/_external/appfunctions-samples/agent
echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
./gradlew assembleStandardDebug          # ~80 MB APK, no API key needed for the standard flavor
```
Start it with **our** script (not the official `run_privileged.sh`, see below):
```bash
SERIAL=<adb serial> scripts/start-testing-agent.sh
```
It installs the APK, adds the app to the system **shell allowlist 2** with the real SHA-256 of its signing certificate, and runs `am instrument … ShellIdentityInstrumentation`.

**Why not `run_privileged.sh`:** on this device `dumpsys package` prints `signatures:[9281b92]` (a 7-character hash) and the script passes it to `cmd allowlist add-package-multimap` as a certificate digest;
the device answers `IllegalArgumentException: string length not even: 7`. This build also lacks `add-package-with-metadata-multimap` and `flush-allowlist-changes` that the script tries first.
`cmd allowlist help` here asks for the digest "in hex format" (SHA-256 of the cert, from `apksigner verify --print-certs`). Note: the allowlist grant stays on the phone until you remove it:
`adb shell cmd allowlist remove-package 2 com.example.appfunctions.agent:<digest>` or `adb shell cmd allowlist clear-shell-allowlist 2`.

**Result:** the agent starts with shell identity, its Debug tab finds **DevFest Ourense** in the system registry and lists our 4 functions (`searchTalks`, `addToFavorites`, `clearFavorites`, `getFavourites`).
`searchTalks` expands into a form with one field per parameter (`track`, `after`, `query`); `track=ai`, `after=15:00` + Run shows `Result #1` (t15, Susanna Wong, 16:00, Red Velvet) and `Result #2` (t21, Eric Risco, 18:30) as typed cards.
The LLM mode ("Agent Demo" tab) belongs to the `retail` flavor and needs a Gemini API key: **not run**.

## For the talk
1. Install the demo app (`./gradlew :app:installDebug`), then launch the Testing Agent.
2. Show the agent listing `com.example.devfest` functions and invoking `searchTalks` by hand.
3. If a key is available: type a natural-language request ("save the Android talks after 15:00") and let the LLM pick
   `searchTalks` → `addToFavorites`.
4. Fallback if it misbehaves: the adb captures in `docs/captures/` (also in the deck's backup scene).

## Open questions
- ~~Does it build and run on this machine/phone?~~ Yes (2026-11-02), see above.
- Does the LLM flavor pick functions from the KDoc as hoped? Rehearse; the demo must not depend on it.
