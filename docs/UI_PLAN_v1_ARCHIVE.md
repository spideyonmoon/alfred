> Historical draft, superseded by [the finalized owner-v4 plan](UI_PLAN.md). Retained to preserve prior planning work; not current requirements.

# Alfred UI plan — working draft 1

Updated 2026-10-09. **Planning only. Not an approved design or an implementation brief ready to build.**

## 1. What the owner has established

- The latest UI is better and broadly follows the sketch, but presentation is unfinished.
- The top panel does not sufficiently follow `owner-input/UI_v0_1.pdf`.
- The spreadsheet needs deliberate design, not another coat of styling.
- Too much information and poor wording obscure the actual task.
- A completed forensic scan must explain what its findings mean.
- Setup, progress and results belong in the **same lower panel**, with the music table retained above.
- This phase is planning/documentation with several focused revision passes. Conserve usage; do not start another implementation/build cycle.

Those are requirements. Layout measurements, column choices and copy below are proposals for discussion. The existing colors and rounded cards are not implicitly approved.

## 2. Why the current version falls short

| Current element | Problem | Planned correction |
| --- | --- | --- |
| App bar plus Music title plus selection count plus import menu | Stacked chrome spends scarce vertical space | One compact workspace toolbar |
| Resolution hidden in Add music | Wrong context; also confuses image resolution with audio format | Distinguish audio format columns from future image-output settings |
| Fixed 820dp table with wide status and size columns | Most phone space goes to low-value information; useful identity scrolls away | Specify a deliberate column order, widths, pinned identity and details interaction |
| Full reason strings inside rows | Diagnostic prose overwhelms comparison and scanning | Short states; explanation on focus |
| Repeated headings, caveats and planned-tool descriptions | Reading is required before ordinary actions | One clear action and contextual explanations |
| Inline scan setup followed by a history link | The workflow ends before its answer appears | Completion changes the lower panel to the corresponding result |
| A summary string plus Native/Reference status codes | Does not explain evidence, uncertainty or relevance | Structured findings, evidence and limitations |
| Generic payload chips and JSON field browser | User must interpret engine structure | Designed report sections; raw fields remain an advanced fallback |

Code anchors: `WorkspaceScreens.kt` owns the table/panel; `Forensics.kt` currently uses `showHistory=false` for inline setup; `MainActivity.kt` opens a separate history route; `ForensicDocument.kt` extracts only a few summary/status fields; `ResultBrowser.kt` is a generic payload browser. These are observations, not edits requested in this phase.

## 3. Workspace anatomy

```text
Home/back    [workspace context]            Import / More
[select all] [selection count] [contextual toolbar controls]
┌ pinned identity ┬ horizontally scrollable audio columns ┐
│ checkbox / song │ format / scan state / optional fields │
│ ...            │ ...                                  │
└────────────────┴───────────────────────────────────────┘
                       resize handle
Forensic          Metadata
┌ lower panel: setup → progress → result → evidence ─────┐
│ One focused track/result; batch navigation when needed │
└───────────────────────────────────────────────────────┘
```

This describes hierarchy, not final pixels. Avoid solving every element with another card. Use alignment, modest dividers and typography first.

### Top panel: element contract

| Element | Proposed behavior and presentation |
| --- | --- |
| Workspace context | One short name or track count; do not repeat Alfred / Music / selected documents as three headings |
| Home/back | Compact, labelled access; returning does not discard the workspace or stop jobs |
| Import | Explicit Files / Folder choices. Decide replacement versus append before naming the action: current implementation replaces selection, so “Add music” is misleading |
| Select all | Tri-state checkbox: none, some, all. Acts on the current bounded list; announce selection count |
| Selection count | “3 selected”; batch selection is distinct from the focused row |
| Resolution | Sketch placement must be revisited explicitly. Audio sample rate/bit depth are data, not settings. Future image dimensions must be named “Image size” and remain unavailable until Spectrogram is in scope |
| Convert options | Reserve in the planning wireframe as a future capability; do not add an executable control or a large promotional block |
| More | Secondary workspace actions, history and settings; exact contents reviewed in the toolbar pass |
| Loading/error notice | One brief, contextual status. Only blocking conditions occupy persistent space; full diagnostics open on demand |

Proposed phone dimensions to test: toolbar around 48–56dp, column header 40–48dp, rows 48–64dp. These are starting constraints, not fixed-height text boxes. Preserve 48dp touch targets and allow large text to grow. Wider layouts may expose more columns without inventing a different workflow.

### Spreadsheet: behavior before decoration

