# U1 report refinement — restartable implementation plan

## Current status — Android implementation, 2026-10-10

The owner approved revision 4 with “this is good. implement it”. Native Android
implementation is now recorded in [UI_REPORT_ANDROID](../task-results/UI_REPORT_ANDROID.md).
The HTML stays revision 4. Earlier approval-pending/HTML-only statements below are
historical. Offline build, lint and actual-data model checks pass; rendered/device
acceptance remains open. See that record for exact validation and limitations.

2026-10-10, Asia/Dhaka. **Revision 4 prototype implemented; owner approval pending.**
FR2–FR5 are complete. FR6 source/data/model review is complete, with the visual
matrix blocked by the recorded preview-policy restriction. See the
[full review record](UI_REPORT_REVIEW.md) for exact checks and limitations.
The planning decisions below remain the report design contract.
No Android implementation, build cycle, engine change or unfinished-tool work.

## Start here after a thread or context change

1. Read HANDOFF.md, README.md, BUILDING.md, ANDROID_CONTRACT.md and applicable
   AGENTS.md before changes. Preserve the existing dirty owner source.
2. Read the [complete owner brief](../owner-input/forensic-report-2026-10-10/brief.md).
   Inspect all three [visual references](../owner-input/forensic-report-2026-10-10/README.md).
   They are preserved byte-for-byte; do not depend on temporary attachment paths.
3. Read this plan, [evidence mapping](UI_REPORT_EVIDENCE.md), and
   [sample provenance](UI_REPORT_SAMPLE.md). Use [UI_SPEC](UI_SPEC.md) for the
   established workspace and lifecycle contract.
4. Inspect `docs/ui-review.html`: `const sample`, `renderReport`, `renderContent`,
   `viewState`, `setSheet`, and review settings. The revision-4 renderer now implements the report design; original data is
   behind explicit technical access.
5. FR2–FR5 are implemented. Review the full report and the FR6 limitations below.
   Do not rerun old transformation scripts over the current HTML.
6. Present the complete prototype for owner approval; obtain permitted rendered
   review or owner feedback to close visual gaps. A prototype does not authorize
   Android implementation.

The original user response was “decent,” accompanied by a detailed report-design
brief and three reference screenshots. Treat this as qualified feedback and a
new refinement direction, not final acceptance of the current U1 presentation.

## Authority and precise scope

The latest report brief supersedes revision-3 report presentation choices:
automatic JSON dumps, raw predicates, prominent frame counters and excessive
precision must leave the ordinary investigation UI. The new brief explicitly
requests a qualified visible reference score; this supersedes the earlier
advanced-only score placement, **without** permitting probabilities or quality
gauges. Native INCONCLUSIVE remains visible next to the qualified assessment.

Preserve the established workspace without redesign:

- Home arrow, selection count, quick-row chevron and right-aligned staggered menu.
- Expandable quick row pushes the spreadsheet down; future converter specimens
  are prototype layout only. Columns/Clear/Import stay in the action menu.
- Native two-axis spreadsheet scrolling, header Select All, stable checked set,
  sortable/configurable/individually resizable columns and persistent widths.
- Persistent bottom sheet, simple handle, compact to near-full measured height.
- Primary categories Forensic, Metadata, Log, Convert; three direct Forensic
  buttons Audio Forensics, Spectrogram, Compare, with same-sheet content changes.
- Home return preserves files/work/context; expansion, scroll and evidence state
  survive ordinary tool/focus/lifecycle transitions under the existing contract.
- Duration selector absent; detector-selective analysis shelved. Artwork selection,
  Metadata editing, Log/CUE and Converter functionality stay deferred.

Do not change core sources, fixtures, policies, pins, locks, exact payloads,
admission/cancellation semantics, signing, licence or release state. Do not
reinterpret unrelated dirty Android files as changes belonging to this task.

## Existing artifacts and what is missing

| Artifact | Current state | Next action |
| --- | --- | --- |
| `docs/ui-review.html` | Revision 4, complete ABBA report and explicit technical access | Owner review; permitted visual validation |
| `docs/UI_SPEC.md` | Workspace contract and revision-3 field ledger | Apply this report addendum when implementing |
| `docs/UI_REPORT_SAMPLE.md` | Verified full-file run and failure provenance | Retain; qualify old layout description as history |
| `docs/UI_REPORT_EVIDENCE.md` | New exact measurements, thresholds, states and translation plan | Use as version-bound presentation source |
| Owner brief / three PNGs / manifest | Durable copies, hash checked | Preserve originals; do not edit references |
| Original JSON / CLI / progress / receipts | Local ignored `build/ui-plan/` artifacts | Reuse; no new analysis needed for this redesign |
| Browser/physical validation | Revision 4 visual matrix blocked; no Android work | Explicitly pending; no fabricated screenshots or passes |

