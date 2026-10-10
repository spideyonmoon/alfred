# U1 forensic report — revision 4 review

## Current status — Android implementation, 2026-10-10

The owner approved revision 4 with “this is good. implement it”. Native Android
implementation is now recorded in [UI_REPORT_ANDROID](../task-results/UI_REPORT_ANDROID.md).
The HTML stays revision 4. Earlier approval-pending/HTML-only statements below are
historical. Offline build, lint and actual-data model checks pass; rendered/device
acceptance remains open. See that record for exact validation and limitations.

2026-10-10. [Open the complete prototype](ui-review.html).
FR2–FR5 implementation is complete. FR6 source, evidence and model review is
complete; browser visual/gesture validation is blocked and owner approval is
pending. This record does not claim visual acceptance or Android implementation.

## What to review

Use the controls outside the phone: real sample report, 320 or 400 px, default or
large text, light or dark, normal or fully expanded sheet. Open all five sections,
their nested findings and the technical-data section. The same continuous report
stays in the established persistent sheet; its handle, toolbar, quick row, table,
selection and Home/tool behavior remain in place.

The three owner screenshots were inspected directly. Revision 4 develops their
strong title, neutral rounded tiles, tabular measurements, paired channel cards,
slim cutoff-position scale, fine dividers and outline section icons. The SVG icons
are original simple paths, with no external icon/font dependency. Styling is
scoped to the report; the theme selector is a review control, not a new app setting.
Large text stacks the metric and channel groups; narrow assessment text reflows.
These CSS choices still require rendered review.

## Complete report review

| Area | Implemented and reviewed at source/data/model level |
| --- | --- |
| Overview | Track identity; qualified finding and score 0; separate native INCONCLUSIVE; 1 lossy / 5 natural **points**; DR8, −10.63 LUFS, 8.54 LU; cutoff/Nyquist; estimated +0.319/+0.025 dBTP; fingerprint, signalling and exercised-bit observations |
| Spectral investigation | Distinct native channel and reference-mid cutoffs; cliff/sharpness; HF magnitude, entropy, envelope, banding, sparsity, bound and variance; all 12 stored excerpts with time bounds, cutoff position, eligibility and wall result; nested excerpt measurements and wall rule |
| Dynamics laboratory | Overall/channel DR; crest, linear RMS/sample peaks and estimated peaks; integrated/momentary/short-term loudness; LRA/quantization bounds; complete-window/tail and interpolation method limits |
| Codec fingerprints | Actual AAC 0.0153 versus 0.06/0.10 criteria; Vorbis 0 versus 0.03; recorded effects; first-180s scopes; first-3s MQA search and zero candidates; resampling hit/wall; applicability of preceding-energy evidence |
| Bit-depth investigation | Declared/reference/native exercised precision; separate scopes; measured native floor −47.76/−44.15 dBFS and floor color; reference floor −46.83 dBFS is measured but masked for depth inference; stored qualified depth interpretation |
| Evidence explorer | Ordered score trail; five source hypotheses; all 34 criteria with named values, scopes, recorded states and effects; applied, untriggered and excluded groups; explicit unavailable/vetoed semantics |
| Verification/technical | Full decode, header match and decoder check; original download; engine/method/schema and input/report/PCM hashes; rule index; explicit exact-projection or selected-rule inspection. Ordinary report has no JSON/predicates or frame counters |

All content, including closed disclosures, was exercised in the actual renderer
using an element sink and inspected in source. This finds render exceptions and
data/structure problems; it cannot show how any element looks or behaves in a browser.

## Evidence integrity and adaptation

The original ABBA JSON hash remains
`069c2ca35351ad2889f1326339006f8a055ed19796309dd64196be7419600dc3`.
The embedded assessment, coverage, native channels/stream, loudness, peaks, MQA,
DR, technical metadata, spectral probes and noise-floor data match the original
UTF-8 product. Original files are unchanged and no audio was copied or uploaded.
See [sample provenance](UI_REPORT_SAMPLE.md) and [rule ledger](UI_REPORT_EVIDENCE.md).

