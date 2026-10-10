# Alfred UI plan — working draft 4

**Date:** 2026-10-09  
**Status:** Owner-directed requirements and planning only. **Not an implementation authorization.**  
**Governing references:** `Home.pdf` (original workflow/wireframe), `Reality_vs_expectation.pdf` (owner's annotated implementation comparison), and the owner's follow-up corrections. This document supersedes contradictory UI assumptions in draft 1–3, **not** established engine/release contracts.

> **Core principle:** Alfred is an information-dense, file-first **shared audio workspace**, not a minimalist app and not a launcher for disconnected utility pages. The sparse home screen leads to a spreadsheet of tracks and a vertically resizable tool panel. The files remain central throughout the task.

## 0. Read this first: boundaries and status

### Active UI repair: capabilities that already exist

1. Home, file/folder picker and import feedback.
2. Music workspace: selected-track table, source properties, sorting/configurable columns, toolbar and actual drag-to-resize split.
3. Audio Forensics: choose analysis, queue/run/cancel, display **actual understandable reports in the same lower panel**, navigate batch/saved reports and retain access to evidence.
4. Existing Spectrogram (A06a) and Compare (A06b) implementations: verify their current state, keep reachable actions/results and remove developer-facing presentation where possible. Do **not** assume these features vanished or are merely concepts.
5. Existing navigation, history, settings, export/share, failure and permission states.

### Not an active UI build target

- Metadata Studio tag editor, Converter and its post-conversion preferences, Log/CUE checker/extractor, and other unbuilt utilities.
- New processing algorithms, unverified backend capabilities or feature-completion disguised as a UI rewrite.
- Mock buttons labelled “planned” for every future feature. Reserve structural room without filling it with promotional placeholders.

**Important exception:** The owner's corrected requirements reveal *functional* gaps in existing input acquisition and forensic execution: recursive folder import, the arbitrary 32-track restriction, and selective detector execution. **Document and assess these gaps now**, but do not claim they are fixed by layout changes, and do not quietly expand a UI-only coding task into an engine rewrite. Explicitly mark implementation dependencies.

### Requirement status terms

- **OWNER REQUIREMENT:** Directly stated or drawn and clarified by the owner.
- **IMPLEMENTATION GAP TO VERIFY:** Observed in UI or reported by owner; underlying code/capability must be checked.
- **PROPOSAL:** A design detail to review, not approved merely because it appears in this document.
- **PARKED:** Confirmed future direction, not to be implemented during current UI repair.

## 1. Reality vs expectation — corrected discrepancy ledger

| Area | Current problem / observed behavior | Owner's expected behavior | Type |
| --- | --- | --- | --- |
| Home | Marketing copy, decorative statements, history/promotional clutter | **Almost empty**: Alfred, one Select File / Folder entry, settings | Presentation — owner requirement |
| Import chooser | Three actions: one document / documents / folder; technical word “document” | **Two actions**: **Files** (select one **or many**) and **Folder** | Presentation and picker behavior — owner requirement |
| Folder import | First directory level only | **Recursive** inclusion of eligible files in nested subfolders | File-handling capability — verify/implement separately |
| Track count | UI says “up to 32 tracks” | **No owner-imposed 32-track limit**. Investigate actual resource constraints; do not advertise 32 as a product rule | Acquisition/jobs capacity — verify |
| Music landing | Extra labels, a short clipped table and a great deal of non-track prose | The **table is the main workspace**; useful columns, compact controls, active lower panel | Presentation — owner requirement |
| Table resizing | Previous horizontal slider; later drag grip but only ~60% maximum panel height | **Vertical divider** between table and tools; lower panel can expand to **virtually the entire available screen** | Presentation/gesture — owner requirement |
| Forensic navigation | Inline scan setup then another page or history route for real results | Setup **→ scanning → findings → evidence**, all in the **same lower panel** | Presentation/navigation — owner requirement |
| Result content | Status strings, job IDs, unexplained options/data, JSON-centric report | Human-readable interpretation, evidence, scope and limitations; raw report optional | Report mapping — owner requirement |
| Partial analysis | “Partial” currently means analyzing the first 1–180 seconds | **Choose a subset of forensic analyses**, e.g. **MQA detection** or **Dynamic Range**; may select multiple | Semantics **and** potential engine/job capability — owner requirement |
| Existing feature visibility | Spectrogram / Compare described as “planned” after rewrites | Preserve already implemented workflows and outputs; verify which work on device | Regression check — active |

Do not declare any row “done” from passing compilation, lint or instrumentation alone. **The owner's phone interactions and rendered results are the product acceptance test.**

## 2. Home and import

### Home — owner requirement

```
                                      [Settings]

                    Alfred

              [Select File / Folder]
```

- Home is sparse **because the task starts with choosing files**, not because the entire application should be minimalist.
- No “Your music. In detail.” slogan, large feature tour, unrequested platform/codec marketing, or blocks listing planned functions.
- Preserve a path to existing saved results if required, **without making it dominate this home composition**. Exact history entry placement is a secondary navigation question.

### Selection — owner requirement

Opening the main selection action exposes **exactly two conceptual import choices**:

1. **Files** — choose **one or multiple audio files** through the file picker.
2. **Folder** — choose a directory and include supported files from its **nested subdirectories**, not just the top level.

The labels should say **file(s)**, **audio**, **folder**, or **tracks** where applicable—not `document(s)`, a raw SAF term.

**Functional validation needed:** Android's Storage Access Framework/provider capabilities, recursive traversal, access grants, cancellation, duplicate paths, load time, ordering and very large folder trees. Directory traversals should be bounded by legitimate resource and lifecycle handling (e.g., cancellation, progress, errors), **not by inventing a fixed 32-track UX cap**. If a real system limit exists, investigate and report it separately before setting a new product limit. Do not claim that all nested audio loaded when only a subset did.

**Proposal, not decided:** When users import more files from the Music workspace, decide and correctly label whether files **append** or **replace** the current set. Do not use “Add music” if the operation secretly replaces it. Maintain access to the file picker without routing the user all the way back through Home.

## 3. The Music workspace is the main canvas

### Owner's underlying concept

A mobile-adapted **Mp3tag-like spreadsheet**, borrowing useful source-file presentation ideas from MediaHuman. Forensics, technical details and ultimately other utilities share the same file selection and workspace. **The user changes the task, not the entire page.**

```
[Back/Home]           [Music/workspace]        [Import] [More]
[select all]  [N selected]              [table controls]
┌─────────────┬────────────────────────────────────────────┐
│ ✓  Track    │ Album  Artist  Format  Rate  Bit depth ... │ →
│ ✓  Track A  │ ...                                        │
│ □  Track B  │ ...                                        │
│            │   ↕ vertical table scrolling               │
└─────────────┴────────────────────────────────────────────┘
                     ══ drag grip ══
[existing tool/navigation controls — layout to verify]
┌──────────────────────────────────────────────────────────┐
│ Setup / active job / **actual readable result**          │
│ Expandable findings and relevant controls               │
│ independent lower-panel scrolling                       │
└──────────────────────────────────────────────────────────┘
```

The box above illustrates **relationships and gestures**, not fixed tab names, pixel sizes, colors or a mandate to render unfinished features.

### Spreadsheet contract

- **Selection:** Checkboxes define which tracks existing batch operations affect. Unchecked tracks are never added implicitly. “Select all” reflects none/some/all and reports the selected count.
- **Focus:** Tapping a track can focus its details/report without silently changing the checked set. Selection and focus are different states.
- **Initial order:** Order of import. Tapping a column header sorts by that column's **actual values** (e.g. Track/Title alphabetically), not by raw UI row indices. Sorting must not change the membership or identity of already submitted jobs.
- **Custom columns:** Users can add/remove displayed columns according to available fields, similar to Mp3tag. Do not require dozens of permanent default columns on a phone.
- **Source “quality”:** In the original drawing, **Quality means the file's current technical characteristics**, akin to MediaHuman's `FLAC 44100Hz stereo 16bps`. Alfred may distribute **Format, sample rate, bit depth, channels, bitrate, duration and song tags** across independent columns. It is **not** a sound-quality score or forensic authenticity grade.
- **Song identity:** Preserve readable track identification; ellipsize only when necessary, with a way to see the full title/filename. Never invent a title or artist if metadata are absent. Distinguish **Track** tag/title versus raw filename as the data permit.
- **Horizontal scroll:** Column headings and their underlying values share the same horizontal offset. Checkbox/compact song identity may be pinned **if actually usable at phone widths**; test this instead of imposing giant frozen columns.
- **Vertical scroll:** Scroll the table separately from the lower tool panel. Long names, numerous tracks, large fonts and narrow phones must remain usable.
- **Interactions:** Keep the table straightforward; no unnecessary swipes, per-cell controls or forced inline tag editing (the Metadata Studio editor is future work).
- **Status:** Per-track scan state may be available, but do not turn the **shared table** into a Forensics-specific sheet with a mandatory wide `Scan` column.
- **Row copy:** Short readable values; errors/details on focus, not multiple lines of diagnostics in every row.

**Proposals requiring visual review:** final default column order, pinned-column width, sorting cycle and the exact column-picker entry point. The owner has already confirmed sorting and add/remove columns; only their details remain.

### Distinguish source properties from converter output (parked)

The original **Resolution** toolbar concept is a **target output format/sample-rate/bit-depth preset for conversion**, not spectrogram-image resolution and not a quality rating. The **source** audio information belongs in the spreadsheet. The owner has chosen a future **hybrid** design (preset near top, detailed Converter controls below). **Do not implement the converter preset or converter panel now**, because the converter itself is not ready.

## 4. Resizable panel — explicit non-negotiable behavior

**OWNER REQUIREMENT: this is a split workspace, not a bottom sheet capped at 60% and not a horizontal seekbar.**

- A visible **horizontal grip** sits on the boundary between upper spreadsheet and lower tools. The gesture is **vertical dragging** of that boundary.
- Drag **up** to expand the tool panel; drag **down** to give the table more room.
- Support the full meaningful range: **mostly table**, **balanced split**, and **nearly full-screen lower panel**. At the uppermost position, the lower panel takes essentially all **usable app content area**, leaving only necessary system insets, navigation, and a small grip/collapsed table affordance. **Do not impose an arbitrary ~60% maximum.**
- The table may collapse to a thin handle/compact context when the panel is nearly full-screen. **Do not force a large permanently visible track table while the user is reading a forensic report or, later, editing metadata.**
- Dragging back must restore the table and retain selected files, active tool, reports and progress.
- Lower-panel content scrolls independently. Dragging inside a report scrolls the report; dragging the grip resizes the split. Avoid nested gesture conflicts.
- Make the grip easy to hit on a phone; provide accessible alternatives where appropriate. Do **not** add a separate “Resize track table” volume-like slider.
- Avoid resetting the split when scan state updates, users change tools, the keyboard appears (for future editing), or the app rotates where preservation is feasible.
- This remains **one workspace**, even near full-screen; it does **not** navigate to a separate results activity/page.

**Physical acceptance scenarios:** On a phone, the owner can (1) reduce the table almost entirely, (2) read a long forensic result using nearly the whole viewport, (3) drag downward to show many rows, and (4) return to the same expanded content without losing context. A static screenshot of a grip or successful Compose test does not prove this works.

## 5. Audio Forensics: actual operation and understandable results

**Priority:** The user should not have to leave the workspace or read engineering status codes to get the answer to a completed scan.

### A. Two different concepts the current UI confused

| Concept | Definition | UI meaning |
| --- | --- | --- |
| **Full analysis** | Run the available complete forensic analysis across the applicable audio content | Ordinary full-scan option |
| **Partial analysis — OWNER REQUIREMENT** | Run **selected forensic detectors / measurement groups**, potentially **multiple**. Examples: **MQA detection**, **Dynamic Range** | A multi-select list of supported analysis categories |
| **Duration-limited analysis** | Analyze a prefix, e.g. the first 180 seconds (if supported by the existing engine) | **Separate** duration/coverage option; **never** call this “Partial analysis” |

- “Partial” **does not** mean “Fast,” “first N seconds,” lower result detail, or a shortened scan chosen by the AI.
- A detector selector must only offer **genuinely executable and distinguishable** detectors/groups. Do not fake selective execution by secretly running the whole engine and hiding unwanted outputs.
- **Capability dependency:** Verify whether the Rust core, JNI surface, job API and persisted report can actually run/represent detector-selective analyses. If they cannot, explicitly mark this as an unimplemented functional requirement with a focused follow-up plan; **do not replace it with prefix scanning or present false UI controls**.
- If prefix scanning is currently supported, keep it correctly named **First N seconds / Analyze prefix** as a separate choice, subject to honest reporting of achieved coverage. Its existence never satisfies the detector-selective requirement.
- Preserve full-scope and partial-selective results as distinct saved scopes; indicate skipped/not-run measurements truthfully.

### B. Same-panel workflow

| State | What the lower panel actually shows |
| --- | --- |
| No checked tracks | Short selection prompt; Scan unavailable |
| Ready | Full analysis or **supported selective-analysis choice**, plus clear Scan action; a distinct prefix/coverage control only where relevant |
| Queued | Plain queue status, selected tracks and cancel where supported |
| Running | Selected track/batch progress using genuine available stage data; avoid unexplained codes, fabricated percentages or invented ETAs |
| Completed | **The report appears right here** without routing to history or another page |
| Partially interpretable | Existing findings **and** why the overall interpretation is limited |
| Batch | Compact per-track navigation and individual ready/failed/cancelled states; no invented single verdict for an entire album |
| Failed/cancelled | Plain-language message, relevant next action, optional real diagnostics |
| Saved report | The **same result presentation**, here in the panel, with scope/date; no automatic rescan |

Completion should update the current panel automatically **only if that job is still the user's focus**. If the user switched track, tool or saved report, show nonintrusive completion feedback instead of stealing the view. Job-to-input identity must not depend solely on a row number or duplicated filename.

### C. Report content hierarchy

In its **first visible section**, the report should answer:

1. **What track did I analyze?** Name and clear identification.
2. **What was actually analyzed?** Full run, detector subset and/or actual audio interval. Requested and achieved coverage must not be conflated.
3. **What did Alfred find?** Main evidence-based interpretation and relevant observations in understandable terms.
4. **Why?** Brief supporting evidence with direct expansion paths to the measurements that justify it.
5. **What can't I conclude?** Specific uncertainty, missing inputs and limitations, without burying the actual finding.

The rest of the panel may offer collapsible sections for spectrum/codec evidence, MQA findings, dynamics/loudness, bit-depth/source candidates, file metadata, processing diagnostics and original measurements **only to the extent the stored report truly supports them**. Real units, channel domains, analysis windows and missing states must be preserved. The groups are **examples of organizing existing data**, not promises of detectors not present in the current engine.

**Not the main report:** raw JSON, job UUID, attempt IDs, schema/version numbers, codec-loader admissions, JNI/bootstrap diagnostics, giant caveats or payload-kind inventories. Keep those under advanced details/export when needed.

**Scientific integrity:** Do not manufacture “genuine/fake” certainty or convert an uncalibrated reference score into a probability or audio-quality grade. Native ancestry can be inconclusive even when useful measurements exist. Distinguish `false`, `null`, not-run, not-triggered and unavailable. Do not invent thresholds, or reinterpret reference rules without grounding them in the report version and actual stored evidence.

**Presentation exercise before implementation:** Use a few **existing real** saved reports and construct small static examples for (a) useful positive finding, (b) no relevant candidate flagged, (c) partial/inconclusive report, (d) detector-selective result when technically available, (e) duration-limited result when available, and (f) mixed batch/failure. Show both the compact panel and expanded evidence. **Do not** burn quota generating speculative result examples unsupported by data.

## 6. Existing Spectrogram and Compare: no silent regression

- A06a (Spectrogram) and A06b (Compare) had implementation work already. Verify the **current APK/code paths and real result outputs** before asserting either is now “planned only” or fully functioning.
- Where already supported, keep these tools accessible from the shared workspace, applying existing checked-track requirements. Avoid extra page hopping where the shared lower panel can host the actual operation and output.
- **Spectrogram:** Prioritize the rendered visual/preview and usable export/share controls when present. Keep resolution/export parameters labelled according to what they really control. No giant lists of UUIDs/PNG bytes where the spectrogram should be.
- **Compare:** Present which tracks are being compared, what the supported result actually says and how to inspect details. Don't introduce new similarity metrics or claim unimplemented comparisons.
- Any mismatch between an implemented backend and missing UI entry point is a **navigation regression**, not evidence that the feature is unbuilt.

## 7. Existing settings, report history and UI copy

### User-facing wording

| Avoid in primary UI | Instead |
| --- | --- |
| `document`, `persisted`, `resource admission`, raw UUID | `file`, `saved`, a useful plain-language outcome; IDs in diagnostics only |
| “Header support does not verify decoded PCM” as a permanent banner | Show normal imported track state; explain a real problem when it matters |
| “Analysis complete” alone | Immediately show the resulting findings |
| “Partial analysis = 180 seconds” | Use **detector selection** for Partial Analysis; call 180 seconds **prefix/duration-limited** if separately retained |
| Long repeated disclaimers | One short contextual limitation beside findings, with expanded explanation available |
| “32 tracks maximum” | No arbitrary owner-imposed cap; explain genuine resource limits only after investigation |

- Show **settings that currently do something**. Future Converter and Metadata preferences remain unimplemented, even if their future design has been agreed.
- Keep existing history, reopening saved reports and export/share available. They must open the report in the **same common renderer**; saved results do not become a second inferior JSON-focused UI.
- Errors should differentiate permission loss, unsupported inputs, out-of-storage, resource bounds, cancellation, and report/export failure when supported by actual evidence. Don't promise actions that the condition cannot support.
- Keep the normal flow free of developer commentary. The full original data and genuine diagnostics remain accessible for advanced users.

## 8. Implementation dependency split (do not hide this)

| Work class | Can be solved mainly by UI? | Action before coding |
| --- | --- | --- |
| Sparse Home, two import actions, concise labels | Mostly yes | Confirm picker APIs already support one/many files; confirm existing history route remains reachable |
| Track columns, sorting, column visibility, compact controls | Mostly yes, with source data access | Inventory actually available fields; define no fabricated values |
| Near-full-height resizable split | Mainly layout/gesture implementation | Inspect hard maximum/minimum split constraint and system insets; test real touch |
| Same-panel Forensics completion + report display | Mostly UI/navigation and report mapping | Confirm job lifecycle, saved report loader, and versioned result data paths |
| Human-readable forensic interpretations | UI presentation grounded in stored data | Build mapping ledger using real report schemas and examples |
| Recursive folder import | **No** — acquisition layer may need work | Inspect SAF traversal and grant/lifecycle support; test nested directories and cancellation |
| Remove unexplained 32-track cap | **Not necessarily** — staging/batch/worker/resource constraints | Find actual enforcing layer(s) and their purpose; document large-set strategy; avoid pretending performance is unlimited |
| Detector-selective Partial Analysis | **Potentially no** — engine/JNI/jobs/report schema | Verify execution granularity; record technical gap, don't fake options |
| Existing Spectrogram/Compare restoration | Depends on actual implementation state | Inspect entry points and preserve existing paths/results |

**No blanket engine rewrite is authorized.** Where an owner requirement depends on a missing backend capability, stop at a clear, small dependency statement and obtain direction for that separate work instead of quietly changing the meaning of the requirement.

## 9. Revised acceptance criteria — check on a physical phone

- [ ] Home visually matches the owner's sparse navigation intention; no promotional blocks.
- [ ] Import presents **Files** (one/many) and **Folder**, not three “document” choices.
- [ ] Nested eligible files are included from a selected folder; incomplete scans never falsely claim success. *(Functional dependency.)*
- [ ] No unapproved 32-track UX limit; if a real constraint remains, its source and resolution path are documented. *(Functional dependency.)*
- [ ] Spreadsheet displays current file properties in configurable columns; columns sort by real field values and import order is preserved as baseline.
- [ ] Track table scrolls vertically and horizontally without headings drifting, mangling long names or accidentally changing checkboxes.
- [ ] Drag divider **vertically**; lower panel can expand to **virtually full usable height**, well past the prior ~60%, then restore without losing state.
- [ ] Analysis setup, true processing state and **actual results** remain in the SAME lower panel from start to finish; no required history-page detour.
- [ ] “Partial analysis” means a selectable subset of real detectors/measurement groups; **if not supported yet, show no fake controls and mark the functional acceptance as blocked**.
- [ ] Prefix/duration-limited scanning, if retained, is explicitly distinct and reports actual coverage.
- [ ] A completed report explains findings/evidence/limits without needing JSON or interpreting opaque codes; genuine advanced data stays accessible.
- [ ] Batch result navigation uses current selection/job identity, never silently scanning unchecked tracks.
- [ ] Spectrogram and Compare capabilities already implemented remain discoverable and usable where supported; no mislabelling regressions.
- [ ] Settings/history/export remain functional and user-facing, with diagnostics moved out of the primary flow.
- [ ] Tested on an actual phone including expanded-panel scroll, narrow screen, long filename, large font and active/completed job transitions; record what was **not** tested.

**Acceptance categories:** Mark each line **pass / fail / blocked by functional dependency / not yet tested**. Do not turn “build passed” into “UI accepted.”

## 10. Economical revision sequence

1. **Reconcile and approve this plan** against `Reality_vs_expectation.pdf` and the owner corrections; do not reinterpret Partial Analysis.
2. **Fix the existing UI shell**: sparse home, two-choice import, compact spreadsheet, true near-full-height divider. Split out recursive traversal / capacity code dependencies.
3. **Make Forensics usable**: same-panel job lifecycle and human-readable reports from real saved samples; map uncertainty honestly. Assess detector-selective execution separately.
4. **Preserve/improve existing Spectrogram and Compare access/presentation** based on what is actually implemented.
5. **Targeted build/UI checks and owner device acceptance** at meaningful checkpoints, rather than rebuilding or polling CI for each small copy change.

This is a review/order of work, **not** permission to execute it. No speculative future-feature UI and no routine full-repository audits solely to change wording.

## 11. PARKED: decisions for later features (preserve, do not implement)

### Converter — confirmed future direction

- MediaHuman-inspired **hybrid**: compact target-output preset near the upper spreadsheet toolbar; detailed options, progress and results in the **same resizable lower Converter panel**. Target “Resolution” refers to **output** format/sample rate/bit depth, not source audio quality or image size.
- Conversion applies **only to checked tracks**, never to every loaded row by default; zero checked = cannot convert.
- Persistent **post-conversion spreadsheet behavior**: (A) keep original rows, (B) append converted rows, or (C) replace source rows with the converted ones. **C is default**; replacement of spreadsheet rows does not itself delete physical source files.
- Persistent output/file-handling default: retain originals in place; save converted files to a `Converted/` subfolder of the original directory. Optional future behavior: move selected source or output files to a user-designated folder/subfolder, or request moving originals to Android's system trash *after successful verified conversion*. Never silently permanently delete as fallback.
- Other converter design (name collisions, actual codec/format controls, bulk destination behavior) remains open until its feature work begins.

### Metadata Studio — confirmed future direction

- Mp3tag-style editing of **checked tracks** within Alfred's shared lower panel.
- When selected tracks have an **identical value for a field**, display the shared value. When values **differ**, show Mp3tag-style `<keep>` (meaning: don't change each track's existing value).
- Only fields **explicitly edited** are applied to all checked tracks on save. Merely displaying a common field value must **not** count as a modification.
- Custom tags, standard tags and artwork are part of the later feature design, **not this UI repair**. Format-specific tag writing and save details will be settled later.

### Log/CUE and other future utility panels

- Reserve conceptual room in the shared workspace. Do not build a Log/CUE UI, Converter UI, or Metadata editor yet.

## 12. Decision record

| Date | Decision | Status |
| --- | --- | --- |
| 2026-10-09 | Minimal **home**, not minimal **application**; music spreadsheet plus resizable lower panel is the core | Owner-confirmed |
| 2026-10-09 | Two import choices (one/multiple files; folder); recursive folder handling and no arbitrary 32-track rule | Owner-confirmed; functional dependencies to inspect |
| 2026-10-09 | Track columns show **current** audio characteristics; sortable/customizable, selection by checkbox | Owner-confirmed |
| 2026-10-09 | Lower panel must drag nearly to full usable height, **not** stop at 60% | Owner-confirmed |
| 2026-10-09 | Forensic setup, progress and actual explanatory results stay in one lower panel | Owner-confirmed |
| 2026-10-09 | Partial analysis = selecting specific detectors / measurement groups, NOT duration-limited scan | Owner-confirmed; backend capability TBD |
| 2026-10-09 | Spectrogram and Compare implementations must not silently regress | Established work; current actual state to verify |
| 2026-10-09 | Only fix established features now; Converter, Metadata Editor and Log/CUE remain parked | Owner-confirmed |
| 2026-10-09 | Converter hybrid, checked-only conversion, persistent A/B/C (C default), preserve sources + `Converted/` default | Owner-confirmed, **parked** |
| 2026-10-09 | Mp3tag identical-value / `<keep>` bulk metadata editing | Owner-confirmed, **parked** |

---

**This plan records owner intent.** If code or technical constraints disagree, raise the discrepancy for a focused decision; don't silently replace the intended interaction with whatever component or backend path is easiest to implement.