| Element | Proposed contract |
| --- | --- |
| Identity | Freeze checkbox plus a compact song/name column during horizontal scrolling; verify remaining data width at 320/360dp before committing to pinned widths |
| Name | Prefer reliable title metadata when available, with filename accessible; otherwise filename. Never fabricate title/artist. Full text opens in row details; avoid tiny compressed text |
| Columns | Initial proposal: Song, Format, Sample rate, Bit depth, Scan. Channels, duration and size are optional/detail fields only when actually available. Final column list is an owner decision |
| “Quality” in sketch | Do not equate declared resolution with quality. Decide whether this means scan finding or format information; until decided, use neither a quality grade nor a traffic-light score |
| Formatting | `44.1 kHz`, `24-bit`, `Stereo` only when supported by actual values. Preserve exact values in details. Unknown stays unavailable, not zero. Do not round exact data into a misleading declaration |
| Header | Fixed while vertically scrolling. Clear column boundaries and aligned numeric values. No long explanations in header cells |
| Row selection | Checkbox controls inclusion in the next scan. Tapping the rest of a row focuses its details/result without silently changing batch selection |
| Focus | Visually distinct from checkbox selection and the currently scanning track; all three states may coexist |
| State | Short scan state: Not scanned / Queued / Scanning / Ready / Failed / Cancelled. Ready means report available, not “good audio” |
| Unsupported input | Short unavailable state with a reason in the panel. Header inspection does not imply verified PCM |
| Scrolling | Shared horizontal offset for header and cells; independent vertical table/panel scroll. Subtle overflow cue. No accidental row toggles during drag |
| Ordering | Preserve import order initially. Sorting is a separate proposal; if adopted, it must not change job identity or the submitted batch order |
| Empty / partial list | One empty instruction. An incomplete folder must expose the required subset confirmation without hiding the truncation |
| Row details | Full filename, declarations, size, input issues and applicable saved scans in the lower panel, not an external page |

Divider: visible grip, generous invisible touch area, accessible increase/decrease actions, and usable minimum heights for both panels. Manual split remains stable while data updates. An optional “Expand results” control may enlarge the same panel; it must not navigate elsewhere or permanently collapse the table without user action.

## 4. The complete same-panel workflow

| State | What the lower panel shows | Transition / protection |
| --- | --- | --- |
| No selection | “Select tracks to scan” | No enabled scan action |
| Input checks | Brief checking state, cancel if applicable | No success claim from extension alone |
| Ready | Scope: Whole track / First N seconds; one Scan action | Chosen subset and scope visible before submission |
| Queued | Queue state and cancel | No invented percentage or ETA |
| Running | Focused track, batch position, real stage; cancel | Detailed pass/frame counters are secondary |
| Cancel requested | “Stopping…” with explanation only if needed | Keep work occupied until worker exit |
| Completed | Actual corresponding report appears automatically here | No history detour and no extra tap to discover the answer |
| Partially interpretable | Available findings plus explicit missing interpretation | Successful processing does not imply a complete conclusion |
| Mixed batch | Count ready/failed/cancelled; per-track navigation | Never average results into an album verdict |
| Failed / interrupted | Plain-language reason and useful next action | Retain structured error in details; do not show a forensic conclusion |
| Saved scan | Same report renderer in the same panel | Display saved date and scope; never silently rescan |
| Unknown version | Explain that interpretation is unavailable; retain original export | Do not guess meaning from unfamiliar fields |

Completion should replace progress only when that job is still the user's focus. If they switched track, tab, saved scan or workspace, show a quiet completion indicator instead of stealing focus. Bind UI updates to attempt and input identity, never row number or filename alone. Editing the next selection must not relabel an active scan's inputs.

Batch proposal: table Scan column is the primary navigation; the panel has a compact previous/next selector and current track name. No long list of payload kinds before the report. First automatic result is the focused scanned track, otherwise first available result in submitted order. Failures stay visible and selectable. Retry must identify its scope and use fresh input as required by existing contracts.

History becomes an in-panel saved-scan list, accessible from the workspace menu and Home. From Home without inputs, open the same workspace shell in saved-results mode; do not imply that original audio has been re-imported. Switching saved scans must preserve their own track names and scopes. Back unwinds evidence → result → prior panel state; it never cancels work implicitly.

## 5. Audio Forensics: the report is the primary product

The first screenful should answer: **Which track? What was examined? What did the method find? How much can I conclude from it?** Evidence and original fields follow that answer. A completed process label is not an answer.

Proposed order inside the panel:

