# Captured demo outputs

Empty on purpose until the sequence is re-run on a device with the real agenda and beta01 (2026-11-02).
The previous captures (alpha10, sample agenda, Pixel 9 Pro, 2026-10-11) were never committed here (the Android
template `.gitignore` ignored every `captures/` folder; fixed on 2026-11-02). Copies of them are in the deck repo history
(`reference/demo-captures/`, commit `25ec6a9` of the `slideshow` repo, branch `talk/app-functions-devfest26`).

```bash
SERIAL=<adb serial> scripts/capture-demo.sh      # writes 01-list.txt … 10-tbc.txt here
```
Then, in the deck repo: `python3 -I scripts/gen-code.py` (the backup scene appears automatically when captures exist).
