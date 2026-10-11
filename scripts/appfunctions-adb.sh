#!/usr/bin/env bash
# ADB helpers for the AppFunctions demo (skill step 4: testing and debugging).
# Usage: scripts/appfunctions-adb.sh <command> [args]     SERIAL=<adb serial> to pick a device, YAML=1 for concise output.
set -euo pipefail

PKG="${PKG:-com.example.devfest}"
SVC="com.example.devfest.appfunctions.BaseAgendaAppFunctionService"
ADB="${ADB:-adb}"
[ -n "${SERIAL:-}" ] && ADB="$ADB -s $SERIAL"

run() { # run <function> <json-parameters>
  $ADB shell "cmd app_function execute-app-function \
      --package $PKG \
      --function '$SVC#$1' ${YAML:+--brief-yaml} \
      --parameters '$2'"
}

case "${1:-help}" in
  check)     $ADB shell cmd app_function help ;;
  list)      $ADB shell cmd app_function list-app-functions --package "$PKG" ;;
  enabled)   $ADB shell cmd app_function is-enabled --package "$PKG" --function "$SVC#${2:?function name}" ;;
  search)    run searchTalks "{\"track\": \"${2:-android}\"${3:+, \"after\": \"$3\"}}" ;;
  favourites) run getFavourites '{}' ;;
  add)       shift; ids=$(printf '"%s",' "$@"); run addToFavorites "{\"talkIds\": [${ids%,}]}" ;;
  clear)     run clearFavorites '{"confirmed": true}' ;;
  stop)      $ADB shell am force-stop "$PKG" ;;
  disable)   $ADB shell cmd app_function set-enabled --package "$PKG" --function "$SVC#${2:?function name}" --state disable ;;
  enable)    $ADB shell cmd app_function set-enabled --package "$PKG" --function "$SVC#${2:?function name}" --state enable ;;
  reset)     $ADB shell cmd app_function set-enabled --package "$PKG" --function "$SVC#${2:?function name}" --state default ;;
  *) cat <<USAGE
check | list | enabled <function> | search [track] [after HH:mm] | favourites | add <id>... | clear | stop | disable|enable|reset <function>
USAGE
  ;;
esac
