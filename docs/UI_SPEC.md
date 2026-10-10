# U1: workspace and report specification

## Current status — Android implementation, 2026-10-10

The owner approved revision 4 with “this is good. implement it”. Native Android
implementation is now recorded in [UI_REPORT_ANDROID](../task-results/UI_REPORT_ANDROID.md).
The HTML stays revision 4. Earlier approval-pending/HTML-only statements below are
historical. Offline build, lint and actual-data model checks pass; rendered/device
acceptance remains open. See that record for exact validation and limitations.

**2026-10-10 report addendum:** the new [report refinement plan](UI_REPORT_REFINEMENT_PLAN.md)
and [evidence ledger](UI_REPORT_EVIDENCE.md) govern report presentation. Preserve
the workspace below. Replace automatic JSON/predicate/frame-counter disclosures
with human-readable investigation; put reproduction data behind explicit access.
Show the stored reference score with calibration/source-history qualifications
and separate native INCONCLUSIVE. Revision 4 is implemented in the HTML;
[the review record](UI_REPORT_REVIEW.md) documents the complete report, component
contracts and validation limits. The 16-case rendered matrix remains blocked. The original brief/screenshots are linked
from the plan. Owner review is required before Android implementation.

Workspace baseline: 2026-10-09, revision 3; report revision 4: 2026-10-10. The owner's U1 corrections supersede conflicting v4 and
first-prototype assumptions. **Documentation/prototype revision only; no Android
implementation yet.** Review [the prototype](ui-review.html) alongside this spec.
It demonstrates proposed interaction, not Android runtime or physical acceptance.
The missing owner PDFs remain unreviewed.

## Owner-directed structure and remaining proposals

| Area | Current decision | Boundary |
| --- | --- | --- |
| Home | Alfred, Select File / Folder, Settings | Preserve sparse home and existing secondary saved-result access |
| Workspace toolbar | Back arrow, X selected, small downward chevron, right-aligned staggered menu icon | No Music heading; no permanent selection/Columns bar |
| Expandable quick row | Floating card styling in normal layout flow below primary toolbar | Pushes spreadsheet down; collapse restores space; prototype-only future preset/Convert specimens |
| Select All | Tri-state checkbox in spreadsheet header | Shared checked set across operations, independent of visible rows |
| Overflow | Columns, Clear, Import; retain existing secondary actions | Clear removes workspace entries, never physical files |
| Import | Files (single/multiple) and Folder (recursive target) | Do not assume replacement; membership behavior remains to be resolved |
| Main categories | Forensic, Metadata, Log, Convert, in that order | No fabricated interfaces for unfinished categories |
| Forensic area | Separate Audio Forensics, Spectrogram and Compare buttons; no dropdown | Existing capabilities remain within this category and this sheet |
| Lower surface | Persistent bottom-anchored sheet overlays spreadsheet | Compact to virtually full workspace height below toolbar; simple handle only |
| Scan setup | Concise Scan action using checked tracks | Remove Duration coverage dropdown; shelve detector-selective partial analysis |
| Results | Actual findings and evidence inside the same sheet | No new page, forced history detour or focus theft |
| Table | Native swipe scrolling; sortable, configurable, individually resizable columns | Chosen widths persist; no permanent scrollbar controls |
| Future conversion | Target output-preset dropdown + Convert button in expandable quick row | Detailed controls in future Convert section; no Android implementation |
| Presentation | Actual information and results first | No duplicated headings, decorative explanations, primary engineering diagnostics or oversized controls |

Remaining proposals: default columns Track, Artist, Album, Format, Rate, Bit depth;
optional Filename, Channels, Duration, Bitrate and Size. Freeze the checkbox only
to avoid consuming the narrow viewport. Sort cycle: ascending, descending, import
order; stable import ordinal resolves ties, missing values last in both directions.
These details are not new owner approvals. Retain the app theme rather than
introducing a new branding pass.

## Compact and expanded bottom sheet

```text
NORMAL                               FULLY EXPANDED
←  3 selected             ⌄  ≡       ←  3 selected             ⌄  ≡
[optional quick row, in flow]         [optional quick row, in flow]
[–] Track  Artist Album Format →      ┌────────── handle ─────────────┐
[✓] …                                │Forensic Metadata Log Convert │
[ ] …                                │Audio Forensics Spectrogram Compare
… native spreadsheet scrolling …     │track / coverage              │
┌────────── handle ─────────────┐    │qualified finding + limits    │
│Forensic Metadata Log Convert │    │measurements / evidence       │
│Audio Forensics Spectrogram Compare │channel / method details      │
│track / coverage / finding    │    │original fields on demand     │
└──────────────────────────────┘    └──────────────────────────────┘
```

