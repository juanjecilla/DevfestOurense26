# Step 1 · Discovery (skill: `feature-discovery-analysis`)

Scan of the manifest, `MainActivity`, `AgendaScreen`/`AgendaViewModel` and the use cases in `domain/`.
Core user journeys: browse the agenda, filter by track, star a talk, review favourites, clear favourites.

## Recommended AppFunctions

| Function | Kind | Why an agent helps | Input |
| --- | --- | --- | --- |
| `searchTalks` | rich query | "What Android talks are after lunch?" skips the track filter and the scrolling. Resolves talks to stable ids, the prerequisite for `addToFavorites`. | `track: String?`, `after: LocalTime?` |
| `addToFavorites` | mutation | "Save the afternoon Android talks" is several taps per talk today. | `talkIds: List<String>` |
| `getFavourites` | rich query | Lets the agent tell the user what is saved, and detect conflicts with other apps' calendars. | none |
| `clearFavorites` | mutation, **destructive** | Hands-free reset. Irreversible: exposed only with explicit confirmation guidance. | none |

## Deliberately not exposed
- **`getTalk(id)`**: purely informational, redundant with `searchTalks` (skill: *tool-first thinking*).
- **`removeFavourite(id)`**: low value over `clearFavorites`; the UI toggle covers it. Revisit if agents need it.
- Anything that toggles on tap in `AgendaScreen`: UI concerns, not capabilities.

## Security and privacy
No credentials or personal data in this app. The one irreversible action (`clearFavorites`) must carry a confirmation
constraint in its KDoc and in `app_metadata.xml`.
