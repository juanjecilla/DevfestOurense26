# Swapping the sample agenda for the real DevFest Ourense talks

The agenda in this app is **sample data** (made-up titles, times, rooms, no speakers). When the organisers publish the
full list, change the places below, in this order, and re-run the demo.

## In this repo
| What | File | Notes |
| --- | --- | --- |
| The talks | `app/src/main/java/com/example/devfest/data/TalkCatalog.kt` | `id` must stay stable and short (`t01`…). `track` is lowercase; `tracks` is derived from the list |
| Track names in agent-facing docs | `…/appfunctions/BaseAgendaAppFunctionService.kt` (KDoc of `searchTalks`), `…/appfunctions/AgendaModels.kt` (`Talk.track`), `app/src/main/res/xml/app_metadata.xml` if you mention tracks | The KDoc is what the model reads: keep it in sync with the real track names |
| Demo ids | `docs/DEMO.md` (`add t08 t09`), `scripts/appfunctions-adb.sh` usage | Pick two real afternoon talks of the Android track |
| Unit tests | `app/src/test/…/UseCasesTest.kt` | Own fixtures; no change needed |

Keep **one lunch/break entry** (the opening scenario relies on "overlaps lunch") and **at least two Android talks after
14:00** so `search android 14:00` returns something. Do not add speakers or personal data without their consent.

## In the deck repo (`~/Projects/Charlas/app_functions`)
| What | File |
| --- | --- |
| Ids and track in adb snippets, idempotency example | `scripts/gen-code.py` (`t08`, `t09`, `"android"`) → run `python3 -I scripts/gen-code.py` |
| Opening scenario call labels (`searchTalks("android", "14:00")`, "3 talks") | `deck/scenes/03-Scenario.tsx` |
| Speaker notes mentioning the demo | `deck/scenes.ts` (scene 12) |

Then in the deck: `npm run shot -- 3 9 10 12`, `npm run build`, `npm run verify`. Mirror in `ourense26/` if it exists.

## Re-verify on the device
```bash
./gradlew :app:testDebugUnitTest :app:installDebug
scripts/appfunctions-adb.sh check && scripts/appfunctions-adb.sh list
YAML=1 scripts/appfunctions-adb.sh search android 14:00
```
Commit on a branch, move `main` and the `cp-04-final` tag only when it works.