The original full report is `build/ui-plan/sample-product.json`, a JSON array with
one product. Its SHA-256 is
`069c2ca35351ad2889f1326339006f8a055ed19796309dd64196be7419600dc3`.
Engine 0.32.0 at clean pin `5c5ce00d44f6759dd6a7319804b5f7a21079d1b4`.
Read JSON explicitly as UTF-8: a prior Windows-default decoding mismatch affected
one algorithm string and was caught/fixed by projection comparison.

Ignored outputs are local evidence, not a Git backup. If unavailable on another
checkout, use the recorded pinned CLI command and source path in UI_REPORT_SAMPLE;
do not substitute an older neighboring executable or fabricate a result. A rerun
may change timings/report bytes; bind any new specimen to its own receipt.

## Visual system to implement

Reproduce the references' actual components: strong track title, subdued metadata,
large tabular measurements, compact rounded neutral metric tiles, paired channel
cards, a slim labelled frequency scale, fine dividers and icon-led investigation
accordions. Do not replace them with generic status badges or spacious dashboard
cards. Do not copy the reference's very tall screenshot spacing onto a 320 px UI.

Use a consistent family of small outline icons with approximately 1.75–2 px
strokes in a 24-unit viewBox: waveform, activity, fingerprint, binary/layers,
scan/search, chevron, information and verified check. Use existing assets if
suitable, otherwise simple code-native SVG; no bitmap/image-generation task is
needed. Do not add an icon to every value. Any borrowed icon asset needs its
licence preserved; a Lucide-like visual style alone does not require a package.

Suggested initial CSS trials (proposals, not accepted measurements):

| Component | Starting treatment | Responsive rule |
| --- | --- | --- |
| Report title | 1.6em, semibold, compact line height | Wrap naturally; no title truncation |
| Section heading | 1.05–1.15em, semibold; 18–20 px icon | Summary text grows vertically |
| Numerical metric | 1.5–1.8em, tabular numerals | Preserve minus sign, decimal and unit |
| Supporting copy | 0.9–1em with readable contrast | No tiny fixed labels to force columns |
| Surface | Neutral subtle fill, 10–14 px corners | No arbitrary green/red quality coding |
| Spacing | 8–12 px internal; 12–16 px between groups | Dense but no label/value collisions |
| Trio | Three equal cards at ordinary phone text | Reflow to two/one columns for large text |
| Channel pair | Two aligned cards with shared measurement rows | Stack at large text if necessary |
| Frequency scale | Cutoff marker/bar on explicit Hz/kHz axis | Never a spectrum or confidence gauge |

Treat these as initial CSS values to validate, not exact owner-prescribed tokens.
Support light/dark through scoped report variables. A review-only theme selector
outside the phone is appropriate for testing, not a new application setting.
Use readable system typography; the screenshot's custom font is not an asset
requirement and must not introduce a remote font dependency.

## Reusable component contracts

| Component | Input and responsibility | Must not do |
| --- | --- | --- |
| MetricTile | Stored value/display, units, label, availability, optional explanation | Convert unavailable to 0 or imply quality |
| MeasurementScale | Quantity, same-unit bounds, labelled ticks/marker, scope | Invent bins, time series or normalized confidence |
| ChannelComparison | Stable channel identity, aligned same-unit measurements | Infer channel labels from count without checking mapping |
| AssessmentPanel | Stored qualified summary, main score, native ancestry, calibration | Recompute policy, imply authenticity or probability |
| EvidenceFinding | Observation, applicable criterion, recorded outcome, meaning, scope | Treat every nonzero metric as a detected signature |
| DetectorDetail | Nested human-readable measurements/method detail | Dump a feature object or raw predicate |
| InvestigationSection | Icon, title, compact description, keyed disclosure state | Navigate elsewhere or reset sheet/table state |
| TechnicalDataAction | Explicit original JSON/export and provenance/precision access | Automatically append raw JSON under ordinary accordions |

DOM text must use textContent rather than interpolating report strings into HTML.
Keep the artifact self-contained except for the explicitly local original-JSON
link, or provide an equally honest original-byte access mechanism. Do not silently
generate a replacement “original” export from the presentation projection.