The diagram's menu glyph is schematic. The actual icon has three horizontal
lines sharing a right edge, progressively shorter downward (24 px SVG paths
M3 6h18, M8 12h13, M13 18h8). It is not an ellipsis or equal-width hamburger.


The spreadsheet retains its geometry behind the sheet while the sheet moves.
Opening the quick row independently reduces the available workspace height. Sheet motion never
allocates two split panes. The sheet is persistent, anchored at the bottom of the
Music workspace and nonmodal: exposed rows remain interactive without a scrim.
Its maximum is the measured workspace below the primary toolbar, any open quick
row, and system insets. The quick row never covers the spreadsheet or report.
Its compact extent accommodates handle, category strip and current task context.
There is no arbitrary percentage ceiling and no permanently reserved table pane.

Use a simple drag handle: no separate divider, slider, Expand tools or Restore
split buttons. Ordinary swipe gestures scroll table or report independently;
handle dragging changes sheet extent. A small visible handle may have a larger
invisible hit area. Platform accessibility actions belong on the handle, not in
extra visible instructional controls. Large text can horizontally scroll the
category strip. Do not enlarge the primary chrome to explain these interactions.

Preserve extent, table scroll, shared checked set, focus, category, forensic tool
and per-result evidence expansion/scroll across job updates, tool changes and
rotation. Recompute measured bounds for insets/font changes without resetting
the user's context. Near-full expansion covers the spreadsheet, not the toolbar.

## Home and expandable toolbar

Preserve minimal Home: Alfred, Select File / Folder, Settings and the existing
secondary saved-result access. Workspace back goes Home, retaining loaded files,
checkbox selection, table scroll/widths, tool/report context and ongoing jobs.
It does not clear the workspace, cancel work, or open an exit confirmation.
The prototype includes a Return to workspace action when a collection exists;
this resumes retained state without pretending to import another collection.

The primary toolbar always keeps `X selected`. At the right, the small chevron
precedes the staggered workspace-menu icon. Chevron toggles one floating-styled
card row in normal flex layout, not absolute positioning. It pushes the table
header and workspace downward; collapse returns exactly that space. No selection
or Columns bar is added. Menu actions remain Columns, Clear and Import.

The quick row reserves one output-preset dropdown and one Convert button, plus
future room for quick controls. This HTML revision uses noninteractive visual
specimens labelled “Layout preview · Converter planned”; they are not keyboard
targets or executable features. Android gets no fake converter UI. Future detail
controls/progress/results belong in Convert. Expansion state survives Home/return.

Audio Forensics, Spectrogram and Compare are direct buttons under Forensic,
with a visible and accessible selected state. Switching them replaces content
inside the persistent sheet and retains per-tool scroll/evidence state.

## Spreadsheet data, sizing and identity

`InputItem.id` identifies a row, focused item and checkbox membership; import
ordinal is separate. Duplicate filenames never act as identity. For this revision,
row/title tapping focuses details and the checkbox changes shared operation
selection. The deferred artwork/row-to-select interaction is not implemented.
Select-all belongs to the spreadsheet header, covers the collection, and shows
none/some/all. Sorting/columns do not change checked membership or frozen jobs.

| Column | Source available now | Sort / missing behavior |
| --- | --- | --- |
| Track | MetadataReport v1 named title entry; fallback InputItem.name | Text; retain complete filename and explain fallback in details |
| Artist / Album | MetadataReport v1 named entry indices into retained entries | Text; absent remains unavailable, no invented tag |
| Filename / Size | InputItem.name / nullable declaredBytes | Text / exact integer bytes; size is declared |
| Format / Rate / Bit depth / Channels | ProbeCapability and typed metadata declarations | Text / numeric, never lexically sort formatted kHz strings |
| Duration | metadata.technical.declared_duration_seconds | Numeric seconds; not achieved analysis interval |
| Bitrate | metadata.technical.declared_bit_rate_bps | Numeric bps; do not silently substitute derived PCM bitrate |

ProbeCapability does not currently contain tags, duration or bitrate. U3 needs a
small typed projection during existing off-main metadata reads, cached by item
and generation. Never parse full maps during recomposition or retain all payloads.
Named tags use the first retained entry; duplicate/truncated/original values remain
inspectable. Metadata projection failure must not change codec admission.

