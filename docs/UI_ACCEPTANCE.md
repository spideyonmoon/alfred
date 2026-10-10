# UI acceptance ledger — corrected U1 direction

## Current status — Android implementation, 2026-10-10

The owner approved revision 4 with “this is good. implement it”. Native Android
implementation is now recorded in [UI_REPORT_ANDROID](../task-results/UI_REPORT_ANDROID.md).
The HTML stays revision 4. Earlier approval-pending/HTML-only statements below are
historical. Offline build, lint and actual-data model checks pass; rendered/device
acceptance remains open. See that record for exact validation and limitations.

## 2026-10-10 revision 4 prototype checkpoint

FR2–FR5 are implemented in the [complete HTML report](ui-review.html). FR6 review
passed source/data/model checks covering all five sections, 34 rules, 12 excerpts,
original-data integrity and keyed state. The browser-policy restriction prevents
the 16-case visual matrix and runtime gesture/accessibility checks; none is passed.
See [the review record](UI_REPORT_REVIEW.md) for exact results and remaining gaps.
Owner prototype approval is pending. No Android/build/CI/physical acceptance or
release progress is implied, and the implementation ledger below is unchanged.

## Historical 2026-10-10 planning checkpoint

The owner's new [report brief and references](../owner-input/forensic-report-2026-10-10/README.md)
are preserved and mapped in the [restart plan](UI_REPORT_REFINEMENT_PLAN.md).
Input preservation, precise plan and real-data mapping are completed; the actual
report redesign, all-section review and 16-case layout matrix remain **not run**.
The current HTML is unchanged revision 3; “decent” is not full design acceptance.

Documentation checks cover local links, reference hashes, 34 rule IDs/states,
12 probe rows, real sample hash and unchanged prototype/source inputs. These do
not pass layout, accessibility, Android or physical acceptance. No new engine run,
Android tests/build, CI, device, commit/push or release was performed for planning.
The prior browser policy limitation remains recorded; no bypass was attempted.

## Existing implementation ledger

2026-10-09, revision 3. Source baseline `a0fe399f1d0a3902ed71f6de433b7c1eedecf28a`
plus pre-existing dirty UI work. This pass adds specifications and a browser
wireframe only. No new Android APK, device receipt or owner visual acceptance.
Core pin: `5c5ce00d44f6759dd6a7319804b5f7a21079d1b4`.