## Continuous report architecture

### A. Rich overview

1. Identity: title; ABBA / Gold (Greatest Hits); FLAC, 44.1 kHz, 16-bit, stereo,
   3:06.787. Compact header, no duplicate page/category title or cover-page spacing.
2. Assessment: “No strong lossy indicators”; uncalibrated reference assessment;
   source history unverified. Show reference score 0 and native INCONCLUSIVE as
   different statements. Lossy 1 / natural 5 are heuristic point totals, **not**
   counts of five detectors or evidence probabilities.
3. Measurement trio: **DR8 | −10.63 LUFS | 8.54 LU**. DR8 is the stored label;
   expose numeric 8.475 dB in Dynamics. Do not replace DR with crest or LRA.
4. Frequency: 21.749 kHz cutoff, 22.05 kHz Nyquist, 8.10 dB cliff. A position
   scale only. State no strong low-pass indication from applicable reference
   rules; the single cutoff is not an FFT spectrum.
5. Paired true peaks: +0.319 / +0.025 dBTP, estimated. Explain 4× FIR estimates
   above zero without declaring audible clipping/distortion.
6. Compact codec rows: AAC 0.0153; Vorbis 0; MQA not detected in first 3 s;
   resampling candidate not indicated. No invented confidence meters.
7. Depth: declared 16-bit versus reference 16 bits exercised on each channel in
   first 30 s. Original source depth remains unverified.

Overview ordering should respond to stored findings. For this sample no strong
codec/source candidate is flagged; present the measured properties rather than
manufacturing an anomaly. Keep important scope qualifications beside results.

### B. Detailed investigation

Five coherent icon-led sections below the overview; each expands in the same
scrollable sheet. Titles match the supplied references where possible:

| Section | Required content and meaningful visual treatment |
| --- | --- |
| Spectral investigation | Reference/per-channel cutoffs; cliff and sharpness; HF magnitude ratio, envelope correlation, banding/sparsity; twelve discrete time-labelled two-second probes; eligibility/wall observations; actual adaptive wall and candidate rule outcomes |
| Dynamics laboratory | Numeric/labelled DR; channel DR/crest; integrated, momentary and short-term loudness; LRA; native RMS/sample peak and FIR estimated peak; aligned channel values; method distinctions and useful window limitations |
| Codec fingerprints | AAC/Vorbis measured values and actual reference thresholds; rule outcome, effect, significance and scope; MQA three-second search and candidate count; resampling indicators and source-veto distinctions |
| Bit-depth investigation | Declared versus native/reference exercised bits; per-channel results; measured first-30-second floor observations; masked reference depth interpretation; no original-depth claim |
| Evidence explorer | Measurement → criterion → recorded state → score/label effect; readable applied/untriggered/excluded/unavailable/vetoed cases; candidate hypotheses and reproducible rule links |

Use nested disclosures only where they add a level of investigation. Expand every
section during self-review. Do not create a polished overview over debug-style
inner panels. Raw data is an explicit separate action after useful investigation.

### C. Verification and technical access

Compact verification: complete track decoded, header length matches, decoder
verification passed. Label what was verified; do not suggest source authenticity.
Show source properties without displacing forensic evidence.

Use time intervals in normal presentation. MQA's three-second scope is material.
Loudness's last ~87 ms outside complete windows belongs in its method note, not
a prominent frame-counter row. Keep exact intervals, PCM/report hashes, versions,
rule IDs and the unchanged original report in explicit technical access.

## Data and interpretation rules

All values, thresholds and mappings are in [UI_REPORT_EVIDENCE](UI_REPORT_EVIDENCE.md).
That ledger is tied to the supported report method, not a generic detector policy.
Do not execute predicate text or reimplement the engine's verdict logic in JS.
Use recorded rule states and before/after effects; translations explain them.

Version-gate the product and each component. Preserve null, false and zero as
different states. Format values for reading without changing exported bytes.
No new thresholds, calibration, proof of source authenticity, private uploads,
invented spectra/waveforms/loudness histories or audio-derived images.

The screenshot labels Left/Right. Confirm the existing decoder's stereo channel
mapping before adopting those labels; otherwise use Channel 1/2. The saved stream
states two channels but does not itself name them. Reference mid/basis features
must not be relabelled native per-channel measurements.

## Adaptive emphasis without fabricated examples