Use a virtualized vertical list and a shared horizontal offset for headers/cells.
Native horizontal and vertical swipes are the ordinary scrolling interaction;
no permanent scrollbar widgets/buttons. Drag a header edge to resize that column;
tap the header to sort. Resize must not sort, check a row or resize adjacent columns.
Persist widths by stable column ID, separately from visibility/order, across
hide/show, sorting, navigation and app restoration. Keep a readable minimum.
Proposed starting widths in dp: checkbox 48, Track 180, Artist/Album 140, Format 88,
Rate 96, Bit depth 88. Trial widths are not owner phone acceptance. Large text can
grow rows; do not shrink text/touch targets. Full names remain accessible on focus.

## Overflow, importing and clearing

Columns configures available properties. Import opens exactly Files and Folder;
Files handles one/many and Folder targets recursive discovery. Current Android
first-level/32-item behavior remains a known dependency, not prototype evidence.
Do not label the action Replace files or implement unconditional replacement.
Append/merge versus explicitly chosen replacement, duplicate-identity handling and
grant ownership must be resolved with F1 before functional import changes.

Clear removes collection entries and selection, never source files, saved reports
or already-submitted job membership. Active work retains its frozen identities and
leases. A source-details view for a removed item must stop referring to a live row;
an already committed report remains openable. No new navigation is required.
The prototype Clear acts only on its illustrative in-memory rows. Its import
choices explain no fake successful import and do not simulate acquisition.

## Category hierarchy and same-sheet lifecycle

Forensic, Metadata, Log, Convert are primary categories. Only Forensic exposes
current capability content here. Preserve the other category positions without
building editors, conversion forms, Log tools, promotional placeholder pages or
nonfunctional task actions. Audio Forensics, Spectrogram and Compare are inside
Forensic. Report metadata evidence is not the future Metadata editor.

Remove Duration coverage. Detector-selective Partial Analysis is **shelved**;
F3 is retained assessment history, not an active UI completion gate. Do not
replace the removed control with another prefix/coverage selector. Existing scope
data/backends remain intact: saved prefix reports still show requested versus
achieved coverage, and admission failures stay honest. Existing Spectrogram and
Compare scope/compatibility behavior must be preserved within those workflows.

Persist a sheet target separately from checked rows:
`Setup(selectionId)` / `Live(jobId, attemptId, itemId?)` /
`Saved(attemptId, itemId?, descriptorPath)` / `SourceDetails(itemId)`.
Category and forensic subtool are separate state. A saved report needs no live row.

| State/event | Sheet content | Integrity condition |
| --- | --- | --- |
| No checked tracks | Short selection prompt, unavailable Scan | Focus alone never changes current checked set |
| Ready | Compact Scan action | No duration dropdown or detector selector |
| Queued | Real queue status and supported Cancel | Freeze item IDs/options and bind returned job/attempt |
| Acquiring / running | Actual item/phase/pass and available frame progress | No fabricated overall percentage or ETA |
| Cancellation requested | Stopping… and last actual progress | Worker remains busy until release |
| Finalizing | Saving results… | Decode end is not committed completion |
| Committed completion | Real finding, evidence and limits in this sheet | Auto-open only while category/tool/attempt still match |
| Focus/category/tool changed | Keep new target; nonintrusive completion status | No late callback or result load may steal focus |
| Mixed batch | Per-item outcomes and navigation | No album verdict or unchecked-track inclusion |
| Cancelled / interrupted | Available committed items plus unfinished outcomes | Retry explicit, fresh attempt; never automatic |
| Failed | Recorded cause and appropriate next action | Host error is not a detector finding |
| Saved | Same result renderer, recorded scope/date | No source acquisition or rescan |
| Unknown/missing payload | Honest unreadable/unsupported state and available export | Never invent a report or parse a future version as current |

The first result section answers identity, actual coverage, finding, evidence and
specific limits. Do not repeat category/tool/page headings or surround results
with decorative explanation. UUIDs, engineering diagnostics and original fields
remain in advanced details; exact original bytes remain exportable.

Reuse result pins/hash/size/version validation and off-main loading of one payload.
The loader carries the full target key; discard results for obsolete targets.
Extract externally targeted result loading/body from the history-list shell rather
than duplicating ResultBrowser in every feature. Resolve item/payload association
through persisted IDs and manifest outcomes, not descriptor index alone. Evidence
expansion/scroll keys include attempt, item and field path.

## Versioned report and evidence ledger

