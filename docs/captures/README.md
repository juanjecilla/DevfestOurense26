# Captured demo outputs

Real adb outputs of the demo sequence: **Pixel 9 Pro, Android 17 (API 37.1), `androidx.appfunctions 1.0.0-beta01`, real agenda, captured 2026-11-02**.
They feed the backup scene of the talk (deck repo: `python3 -I scripts/gen-code.py`).

Re-capture after changing the agenda or the functions:
```bash
SERIAL=<adb serial> scripts/capture-demo.sh      # rewrites 01-list.txt … 10-tbc.txt
```
The 2026-10-11 captures (alpha10, sample agenda) were never committed (the Android template `.gitignore` ignored every `captures/` folder; fixed 2026-11-02).