1. **Track and coverage.** Name plus actual analyzed interval. Distinguish requested prefix, actual coverage and whole-track completion; do not label a short/unknown decode “whole track” from the request alone.
2. **Main finding.** Faithful, qualified interpretation from the saved reference assessment. Complete / partial / unavailable refers to interpretation availability, not authenticity.
3. **What supports it.** A short, deterministic set of relevant saved observations/candidates, each with a plain explanation and evidence link. Missing/conflicting evidence is adjacent, not buried.
4. **What this does not establish.** Specific limitation where relevant. One compact general qualification; no repeated warning paragraphs.
5. **Details.** Sections for source-history candidates, depth candidates, measured signal properties, file metadata, and method/diagnostics. Hide empty optional sections but expose why an expected finding is unavailable.
6. **Actions.** Export / Share / Scan again where applicable. “Original JSON” is an explicit export format, not the main report presentation. Delete belongs under secondary actions.

### Data-to-presentation contract

Grounding: pinned core `docs/PRODUCT_REPORT.md`, `docs/validation/REFERENCE_ASSESSMENT_VALIDATION.md` and `src/reference_assessment.rs`, inspected read-only. No engine changes or copied fixtures.

| Saved source | User-facing role | Required guard |
| --- | --- | --- |
| `reference_assessment.status`, `display_summary` | Main interpretation and availability | Match supported method/version; retain qualification; unknown status has a fallback |
| `source_candidates`, `depth_candidates` | Candidate findings with explanations | `matched=true`, `false`, and null are distinct. False is not proof against a source; null is unavailable |
| Candidate `rule_ids`, assessment `rules` and `features` | “Why this finding?” evidence | Only link actual supporting rules/features. Respect not-triggered, vetoed and unavailable states; no UI re-scoring |
| Feature value, unit, domain, channels, intervals, caveats | Measurement row and meaning | Preserve units, channel/domain distinctions and measured scope. No invented “normal” ranges |
| `missing_inputs`, `caveats`, `deviations` | Explanation of uncertainty | Partial results cannot be promoted to a complete verdict by hiding missing inputs |
| `scores`, `reference_label` | Optional method details | Uncalibrated reference values; never probability, sound-quality grade or authentic/fake badge |
| `measurement_report` | Native observations, status and coverage | Native ancestry remains INCONCLUSIVE and evidence index unavailable; explain this separately from reference candidates |
| `metadata` | File/container declarations and tags | Declared rate/depth do not prove recording history or effective information content |
| Optional byproducts/tool statistics | Further signal details, if present | Inspect exact schemas before mapping; absent stays unavailable |
| Legacy outputs | Advanced audit only | Never elevate historical certainty wording to the headline |
| Host outcomes / artifact diagnostics | Processing/export issues | Distinguish analysis failure, partial interpretation and artifact/export failure |
| Original payload | Full technical inspection/export | Preserve all fields, large integers, nulls and original bytes |

Before implementation, create a small **presentation mapping ledger**: stable field/candidate ID → actual supported values → label → explanation → source/unit/scope → unavailable wording → evidence link → example state. Inventory every supported reference summary/status and candidate ID then. This draft deliberately does not invent exhaustive mappings from uninspected fields.

Explanation copy must be deterministic and version-aware. No runtime LLM, network upload, new threshold or invented detector. If the saved data does not support a useful explanation, say what is unavailable instead of manufacturing a confident story.

### Report drafts to review before building

Produce static, clearly labelled examples from existing generated/saved data for:

- Complete interpretation with a positive candidate and its supporting evidence.
- Complete interpretation with no relevant candidate flagged; no “authentic” conclusion.
- Partial interpretation with useful candidates but missing composite scores.
- Inconclusive/unavailable interpretation despite successful processing.
- Prefix result, shorter actual coverage, and channel-dependent observations.
- Mixed batch with success, unsupported file, cancellation and failed scan.
- Unknown saved version and export failure with the valid local report retained.

For every example, write the collapsed panel first, then expanded evidence. Review whether a person can explain the result without opening JSON. Avoid spending usage generating dozens of cosmetic variants.

## 6. Language and information budget

| Current language / pattern | Draft replacement or placement |
| --- | --- |
| “Your music. In detail.”, “Explore your audio” | Remove decorative copy if it competes with the task |
| “3 of 10 selected documents” | “3 selected” with total in table context |
| “Acquisition and codec verification pending” | “Checking files…” |
| “Analyze full scope” | “Scan” with “Whole track” visibly selected |
| “Analyze selected prefix” | “Scan” with “First 30 seconds” visibly selected |
| “Native: … · Reference: …” | Named finding and brief availability explanation; raw statuses in details |
| “Partial” without explanation | “Some findings are unavailable” plus the actual missing reason |
| “No candidate matched” | “No [specific] indication was flagged by this method”; not proof of absence |
| “resource_limit” | “This scan exceeds the available resources” plus a valid next action; raw code in details |
| Attempt IDs / schema names / hashes | Technical details, not row titles or success messages |
| Repeated global disclaimers | One local qualification near interpretation; method details expandable |
| “Coming later” blocks | Quiet unavailable labels only where useful to the agreed layout |

