# Alfred UI work chunks — corrected U1 direction

## Current status — Android implementation, 2026-10-10

The owner approved revision 4 with “this is good. implement it”. Native Android
implementation is now recorded in [UI_REPORT_ANDROID](../task-results/UI_REPORT_ANDROID.md).
The HTML stays revision 4. Earlier approval-pending/HTML-only statements below are
historical. Offline build, lint and actual-data model checks pass; rendered/device
acceptance remains open. See that record for exact validation and limitations.

**2026-10-10 report refinement checkpoint:** use
[UI_REPORT_REFINEMENT_PLAN](UI_REPORT_REFINEMENT_PLAN.md) and
[UI_REPORT_EVIDENCE](UI_REPORT_EVIDENCE.md). FR0 preserves owner inputs; FR1 records
the real-data audit (both documented). FR2 overview/components, FR3 five detailed
sections, FR4 technical access/state and FR5 adaptive emphasis are **implemented**
in revision 4. FR6 source/data/model review passed; its 16-case rendered matrix
remains blocked by the recorded browser policy. See the
[complete review record](UI_REPORT_REVIEW.md). Owner approval is pending. These
are U1 HTML/document subchunks, not Android implementation. Next: owner review and
permitted rendered validation; no ordinary raw dumps remain.

2026-10-09, revision 3. Governing [owner plan and U1 corrections](UI_PLAN.md).
**Documentation/prototype revision only; no Android implementation yet.**
Existing dirty Android/source/APK work is preserved. This packet is not visual,
runtime or physical acceptance. Original owner v4 remains unchanged in owner-input.

## Settled direction

- Sparse Home; Files handles one/many, Folder targets recursion.
- Dense source-property spreadsheet with native swipe scrolling, configurable,
  sortable and individually resizable columns; preserve chosen widths.
- ONE compact workspace toolbar; tri-state Select All in table header; overflow
  contains Columns, Clear, Import. No unconditional replacement assumption.
- Persistent bottom-anchored sheet expands over the table, virtually to the
  toolbar. Simple handle only; no split panes, divider, slider or expand/restore buttons.
- Main categories: Forensic, Metadata, Log, Convert. Existing Audio Forensics,
  Spectrogram and Compare belong inside Forensic. No unfinished-feature interfaces.
- Scan setup/progress/actual findings/evidence and saved results share that sheet.
- Remove Duration coverage dropdown. Detector-selective partial analysis is shelved.
- Future upper toolbar: one output-preset dropdown + Convert button in an expandable quick row; details in
  future Convert section. Prototype-only layout specimens are allowed; no Android controls yet.
- Cover-art-in-checkbox-slot selection is deferred QoL; do not implement now.

## Work order and completion evidence

| Chunk | Deliverable | Depends on | Completion evidence |
| --- | --- | --- | --- |
| U1 | Concrete corrected sheet/table/report spec and prototype | Owner corrections | Layout review, real pinned report specimens and versioned evidence mapping |
| U2 | Sparse Home, one toolbar, import/clear/overflow shell | U1; import-membership decision before acquisition changes | Two import choices, entry-only Clear, honest errors and existing saved-result access |
| U3 | Shared spreadsheet | U1/U2 | Actual sorting/configuration/individual sizing, preserved widths, header Select All, stable identity and native scrolling |
| U4 | Persistent bottom sheet and category state | U3 | Overlays unchanged table geometry, compact-to-toolbar extent, simple handle, preserved context |
| U5 | Same-sheet Forensics lifecycle | U4, U1 state contract | Real queued/running/cancelled/finalizing/completed/saved states without forced page navigation |
| U6 | Readable report and evidence | U1 report mapping, U5 | Actual findings/coverage/limits, version gates, original data and bytes |
| U7 | Existing Spectrogram/Compare inside Forensic | U4, capability verification | Actual live/saved output, scope/compatibility/PNG/export behavior preserved |
| U8 | Secondary actions, copy, accessibility | U6/U7 | Working history/settings/export/share, concise errors, accessible gestures/focus |
| U9 | Consolidated validation and phone review | U2–U8; active functional chunks | Revision-bound evidence and owner phone acceptance; open dependencies remain explicit |
| F1 | Collection/batch capacity and import-membership design | Enforcing-layer assessment | Resource policy, migration/retention/grant design before implementation |
| F2 | Recursive folder acquisition | F1 and later explicit functional scope | Bounded traversal, cancellation, deterministic identity/order and truthful incomplete outcomes |
| F3 | Detector-selective execution | Shelved by owner | Retain assessment; no current integration, selector or UI gate |

Sequence remains U1 → U2 → U3 → U4 → U5 → U6 → U7 → U8 → U9 when implementation
is later authorized. F1/F2 remain separate functional work; F3 is not on the
active path. No Android coding/build/CI is authorized by this revision request.