Use stored positive candidates, signalling observations and applicable rule
outcomes to prioritize relevant findings. Do not infer a strong finding from an
arbitrary nonzero scalar or reclassify a contract-excluded rule as a negative.
Unavailable sections should offer a concise specific reason if relevant, not
empty cards. A supported partial report retains useful measurements.

The ABBA specimen can validate the no-strong-indicators presentation. Separate
real positive/partial specimens remain necessary before claiming adaptive UI
acceptance. Do not mutate the ABBA result into fake AAC/MQA/bit-depth examples.
Document unsupported branches and use clearly labelled presentation-model tests
only for state handling; they are not forensic evidence.

## Work chunks and restart checkpoints

| Chunk | Work | Status at this checkpoint | Required completion evidence |
| --- | --- | --- | --- |
| FR0 | Preserve brief/screenshots, record scope/current state | Complete | Byte-identical copies, manifest, handoff links |
| FR1 | Audit real sample fields/scopes/rule thresholds and presentation mapping | Planning audit complete | Evidence ledger, original hashes; verify mappings again when coding |
| FR2 | Scoped tokens/icons and reusable report components; build rich overview | Implemented; model/source checks passed; visual checks blocked | Actual ABBA overview, no workspace regression; light/dark/narrow/large-text review |
| FR3 | Build all five detailed investigation sections | Implemented; model/source checks passed; visual checks blocked | Every section useful/readable; values/criteria/states linked; no automatic raw dumps |
| FR4 | Verification/technical access, original-byte route, keyed disclosure state | Implemented; model/source checks passed; visual checks blocked | Exact report access; roundtrip state/scroll checks; no surprise navigation |
| FR5 | Data-driven emphasis and honest missing/unknown handling | Implemented; model/source checks passed; visual checks blocked | Source-based state conditions; ABBA and available real failure cases; missing positive coverage stated |
| FR6 | Full self-review, scoped checks, document final state and present prototype | Source/data/model review complete; rendered matrix blocked; owner review pending | Matrix below with evidence/limits; owner approval pending |

FR2 → FR3 → FR4 → FR5 → FR6. These are subchunks of U1's HTML refinement, **not**
authorization to begin U2–U9 Android work. Keep broader F1/F2 dependencies separate;
F3 and future tools stay shelved. Record completed chunks and exact next action
in HANDOFF after each substantive checkpoint so another thread can continue.

## Validation matrix — required, currently blocked

Cross product: 320 / 400 px × normal / fully expanded sheet × default / large
text × light / dark = **16 layout combinations**. At each, inspect the overview
and open every investigation section; scroll to its end. Do not mark a combination
passed based only on DOM presence or initial viewport.

| Area | Checks |
| --- | --- |
| Layout | No clipped numbers/units/title; readable labels; metric/channel reflow; aligned icons; no horizontal report overflow; no excessive blank space |
| Disclosure | Each opens/closes; nested evidence reachable; keyboard/focus usable; stored disclosure/scroll survives tool and sheet changes |
| Sheet/workspace | Normal/full report readable; simple handle; existing quick-row push/collapse, table scrolling, resizing/sort/checks and Home state retained |
| Evidence | ABBA values, actual criteria and rule outcomes match source; no raw predicates/JSON/primary frame counters; first-3s/30s/180s scopes correct |
| Integrity | Exact original hash; null versus zero; score points not probabilities/counts; exclusions not negatives; no invented charts or thresholds |
| Adaptation | Findings order follows stored evidence; no empty universal warnings; unverified positive branches explicitly pending |
| Preservation | Existing dirty source/owner/pin untouched; no Android/build/CI/engine/fixture changes |

Known prior tool limitation: local HTTP preview timed out with socket denial in
revision 2; local `file:` navigation was explicitly blocked by browser policy in
revision 3, including a prohibition on workarounds. Do not repeat blocked actions
or bypass that policy with another browser surface or automation library. Use only
permitted preview capabilities; if still unavailable, report visual checks as
blocked and provide the HTML for owner inspection. Element-sink tests can catch
data/render-function exceptions but cannot validate layout or gestures.

## Completion criteria

The prototype is ready for approval only when the full report, not just its top,
has been reviewed. Every section must answer useful measurement/interpretation/
evidence questions. Machine reproduction data must be reachable explicitly but
must not serve as the ordinary presentation. All remaining visual/runtime/data
gaps are stated, not concealed behind passing syntax tests.

Do not call U1 accepted until the owner accepts it. A07 physical resources/device
acceptance and A08 signing/release remain independent and open.
