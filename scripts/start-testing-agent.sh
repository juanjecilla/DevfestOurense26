#!/usr/bin/env bash
# Starts the official AppFunctions Testing Agent (https://github.com/android/appfunctions, agent/) on a device.
# Why not its own run_privileged.sh? On the Pixel 9 Pro (Android 17, API 37.1, checked 2026-11-02) `dumpsys package` prints a 7-char
# signature hash and that script feeds it to `cmd allowlist` as a certificate digest ("string length not even: 7"). This does the same
# steps with the real SHA-256 of the APK's signing certificate.
# Usage: SERIAL=<adb serial> scripts/start-testing-agent.sh [path/to/agent]   (default ~/Projects/Demos/_external/appfunctions-samples/agent)
# Revert: adb shell cmd allowlist remove-package 2 <package>:<digest>   or   adb shell cmd allowlist clear-shell-allowlist 2
set -euo pipefail
AGENT_DIR="${1:-$HOME/Projects/Demos/_external/appfunctions-samples/agent}"
PKG=com.example.appfunctions.agent
APK="$AGENT_DIR/app/build/outputs/apk/standard/debug/app-standard-debug.apk"
ADB="${ADB:-adb}"; [ -n "${SERIAL:-}" ] && ADB="$ADB -s $SERIAL"
APKSIGNER="$(ls -d "${ANDROID_HOME:-$HOME/Library/Android/sdk}"/build-tools/*/apksigner 2>/dev/null | sort | tail -1)"

[ -f "$APK" ] || { echo "Build it first: (cd $AGENT_DIR && echo sdk.dir=\$ANDROID_HOME > local.properties && ./gradlew assembleStandardDebug)"; exit 1; }
[ -x "$APKSIGNER" ] || { echo "apksigner not found in the Android SDK build-tools"; exit 1; }
DIGEST=$("$APKSIGNER" verify --print-certs "$APK" | awk '/certificate SHA-256 digest/{print $NF}' | head -1 | tr 'A-F' 'a-f')
[ ${#DIGEST} -eq 64 ] || { echo "could not read the certificate digest"; exit 1; }

$ADB install -r "$APK" | tail -1
$ADB shell cmd app_function purge-allowlist-cache > /dev/null 2>&1 || true
$ADB shell cmd allowlist add-package-multimap 2 "$PKG:$DIGEST" "'*'"
$ADB shell am force-stop "$PKG"
$ADB shell am instrument -w "$PKG/.ShellIdentityInstrumentation" > /tmp/testing-agent-instrumentation.txt 2>&1 &
sleep 4
echo "Started. Open it on the phone: Debug tab > search 'DevFest' > searchTalks > track=ai, after=15:00 > Run."
echo "Revert the allowlist when done: $ADB shell cmd allowlist remove-package 2 $PKG:$DIGEST"