## U1 — reviewable concrete specification

Read [UI_SPEC](UI_SPEC.md) and [prototype](ui-review.html). Review compact and
near-full sheet states below the primary toolbar and optional quick row, category order and subordinate
Forensic tools, individual column sizing and overflow placement. Check narrow
width, large fonts, duplicate/long/non-Latin names and a long result.

Use real pinned saved/generated product examples for positive/no-flag/partial,
prefix coverage, mixed batch/failure and unknown version. Selective example is
shelved. Revision 3 supplies a real complete report and real failure cases
([sample evidence](UI_REPORT_SAMPLE.md)); broader cases remain pending. Preserve
field/candidate → supported value → label → unit/domain/coverage → unavailable
wording → evidence links, component versions and exact integer/null semantics.

Remaining review decisions: default columns, trial widths/checkbox pinning,
sort cycle, import append/merge versus explicit replacement and duplicate handling.
Do not reopen settled sheet/category/toolbar decisions or request permission for
ordinary documentation corrections. Missing PDFs cannot be claimed reviewed.

## U2 — Home and one workspace toolbar

Keep sparse Home and existing secondary routes. Workspace has a back arrow to
Home, X selected, a quick-row chevron and a right-aligned staggered menu icon.
No Music heading. Home/return preserves files, selection, tool/report state and
ongoing work. The optional floating-styled quick row participates in layout,
pushing the spreadsheet downward; collapsing returns its space. Only the prototype
shows labelled preset/Convert layout specimens; no unfinished Android controls.
Put Select All in the spreadsheet header; no second selection/Columns bar.

Overflow includes Columns, Clear, Import. Import gives Files (one/many) and Folder
(recursive target). Do not call it Replace files or silently assume replacement.
Resolve membership semantics with F1 before changing acquisition. Until then the
prototype opens choices only; no fake successful import/recursion.

Clear removes workspace rows/selection, never physical source files or saved
results. Submitted job membership and required leases remain intact. Check empty
collection, active job, grant ownership, cancelled picker, failed import, duplicate
identities and honest incomplete outcomes. Preserve meaningful saved-result access.

## U3 — spreadsheet, shared selection and persistent widths

Use stable item IDs/import ordinals; freeze job membership independent of later
selection/sort. Checkbox selection is shared across operations. Row focus remains
separate for this revision; do not implement the deferred artwork selection behavior.
Tri-state header Select All covers current collection, not just viewport.

Actual numeric/text/null sorting, configurable columns and individually resizable
header edges are required. Widths persist by column ID through hide/show, sorting,
rotation and restoration. Resize gesture must not trigger sort or selection; no
automatic neighboring-column compression. No permanent scrollbar controls: use
native horizontal/vertical swipe scrolling and aligned header/cell offsets.

Reuse off-main complete metadata access via bounded typed display projections;
do not parse every large report to show rows. Metadata declarations are not
authenticity grades. Check long/duplicate/non-Latin names, missing properties,
numeric versus lexical sort, tie order, none/some/all selection, active-job sort,
resizing/hiding/restoring columns and narrow/large-font behavior. F1 limits remain.

## U4 — persistent overlaid bottom sheet

Replace the split-pane model with a persistent, nonmodal, bottom-anchored sheet
over the spreadsheet. The table retains its geometry/scroll state behind it.
Maximum extent is measured usable area below toolbar/insets, with no arbitrary
percentage ceiling; compact extent holds handle/category/current context.

Only a simple handle is visible. Remove Expand tools and Restore split buttons;
no extra divider or slider. Sheet-content gestures scroll the report; table swipes
work in exposed space. Accessibility actions attach to the handle without adding
visible instructional controls. Preserve extent/selection/focus/category/tool/
report scroll and expansion across rotation, state changes and restored sessions.

Categories: Forensic, Metadata, Log, Convert. Preserve the hierarchy but build no
unfinished editors/forms or promotional placeholder pages. Test measured extremes,
landscape/insets, nested gestures and large fonts. Browser behavior or semantics
does not prove actual phone touch usability; owner physical review remains.

## U5 — same-sheet Forensics lifecycle

Implement U1's target keys and full job state contract: setup, queued, acquisition,
running, cancellation requested, finalizing, completed, failed, cancelled,
interrupted, mixed batch and saved report. Actual results appear inside the same
sheet, not merely a completion message or a link to another page.

Remove Duration coverage dropdown; do not substitute a prefix selector. Shelve
detector-selective partial analysis. Retain saved scope data and accurate requested/
actual coverage; existing admission remains honest. Preserve existing tool-specific
scope behavior where Spectrogram/Compare requires it.

