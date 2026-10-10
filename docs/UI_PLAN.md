# Alfred UI plan — owner-v4 baseline with U1 corrections

**2026-10-10 next refinement:** [restartable report plan](UI_REPORT_REFINEMENT_PLAN.md)
records the owner's full presentation brief, preserved screenshots, component
contracts, evidence mapping and FR0–FR6 subchunks. This supersedes conflicting
revision-3 report presentation choices only; the established workspace is fixed.
Current checkpoint is documentation/planning; HTML revision 4 is not implemented.

2026-10-09, revision 3. **Documentation/prototype revision only; no Android
implementation yet.** The owner's latest U1 corrections govern conflicts with the
original [unchanged v4 input](../owner-input/UI_PLAN_v4.md), the earlier prototype,
older plan text and historical handoff/README descriptions. Engine, storage,
fidelity and release contracts remain unchanged. Missing reference PDFs were not
reviewed. [Draft 1 archive](UI_PLAN_v1_ARCHIVE.md) remains historical context.

Read [UI_SPEC](UI_SPEC.md) for concrete layout, field mapping and state behavior,
[work chunks](UI_WORK_CHUNKS.md) for execution dependencies,
[prototype](ui-review.html) for review, and [acceptance](UI_ACCEPTANCE.md) for status.

## Governing workspace design

Alfred remains an information-dense, file-first shared audio workspace. Sparse
Home leads to a spreadsheet, with a **persistent expandable bottom sheet** anchored
to the workspace bottom. It expands **over** the spreadsheet from compact to
virtually full-screen below the toolbar. This supersedes the earlier split-pane
requirement. Use a simple handle, no extra divider, slider, Expand tools or Restore
split buttons, and no artificial 60% ceiling. Preserve context while moving it.

Use **one compact primary workspace toolbar**: back arrow to minimal Home,
`X selected`, small downward chevron, then a mirrored staggered menu icon
(three right-aligned lines progressively shorter downward). Remove Music heading.
Home preserves loaded files and ongoing work. Chevron opens a floating-styled
quick row beneath the toolbar in normal layout flow: it pushes the spreadsheet
down and collapsing restores space. It reserves the future preset dropdown and
Convert button; this prototype may show labelled noninteractive layout specimens.
No unfinished Android functionality is authorized. Select All belongs in the spreadsheet's
checkbox header, not a second toolbar. The workspace action menu includes
Columns, Clear and Import. Clear removes workspace entries, not storage files.
Import offers Files (single/multiple) and Folder (recursive). Do not assume that
importing always replaces the collection; resolve membership semantics before
functional changes. Keep existing saved results/settings reachable through
secondary actions without adding navigation to explain ordinary gestures.

The spreadsheet uses native horizontal/vertical swipe scrolling without permanent
scrollbar controls. Columns are sortable, configurable and individually resizable;
chosen widths persist. Headers and values remain aligned. Checkbox selection is
shared across operations. Preserve stable identities, complete filename access,
real source-property sorting, import order, numeric/null distinctions and frozen
submitted membership. Source properties are not authenticity or sound-quality grades.

## Categories and current tools

Main categories remain **Forensic, Metadata, Log, Convert**. Audio Forensics,
Spectrogram and Compare are existing capabilities **inside Forensic**, not
replacement main categories. Show them as three direct buttons, not a dropdown. Preserve their workflows/results/export. Do not
build interfaces for unfinished Metadata, Log or Convert features, or fill them
with promotional placeholders.

Actual forensic setup, processing, findings and evidence stay inside the same
bottom sheet; no separate result page or required history detour. Show committed
completion automatically only if that attempt/tool remains focused. Saved reports
use the same renderer, without a rescan. Mixed batches remain per-item outcomes.

Remove the **Duration coverage dropdown**. **Shelve detector-selective partial
analysis for now.** Do not create a replacement prefix selector or fake selective
execution. Retain backend and saved scope data, and accurately display actual
coverage in results; prefix is never relabelled detector-selective analysis.
F3's unsupported-at-pin assessment remains documented for later, not an active
UI acceptance blocker. Existing Spectrogram/Compare compatibility and scope rules
remain intact.

