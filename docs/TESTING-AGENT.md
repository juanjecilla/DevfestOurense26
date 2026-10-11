# AppFunctions Testing Agent

Official tool: `github.com/android/appfunctions` → `agent/` (Apache-2.0). Package `com.example.appfunctions.agent`.
Read from its README and `run_privileged.sh` on 2026-10-11 (**not yet run by us**).

## What it is
Manual debugging with deterministic inputs, test automation, and an **LLM agent** to evaluate functions.

## How privilege works
`run_privileged.sh` builds the app, installs it and launches `ShellIdentityInstrumentation`
(`am instrument …`), so the app runs with the **shell identity** and can call the privileged AppFunctions APIs.
This does **not** need root: it should work on a `user` build such as the Pixel 9 Pro used for the demo
(unverified).

## Run
```bash
git clone https://github.com/android/appfunctions && cd appfunctions/agent
./run_privileged.sh --build -s <adb serial>          # standard flavor, manual debugging, no API key
```
- `standard` flavor (default): manual invocation. No key needed.
- `retail` flavor: LLM agent. **Needs a Gemini API key**: `--flavor retail --api-key <KEY>`
  (or `-PGEMINI_API_KEY`). Use your own key, do not commit it, and keep it out of the slides and screen share.

## For the talk
1. Install the demo app (`./gradlew :app:installDebug`), then launch the Testing Agent.
2. Show the agent listing `com.example.devfest` functions and invoking `searchTalks` by hand.
3. If a key is available: type a natural-language request ("save the Android talks after 15:00") and let the LLM pick
   `searchTalks` → `addToFavorites`.
4. Fallback if it misbehaves: the adb captures in `docs/captures/` (also in the deck's backup scene).

## Open questions
- Does it build and run on this machine/phone (disk space on the phone was ~650 MB free)?
- Does the LLM flavor pick functions from the KDoc as hoped? Rehearse; the demo must not depend on it.