Envelope gate: `audio-forensic-product-v1`. Native measurement gate: schema
`0.18.0`, policy `observations-only-v18`. Reference gate: assessment 1, contract 1,
method `python-reference-c6ecce2-v1`. Tool statistics gate: statistics 1, contract
1, method `ffmpeg-7.1.1-sox-14.4.2-v1`. Metadata gate: metadata 1, contract 1.
Recognize each component independently: an unknown reference method must not
hide otherwise supported source/coverage fields or become a trusted headline.

Paths below are product-relative. These mappings were checked against the pinned
core types and alias map, not inferred from the current thin ForensicDocument UI.

| Field / ID | Supported presentation | Unit / domain / coverage | Missing wording and evidence destination |
| --- | --- | --- | --- |
| `/measurement_report/source` | Track analyzed, alongside persisted item filename | Text; display identity only | Source unavailable; original field |
| `/measurement_report/status` | Analysis outcome | Native file status, distinct from host job status | Outcome unavailable; diagnostics |
| `/measurement_report/coverage` | Requested Full or First N seconds; actual start/end; reached end | Seconds, exact frames, native PCM | Coverage unavailable; all coverage fields and verification/hash |
| `/reference_assessment/display_summary` | Primary qualified finding, verbatim when method is supported | Stored uncalibrated interpretation | Reference interpretation unavailable/unsupported; method details |
| `/reference_assessment/source_candidates[]` | Show matched candidate's stored display | Candidate, not proven source history | matched false: Not indicated by this rule; null: Unavailable; `rule_ids` link to rules |
| `cassette` / `vinyl` | Stored candidate display | Reference method | R15/R16; R21/R22 respectively |
| `bandwidth` / `resampling` | Stored candidate display | Reference method | R17; R10 respectively; no original-rate claim |
| `independent_hf` | Stored candidate display | Reference method | R28; added noise is only one possible explanation |
| `/reference_assessment/depth_candidates[]` | Stored exercised-bit candidate and limitation | Bits, candidate-specific channel evidence | No source-depth assertion when missing; follow stored rule IDs |
| `/reference_assessment/rules[]` | Why this observation appears | Rule state/predicate/effect, original operands | Not triggered is distinct from unavailable/vetoed; link `input_feature_ids` |
| `/reference_assessment/features/{id}` | Value, unit, algorithm and availability | Stored domain/channel_indices/intervals/sample_rate | Use status/caveats; never invent common scope or channel averaging |
| `/reference_assessment/missing_inputs` | Specific reason interpretation is partial | Required feature IDs | No list does not prove complete; check assessment status |
| `/reference_assessment/scores` | Revision 4: qualified main score in overview; contribution trail in Evidence explorer | Uncalibrated method points, not detector counts | Null means unavailable; never show a probability/quality gauge |
| `/measurement_report/mqa` | Signalling observations; status and candidate details | Native integer PCM, scanned_frames; bit-plane/frame indices | Null/unsupported distinct from not_detected; candidate is not MQA authentication |
| `/metadata/observations/mqa_metadata_claimed` | Editable metadata claims MQA | Metadata declaration | Unknown stays unknown; never substitutes for signalling evidence |
| `/tool_statistics/measurements/dr/overall` | Overall measured DR, stored display/value | ToolValue unit + DR input_domain; tool coverage/block/tail fields | Honor availability/reason; no Android-computed average |
| `/tool_statistics/measurements/dr/channels[]` | Per-channel DR | Channel index + numeric_dr ToolValue | Missing not zero; retain channel distinctions |
| `dr/overall_integer_label` / `dr/legacy_python_label` | Overall label / legacy reference label, separately | Different selection/rounding conventions | Never substitute legacy first matching channel for overall DR |
| `/metadata/technical`, `/metadata/entries` | File properties / read-only tags | Declarations; metadata covers source, not just prefix | Preserve unavailable_fields, duplicates and text_limits |
| `/measurement_report/diagnostics` | Reason-specific limitation/error | Measurement status, not a new verdict | Keep original code/message under details |
| Spectrogram/artifact descriptor | Preview/export availability separate from findings | Existing bound artifact and original PNG | PNG failure does not turn valid measurements into failed analysis |

Native ancestry remains INCONCLUSIVE and its evidence index remains null. Do not
promote legacy `GENUINE` wording to an authenticity headline. Preserve every
original integer token (including u64 maximum), false and null in advanced data;
display formatting never modifies export bytes. Evidence expansion navigates
candidate → stored rules → stored features → original field path. No new rules,
thresholds, summary score, runtime LLM or recalculation in Alfred.

## Report specimens and honest evidence status