Auto-open only if category/tool/attempt is still focused. Later callbacks/loaders
cannot steal another track/report's focus. Bind by persisted IDs, not filename or
row/descriptor index alone. Cancel preserves committed items; retry is explicit
and fresh. Check selection/sort/category/tool changes, late callbacks, background,
rotation, interrupted recovery and result commit versus decode end.

## U6 — real report interpretation and evidence

Use one renderer for completion and saved results. First section: identity,
achieved coverage, stored qualified finding, supporting evidence and specific
limits. Evidence links follow candidate/rule/input-feature IDs with actual
channel/window/unit/domain. Keep false/null/not-triggered/unavailable distinct.

No score/probability/quality inference, invented thresholds or rewritten exports.
Version-gate each component and keep advanced originals/diagnostics accessible.
Test exact large integers, unknown versions/methods, missing scores, mixed outcomes
and artifact failure alongside valid measurements. Reduce repeated headings and
decorative/caveat blocks; information density must not erase necessary limitations.
Owner understanding of real results is required, not just a passing build.

## U7 — existing tools within Forensic

Reuse retained screens/work/loaders/preview/export, hosted inside the Forensic
category of the same sheet. Audio Forensics, Spectrogram and Compare do not
replace main categories. Use three direct buttons, never a tool dropdown.
Switch content in the same sheet and retain per-tool context. Verify real live/saved output, not source-file existence.

Preserve Spectrogram one-track scope, preview versus PNG preset/export behavior;
Compare same-track assertion, 2–32 arity, common coverage, version/domain
compatibility and no-winner cases. No new DSP or comparison semantics. Larger
workspace capacity does not authorize larger Compare operations. Cancellation,
failure and unavailable output remain honest and inspectable.

## U8 — secondary flows and concise presentation

Preserve history, working settings, notifications, export/share and deletion.
Saved selection opens the same-sheet renderer without rescan. No new navigation
or controls solely to teach touch interactions. Distinguish actual permission,
unsupported, storage/resource, interruption and export failures; preserve local
results on export failure and source files on entry/report deletion.

Remove duplicated headings, decorative explanations, primary UUIDs/engineering
diagnostics and unnecessarily large controls. Check back behavior, sharing grants,
large text, spoken labels, focus order, sheet/column handle semantics and non-color
status. Do not add speculative settings, launcher rebranding or future-tool forms.

## Functional work and deferred scope

F1/F2 details: [UI_FUNCTIONAL_DEPENDENCIES](UI_FUNCTIONAL_DEPENDENCIES.md).
Distinguish collection size, job membership, Compare arity, metadata/history/byte
quotas and transport bounds. Do not globally replace 32 or flood the queue with
segmented jobs. Resource-backed paging/retention/migration/grant behavior must be
settled and measured before implementation. Recursive traversal needs cancellation,
identity/cycle handling, provider-error reporting and truthful incomplete results.

F3 assessment found no public end-to-end detector mask at the pinned revision.
Owner has now shelved this work. Keep the smallest future core dependency packet;
do not edit/copy engine source, update pins, create fake selectors or treat it as
current UI failure. Exact PCM/JSON, bounded worker/cancellation and original bytes stay.

Future Converter: one targeted output-preset dropdown and one Convert button in
the expandable quick row; detailed conversion in Convert section. Preserve MediaHuman
arrangement, checked-only scope, A/B/C post-conversion behavior (C default), original
retention and Converted/ default. Prototype-only noninteractive layout preview is allowed; no Android conversion controls yet.
Future Metadata keeps common-value / `<keep>` and explicit-edit-only behavior.
Log/CUE remains deferred.

Deferred QoL: unchecked row's embedded cover art replaces its checkbox in the
same position; artwork/row tap selects and reveals checked checkbox; uncheck
restores artwork. No extra column. Do not implement in prototype or Android now.

## U9 — validation and owner phone review

Use [corrected acceptance ledger](UI_ACCEPTANCE.md). Group future local/CI checks
at U2–U4 workspace, U5–U6 Forensics and U7–U8 integration checkpoints. No repeated
heavy Android validation per document/copy edit. Bind evidence to source/core,
dirty compiled inputs and APK hash; reuse only through existing source guard.

Phone review must cover compact-to-near-full bottom sheet, exposed table swipes,
long report scroll, category/tool context, narrow/large-font layouts, column
width persistence and live-to-result transitions. A07 physical resource/thermal/
16 KiB acceptance and A08 signing/release are separate and still open.

## Current status

- U1 revised spec/prototype includes a real pinned report and failures; broader
  specimens and owner visual acceptance remain. F1 design and F3 source assessment are drafted.
- U2–U9 Android implementation not started by this pass. F1 measurements and
  implementation, F2 traversal remain pending. F3 integration shelved.
- Current activity is documentation/prototype revision and focused local checks;
  no app build/tests, CI dispatch, push, signing or release.
- Keep HANDOFF and acceptance evidence current; preserve existing dirty owner work.