## Reports and presentation

Prioritize actual information: track, actual coverage, stored qualified finding,
supporting evidence and specific limits. Remove duplicated headings, decorative
explanations, primary engineering diagnostics and unnecessarily large controls.
Advanced originals/diagnostics remain inspectable and original bytes exportable.
Use deterministic version-supported mapping, no new algorithms, thresholds,
runtime LLM, calibrated scores or authenticity guarantees. Preserve false/null,
unavailable and not-triggered distinctions, channel/window domains and native
INCONCLUSIVE ancestry. See the source-grounded mapping ledger in UI_SPEC.

Revision 3 includes a real complete pinned-engine report and actual failure
examples; see [sample evidence](UI_REPORT_SAMPLE.md). Broader U6 specimens and
owner visual review remain required. Do not copy engine source/fixtures.

## Functional dependencies and scope

Recursive input and collection/batch capacity remain active functional gaps with
separate design, resource, migration and validation work (F1/F2). Do not disable
bounded admission or silently truncate to appear unlimited. Detector selection
(F3) is shelved; retain the precise assessment without pursuing integration now.
No blanket engine rewrite, new future-tool implementation, build, CI, release or
signing change is authorized by this documentation/prototype request.

## Corrected acceptance requirements

- Sparse Home and two import choices; clear distinction between prototype and actual acquisition.
- Compact primary toolbar with Home arrow and selection count; overflow Columns/Clear/Import; header Select All.
- Recursive folder acquisition with truthful incomplete outcomes (F1/F2).
- Capacity policy grounded in resources, not an arbitrary owner-imposed 32-track rule (F1).
- Source-property columns sort, configure and resize individually; widths persist.
- Native horizontal/vertical swipes, aligned headers, full-name access and shared selection.
- Persistent bottom sheet overlays the table, reaches below-toolbar maximum and returns compact without losing context.
- Forensic/Metadata/Log/Convert hierarchy; existing Spectrogram/Compare inside Forensic.
- Actual scan lifecycle, findings and evidence remain in the same sheet; no result-page detour.
- No Duration coverage dropdown or selective partial-analysis UI; saved scopes stay accurate.
- Understandable versioned results with genuine advanced originals and exact byte export.
- Batch identity, checked membership, focus and late callback handling remain correct.
- Existing settings/history/export remain functional and concise.
- Actual phone review: sheet drag, sheet/table scroll, narrow width, long names, large fonts, resized columns, running/completed transitions.

Build or browser success is not owner phone acceptance. Mark each requirement
pass / fail / blocked by functional dependency / not yet tested, with revision,
device and evidence. Shelved work is tracked separately, not failed active acceptance.

## Parked decisions retained from owner v4: decisions for later features (preserve, do not implement)

### Converter — confirmed future direction

- MediaHuman-inspired **hybrid**: exactly one target output-preset dropdown and one Convert button in the expandable row below the primary workspace toolbar; detailed options, progress and results in the future **Convert section of the persistent bottom sheet**. Target “Resolution” refers to **output** format/sample rate/bit depth, not source audio quality or image size.
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

## Additional deferred QoL

**Do not implement:** embedded cover-art thumbnail occupies the checkbox position
for an unchecked track. Tapping artwork or the row selects it and reveals the
checked checkbox; unchecking restores artwork. No extra column. This is future
interaction behavior, not a change to current prototype selection/focus handling.

## Revision record

Owner v4 established dense file-first work, source-property columns, shared checked
selection, same-surface results and parked future-feature decisions. The latest
owner corrections explicitly replace split panes with an overlaid bottom sheet,
restore four categories, add persistent individual column sizing, consolidate the
toolbar/overflow and shelve detector selection. Converter placement is preserved
as a future layout requirement, with a prototype-only layout preview, not Android conversion controls.