Revision 3 runs the pinned 0.32.0 desktop CLI on the owner's reference audio.
The prototype now contains a complete real successful report for ABBA's
“Money, Money, Money”, plus a real resource-limit example. Read the
[sample receipt, values, field mapping and presentation](UI_REPORT_SAMPLE.md).
The original JSON and hashes remain locally retained; no audio/source/fixture copy.

Normal extent: identity, achieved interval, qualified reference finding and three
measured dynamics cells. Expanded extent: the same report reveals organized peak,
bandwidth, bit-depth and MQA observations, with expandable channel/rule/feature
evidence, precise detector coverage, method limits and original fields. No score
gauge, provenance certainty, fabricated waveform or separate report page.

The frequency band has an explicit Hz scale; it is not a quality meter. Peak
estimates above 0 dBTP are not labelled clipping. MQA non-detection remains scoped
to three seconds. Reference depth is not original source depth. Missing values,
candidate false/null and native INCONCLUSIVE remain distinct.

One successful specimen and two unsuccessful source trials are recorded. Positive
flagged, prefix, partial-availability, mixed-batch and unknown-version cases still
need complete acceptance specimens. Detector selection stays shelved. U1 remains
drafted pending owner visual review; a real scan is not Android runtime acceptance.

## Implementation and validation handoff

No Android implementation is authorized by this revision. When later directed:

1. U2: sparse Home, compact primary toolbar and expandable quick row; preserve
   files/jobs on Home return and picker/grants; separate Clear from Import and
   resolve import membership semantics before functional changes.
2. U3: typed source projection, real sorting, configurable/resizable columns,
   persisted widths, header select-all, swipe scrolling and shared selection.
3. U4: persistent overlaid bottom sheet; measured extremes, simple handle,
   accessibility semantics and category hierarchy/state preservation.
4. U5/U6: same-sheet lifecycle and common version-gated report renderer; validate
   actual pinned complete/prefix/partial/mixed/failed report specimens and stale loads.
5. U7: retained Spectrogram/Compare inside Forensic, including live/saved output,
   one-track Spectrogram and same-track 2–32 Compare rules. No new DSP.
6. U8/U9: secondary flows, accessibility and focused integrated checks followed by
   actual phone review. [Acceptance ledger](UI_ACCEPTANCE.md).

## Deferred decisions preserved

Converter uses the MediaHuman-style arrangement: one target output-preset dropdown
and one Convert button in the expandable row below the primary toolbar; detailed
controls belong in the future Convert section. Show their noninteractive layout
only in the prototype quick row, clearly marked as a future layout preview;
no Android conversion controls yet.
Preserve checked-only conversion, persistent post-conversion A/B/C behavior (C
default), original-file retention and Converted/ default destination from owner v4.

Metadata editing retains identical-value / `<keep>` behavior for checked tracks;
only explicitly edited fields are applied. Log/CUE remains parked.

Deferred QoL, **do not implement**: unchecked track displays its embedded cover-art
thumbnail in the checkbox position. Tapping artwork or row selects the track and
shows the checked checkbox; unchecking restores artwork. No additional column.
Reconcile focus behavior only when this QoL is activated; do not change today's
row-focus/checkbox behavior or invent a missing-artwork policy now.

## Revision 4 report components and presentation

The report uses scoped neutral light/dark tokens, tabular numbers and original
24-unit outline icons. Metric tiles, paired channel cards, same-unit cutoff
position scales, named readings, criterion/outcome findings and keyed nested
disclosures form one continuous scrollable investigation. At large text, tiles
and channels stack; the narrow assessment flows to a separate score row. Theme,
viewport and text selectors remain review-only controls outside the phone.

Overview order: identity; qualified assessment/score and native ancestry; stored
DR label, integrated LUFS and LRA; frequency position; estimated channel peaks;
codec/source fingerprints; declared/reference exercised bits. Stored significant
findings get priority links and move their investigation section earlier. The
default section order is Spectral, Dynamics, Codec, Bit-depth, Evidence.

All five sections include meaningful measurements, time scopes and explanatory
criteria. Evidence has all 34 version-bound rule translations and a recorded
before/after score trail. Exact technical projection and predicates are displayed
only after an explicit inspection action in Technical data; the unchanged JSON
download uses the retained original file. No projection is serialized as an
original export. See the review record for content inventory and limitations.

Product/component versions gate interpretation independently. Feature version,
status and null values are respected; unknown reference output retains supported
native measurements without a translated reference verdict. Positive/missing
branches have model checks only, not real audio or visual acceptance.

Workspace handlers and DOM remain the established revision-3 behavior. Report
disclosures use stable section keys and the existing per-tool/focused-result
scroll state. No Android lifecycle claim follows from the HTML model checks.
