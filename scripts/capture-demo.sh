#!/usr/bin/env bash
# Runs the demo sequence on a device and stores each real output in docs/captures/ (backup for the slides).
# Usage: SERIAL=<adb serial> scripts/capture-demo.sh      Resets favourites first and leaves them cleared.
set -uo pipefail
cd "$(dirname "$0")/.."
H=scripts/appfunctions-adb.sh
OUT=docs/captures
export YAML=1
mkdir -p "$OUT"

cap() { # cap <file> <command...>   stores stdout+stderr
  local f="$OUT/$1"; shift
  "$@" > "$f" 2>&1 || true
  echo "== $f"; head -c 300 "$f"; echo
}

$H clear > /dev/null 2>&1 || true
unset YAML
cap 01-list.txt bash -c "$H list | grep -A1 '\"functionId\"' | grep '#' | sed 's/^ *//; s/\"//g' | awk '!seen[\$0]++'"
export YAML=1
cap 02-search.txt $H search android 14:00
cap 03-add.txt $H add t08 t09
cap 04-add-again.txt $H add t08
cap 05-favourites.txt $H favourites
cap 06-add-unknown.txt $H add nope
cap 07-clear-unconfirmed.txt bash -c "${ADB:-adb} ${SERIAL:+-s $SERIAL} shell \"cmd app_function execute-app-function --package com.example.devfest --function 'com.example.devfest.appfunctions.BaseAgendaAppFunctionService#clearFavorites' --parameters '{\\\"confirmed\\\": false}'\""
$H stop
cap 08-closed-app.txt $H search android 15:30
$H disable searchTalks > /dev/null 2>&1
cap 09-disabled.txt $H search android
$H reset searchTalks > /dev/null 2>&1
$H clear > /dev/null 2>&1
echo "done: $(ls $OUT | wc -l) captures"