The [corrected requirements](UI_PLAN.md#corrected-acceptance-requirements)
supersede incompatible owner-v4 acceptance rows. Fail denotes a source-observed
mismatch, not a new phone test. F3 is shelved, not an active acceptance blocker.
The browser prototype cannot pass an Android acceptance criterion.

| # | Requirement | Status | Evidence / next action |
| --- | --- | --- | --- |
| 1 | Sparse Home | fail | WorkspaceScreens has promotional blocks; U2 after U1 review |
| 2 | Files one/many + Folder | fail | Home currently offers three document actions; reuse multiple picker |
| 3 | Recursive eligible-file import, honest incomplete outcome | blocked by functional dependency | SafSelection skips directories; F1/F2 design in UI_FUNCTIONAL_DEPENDENCIES |
| 4 | No arbitrary 32-track product limit | blocked by functional dependency | Related picker/jobs/metadata/result bounds inventoried; F1 measurements and migration pending |
| 5 | Configurable/sortable/individually resizable source columns and persisted widths | fail | Current TrackTable fixed columns/no sort or width controls; U3 projection/sizing specified |
| 6 | Native horizontal/vertical swipes, aligned headings, shared checkbox selection | not yet tested | Prototype uses native overflow without permanent scrollbar controls; Android/phone checks pending |
| 7 | Persistent bottom sheet over table, compact to below-toolbar maximum | fail | Android currently uses weighted split panes; U4 now specifies overlay, simple handle and no expand/restore buttons |
| 8 | Setup through actual findings/evidence in same bottom sheet | fail | Inline Forensics hides ResultBrowser and directs completion to history; U5 target/state contract |
| 9 | No Duration coverage dropdown or selective partial-analysis UI | fail | Existing Android still offers prefix under Partial analysis; prototype removes control; F3 shelved |
| 10 | Stored requested/actual coverage remains accurate | not yet tested | U6 mapping retained, including old prefix reports; one full-file specimen available; prefix cases pending |
| 11 | Understandable findings/evidence/limits and advanced originals | not yet tested | U1 field ledger drafted; real full-file specimen available; broader cases and owner review required |
| 12 | Batch uses job/item identity and checked membership | not yet tested | Work snapshots item list; panel routing/stale target tests not implemented |
| 13 | Forensic/Metadata/Log/Convert hierarchy, existing Spectrogram/Compare inside Forensic | fail | Android shells still mislabel tools; corrected prototype hierarchy is not runtime acceptance |
| 14 | Settings/history/export functional and readable | not yet tested | U8 integration not implemented or checked on this source revision |
| 15 | Actual phone: sheet/swipe/column sizing/narrow/long names/large fonts/transitions | not yet tested | No physical device run; explicit owner review and A07 remain |
| 16 | One toolbar; Select All in table header; overflow Columns/Clear/Import | fail | Current Android toolbar/menu differs; corrected prototype removes second bar |
| 17 | Clear removes entries only; Import does not assume replacement | not yet tested | Prototype Clear affects illustrative rows only; merge policy and Android grant/job checks pending |
| 18 | Concise presentation without duplicate headings/primary engineering diagnostics | not yet tested | Real report presentation revised; owner/phone review required |

Future conversion preset/button placement is demonstrated by noninteractive
specimens in the expandable prototype quick row. Detailed Convert section remains
deferred; no Android conversion controls are implemented. Cover-art-in-checkbox-position selection is
deferred QoL and absent from the prototype. Metadata/Log/Convert interfaces remain
unbuilt. These deferred items do not block current UI review.

## Checkpoint requirements

- Workspace U2–U4: cancelled/empty/failed import; generation changes; duplicate and
  non-Latin names; numeric/null/tie sorting; tri-state selection; selection unchanged
  by focus/sort; individual width persistence; overlaid sheet extremes, rotation
  and large font behavior; Clear versus live job ownership and import semantics.
- Forensics U5–U6: exact original bytes and large integers; supported/unknown
  component versions; complete/prefix/partial/mixed/failed products; per-item evidence;
  cancellation/finalization; background/rotation; changed focus and stale loads.
- Integration U7–U8: actual live/saved PNG and Compare result; export/share failure;
  same-track assertion and common coverage; no-winner/incompatible comparison;
  Home history entry, back navigation, notification denial, accessibility labels.
- Functional F1/F2: separate checks described in the dependency packet; UI-only
  validation does not close them. F3 is shelved. Use revision-bound artifacts and focused CI when
  implementation is ready; do not repeat a full matrix for document/copy changes.

A07 physical resource/thermal/16 KiB acceptance and A08 signing/release stay open.
No release, signing, license, source push or CI dispatch is part of this packet.

## Revision 2 documentation/prototype checks

Passed: local documentation links and whitespace; JavaScript syntax; HTML unique
IDs, single toolbar, category order, removed controls and overlay structure;
prototype model numeric/null sorting, import-order reset, selection preservation
and width serialization. A 54-file hash inventory confirms existing Android/native
source, owner v4 input and dependency pin were unchanged. These checks do not
pass any Android acceptance row above.

Browser preview timed out and local socket access was denied. Rendering, pointer
drag/swipe behavior and physical usability were not verified. No Android tests,
build, CI or device run. Receipt: `build/ui-plan/revision-2-checks.json` (local).

## Revision 3 checks and remaining limits

Passed: pinned desktop CLI offline release build; real full-file forensic analysis
of Money, Money, Money; original JSON/hash retention and report field mapping.
Earlier source candidates returned actual resource_limit and unsupported outcomes,
preserved in [sample evidence](UI_REPORT_SAMPLE.md), not counted as successes.

Local checks: HTML structure/direct tool buttons, JavaScript syntax, numeric/null
sorting, import-order reset, checkbox preservation, width serialization, report
projection equality, document links/whitespace and 54-file source/owner/pin
preservation. Local receipt: `build/ui-plan/revision-3-checks.json`.
The initial projection equality check caught a Windows text-decoding mismatch
in one reference algorithm string. Explicit UTF-8 decoding corrected it; the
full comparison then passed. Report rendering was exercised with an element
sink to catch data exceptions, not a browser or layout simulation.

Browser local-file navigation was denied by browser security policy. No workaround
was attempted. Render, dragging/swipes, 320 px/large-text geometry and accessibility
interaction remain unverified. No Android changes, build/tests, CI or device run.
Future U2 checks additionally require quick-row push/collapse geometry, Home
roundtrip without file/job loss, and correct staggered icon/direct tool access.