These are drafts, not universal string replacements. Each error needs its own applicable action: shorter scan only when valid, reselect for lost access, free storage for storage failure. Do not promise recovery that the condition does not support.

Default information budget: one primary action per state; no explanatory paragraph in ordinary table rows; one compact finding block before evidence; no giant score gauge. Do not truncate critical limitations or trade away accessibility merely to meet a line count.

## 7. Other surfaces and shared behavior

| Surface / element | Planning requirement |
| --- | --- |
| Home | Minimal entry into file/folder selection; saved scans and resume available without promotional text |
| Import chooser | Consistent “File / Files / Folder” vocabulary; cancel is harmless; state whether selection is replaced |
| Metadata tab | Remains a small planned shell; existing completed-report metadata remains readable |
| Spectrogram / Compare | Remain planned; no new execution, conversion, log/cue or PNG-export scope |
| Settings | Appearance, notifications and necessary storage/help; native bootstrap and engineering limits under diagnostics |
| Notifications | Honest running/cancel state; no percentage or conclusion that the engine does not provide |
| Export/share | Context identifies track and report; failure keeps local results. No private audio upload |
| Deletion | Identify the saved report; do not conflate report deletion with source deletion |
| Accessibility | Logical focus, spoken checkbox/focus/scan states, non-color status cues, large text, labelled icons and divider controls |
| Persistence | Rotation, tab switches and backgrounding preserve focus and split; process restart reports interrupted work honestly |
| Layout extremes | Narrow phone, landscape, large text, long/duplicate/non-Latin names, 32 tracks, missing metadata and long findings |

## 8. Revision sequence and acceptance

Work one packet at a time. Update this document and a short decision log after each review; do not rewrite the whole plan or rebuild the app for a copy change.

| Pass | Reviewable output | Completion test |
| --- | --- | --- |
| P1 — structure | Annotated top toolbar/table sketch; explicit columns and row interactions | Owner recognizes the intended top panel; ambiguity around Resolution/Quality resolved |
| P2 — result meaning | Data mapping ledger and the representative report drafts above | Every finding has provenance, meaning, scope and honest uncertainty |
| P3 — workflow | State transitions for one track, batch, saved results and failure | No forced result page/history detour; completion never steals unrelated focus |
| P4 — copy and density | Screen-by-screen string inventory with keep/remove/rewrite decisions | No diagnostic prose in primary flow; consistent action vocabulary |
| P5 — visual specification | Agreed spacing/type/column behavior and key narrow-phone layouts | Table and report remain usable with large text and long content |
| P6 — implementation packet | Small ordered code tasks and focused UI acceptance scenarios | Only then seek implementation direction; no open core semantic assumptions |

Future implementation order: same-panel result state/identity → designed report mapping → toolbar/table → remaining copy/styles → targeted tests. Check frozen headers, select-all mixed state, focus versus selection, divider gestures, automatic completion, partial/failed results, original export, rotation and unknown versions. Use existing generated fixtures in place; do not copy engine fixtures. Build only after a coherent approved implementation slice; reserve heavy Android validation for an agreed checkpoint. Physical acceptance remains separate.

### Decisions for the next planning conversation

1. Exact top toolbar arrangement: which sketch controls should remain visible now, and what did “Resolution” mean there?
2. Spreadsheet default columns, and whether “quality” meant a scan finding rather than format information.
3. Result emphasis and vocabulary: review concrete report drafts before choosing how strong/technical the headline should be.

These are unresolved design choices, not a demand that the owner write a specification. Bring small alternatives and concrete examples to the next pass. Do not repeat already settled requirements, especially same-panel results.

## 9. Current status and economical continuation

This turn produced the first planning baseline from the owner's feedback, existing sketch context and a focused read-only source/contract review. It is not the completed multi-pass design. No code, dependency, APK, build, CI, engine or visual artifact was changed/generated for this planning turn.

Next useful work: P1 toolbar/table alternatives and P2 representative result copy. Read this plan and relevant sources only; avoid another full-repository audit, sub-agents, speculative feature work or implementation until requested. Preserve the existing uncommitted redesign work. Record subsequent decisions below.

| Date | Decision | Status |
| --- | --- | --- |
| 2026-10-09 | Same lower panel must own scan setup, progress and actual results | Owner requirement |
| 2026-10-09 | Current top panel, spreadsheet and forensic presentation remain unfinished | Owner feedback |
| 2026-10-09 | Planning/documentation only; conserve usage | Current task boundary |
| 2026-10-09 | Layout, column and copy proposals in this draft | Pending refinement |