The main score trail uses recorded before/after states, not executed predicates:
one lossy point, five natural points, stored net/heuristic zero, broadband
counter-evidence main-score adjustment 0 → −30, final clamp −30 → 0. This is not
a probability, detector count or certificate of source authenticity.

Priority is driven by stored positive source/depth candidates, contributing AAC/
Vorbis rules, MQA sync matches and declared-versus-exercised activity. Relevant
investigations move before the default section order and get a linked finding.
ABBA has none of these priority flags. Null values remain unavailable, numerical
zero remains zero, and tiny positive values do not silently round to exact zero.
Product, measurement, reference, statistics, metadata and reference-input versions
are gated; an unsupported reference component does not remove supported native
measurements. Feature version/status also gates translated readings.

No real positive or partial-success specimen was added. Synthetic model variants
were used only inside the local validation harness for nulls, unsupported versions,
priority ordering and unavailable/vetoed states; none is offered as audio evidence
in the prototype. Real resource-limit and illustrative-no-report states remain.

## Validation results

Passed locally:

- JavaScript syntax (`node --check`) and actual renderer execution in an element
  sink across overview, all five sections, 34 rule steps and 12 excerpts.
- Exact source-projection equality, original JSON hash, unique disclosure keys,
  no ordinary raw dump, and the unchanged-original link target.
- Actual `renderContent` model roundtrip through Spectrogram and back: nested
  disclosure keys and report scroll value retained. Technical-record action opens
  explicit inspection. These are model checks, not gesture/focus acceptance.
- Null/missing and unknown component handling, priority model cases and recorded
  unavailable/vetoed distinctions. Sorting/null placement, import-order reset,
  checked-set and width serialization model remains valid.
- HTML structure, documentation links/whitespace, unchanged workspace functions
  and preservation of pre-existing files outside the declared report/docs edits.

Local check receipts and harnesses are under ignored `build/ui-plan/`:
`revision-4-checks.json`, `revision-4-integrity.json`, `check-report4.cjs`,
`check-report4-integrity.py`, `fr4-start.json` and `fr3-before.html`.
Ignored artifacts are not a backup. The committed/untracked deliverable is the
self-contained HTML plus this documentation; original JSON access additionally
requires the retained build file described in the sample provenance.

Intermediate validation issues: one PowerShell-quoted edit failed to parse before
writing; it was replaced by a file-based edit. The first element-sink run reached
the synthetic cases and failed because its VM lacked `structuredClone`; the harness
was corrected and rerun successfully. Additional component-version tests initially
redeclared a VM variable; block-scoping corrected the harness. The feature-version
test then found specimen-specific values embedded in two criterion descriptions;
those examples were removed, along with implementation-facing directions in the
ordinary rule copy. The final renderer/model, syntax and integrity checks passed.
None of these checks involved an engine or Android build.

### Required visual matrix — all blocked, not passed

The prior local file-navigation denial explicitly prohibited workarounds; the
earlier local HTTP attempt also encountered a socket denial. No browser route,
alternate automation library or hosted/uploaded copy was used to bypass these.

| Width | Sheet | Default/light | Default/dark | Large/light | Large/dark |
| --- | --- | --- | --- | --- | --- |
| 320 px | Normal | Blocked | Blocked | Blocked | Blocked |
| 320 px | Full | Blocked | Blocked | Blocked | Blocked |
| 400 px | Normal | Blocked | Blocked | Blocked | Blocked |
| 400 px | Full | Blocked | Blocked | Blocked | Blocked |

For each case, the overview **and all expanded sections through their ends** need
inspection for clipping, number/unit wrapping, contrast, excess spacing, overflow
and retained scroll. DOM presence is not a visual pass. Pointer gestures, keyboard
focus, screen reader behavior and actual CSS reflow remain unverified.

No engine rebuild/new scan, Android implementation/build/tests, CI, APK/device,
physical acceptance, commit, push or release was run. A07 and A08 remain open.
Next action is owner prototype review, then correction of any visual findings;
Android implementation needs subsequent owner direction.
