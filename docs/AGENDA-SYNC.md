# Agenda sync: DevFest Ourense 2026

The agenda in this app is the **official one** (7 Nov 2026, ESEI Ourense), copied into
`app/src/main/java/com/example/devfest/data/TalkCatalog.kt`.

| | |
| --- | --- |
| Source | https://gdgourense.es/events/df26 (section "Sesiones & Ponencias Confirmadas") |
| Read on | **2026-11-02** (the organisers can still change it: re-read before the event) |
| Sessions | 23: 12 talks, 4 startup demos, breaks, welcome/presentation, round table, closing |
| Rooms | Red Velvet (main), Honeycomb, Cinnamon Bun (breaks, lunch, welcome) |

## How it was read
The page is an Angular SPA: `curl` and `WebFetch` only see the page title, and the static data bundled in the JS has speaker bios but **no titles or
times** (the schedule is loaded at run time). It was read with a browser (Claude in Chrome): `get_page_text` shows only the header, so the session list was
read from the accessibility tree of the "Sesiones & Ponencias Confirmadas" region (heading, time, room, speaker, description of each session), and one
session detail modal was opened to check what else it carries (nothing: time, room, title, speaker, description).

## What is the organisers' and what is ours
| Field | Origin |
| --- | --- |
| `title`, `speaker`, `room`, `start` | organisers (verbatim; titles keep the language they were published in) |
| `track` | **ours**: the site tags no session, only four event themes. We classify by title: `ai`, `android`, `web`, `demo`, `general` |
| `id` (`t01`…`t23`) | ours: order by start time, then room |
| end time / duration | not published, so not stored |

Classification: `ai` Orchestrating Autonomy, From Tokens to Circuits, Revive tus fotos con IA, NotebookLM, When Software Stops Being Deterministic,
Enseñando… Gemma 4. `android` Convierte tu app… (this talk), Set Sail for the Grand Line. `web` AI in the browser…, Prepara tu web…. `demo` Sizit, Trenio,
Docucraft, Pendiente de confirmar. `general` everything else.

## Quirks recorded (2026-11-02)
- *Revive tus fotos con IA*: the description field says "David Martínez" (a name, not a description). Only the speaker field (Martín Díaz Fernández) is used.
- *Pendiente de confirmar* (18:15, Red Velvet, a demo slot) is kept on purpose so it can be asked about live ("what is at 18:15?").
- The page lists the speaker of this talk as "Juanje Cilla (Hiberus)"; the slides say Revolut. Ask the organisers to correct the page if needed.
- Speaker names are shown on purpose (decision 2026-11-02): they are published by the organisers.

## Refreshing it
1. Re-read the page as above; diff against `TalkCatalog.kt` (the unit test `TalkCatalogTest` guards ids, ordering, tracks and the live-demo examples).
2. If the demo ids change (`t15`, `t21`, `t16`, `t20`), update `docs/DEMO.md`, `scripts/capture-demo.sh`, then in the deck repo `scripts/gen-code.py`
   and `deck/scenes/03-Scenario.tsx`, and re-run `scripts/capture-demo.sh` on a device.
3. Keep `docs/AGENDA-SYNC.md` honest: update "Read on".
