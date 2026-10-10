# U1 real report specimen — 2026-10-09

**2026-10-10:** this specimen and its provenance remain authoritative. Its
revision-3 layout description below is historical: the new
[report refinement plan](UI_REPORT_REFINEMENT_PLAN.md) replaces ordinary raw-data
disclosures with designed investigation. [Evidence mapping](UI_REPORT_EVIDENCE.md)
records the field/threshold/scope translations. No new scan was needed.
Revision 4 is now implemented; see [the complete review record](UI_REPORT_REVIEW.md)
for changes, passed source/model checks and the blocked visual matrix.

The revision-3 [prototype](ui-review.html) presents an actual local run of
`09 - Money, Money, Money.flac` from the owner's supplied reference/test_files
folder in the independent audio-forensic-rust checkout. No audio was copied or
uploaded. The other spreadsheet rows remain explicitly illustrative.

## Provenance and execution

- Engine: 0.32.0, built offline from clean `.core` at full revision
  `5c5ce00d44f6759dd6a7319804b5f7a21079d1b4`.
- Local desktop release build: passed, 6m34s. No Android build or CI.
- Run: `audio-forensic.exe <source> --product-json --progress --deadline-seconds 600`.
  No fast/prefix option, detector override, PCM transformation or core modification.
- Exit 0, native outcome `analyzed`, elapsed 11.380 seconds on this
  desktop. This is not an Android performance or physical acceptance measurement.
- Encoded input: 21,385,832 bytes; SHA-256 `03556f249be137b4cc3a10e35f26981284ae9a8323722c2aa34691c2ef80ef4c`.
- CLI SHA-256: `d5341ef4478cfaa38aa1801cf540bd5d4eaa73d23be67a4c76ac475b3ff43ddd`.
- Original JSON SHA-256: `069c2ca35351ad2889f1326339006f8a055ed19796309dd64196be7419600dc3`.
- Decoded PCM SHA-256: `8cff0d5b5ab8d9101f02747e0367414d50076c8374d0e4af0f8b8362e5ebc560`
  (`s32le_msb_aligned`).

Exact original JSON, progress and receipt remain under ignored `build/ui-plan/`
as `sample-product.json`, `sample-progress.log`, `sample-receipt.json`. The
prototype embeds a deterministic projection and links to the unchanged local
JSON. That local link requires this checkout's retained build output; the report
presentation itself is self-contained. Do not recreate original exports from the
projection. Engine source, fixtures and audio remain outside the deliverable.

## Display and field mapping

Paths below are relative to the first product in the CLI JSON array.

| Presentation | Actual value | Original field |
| --- | --- | --- |
| Headline | No strong lossy indicators; uncalibrated reference method; source history unverified | `reference_assessment.display_summary` (qualified summary split typographically) |
| Source | ABBA; Gold (Greatest Hits); FLAC, 44,100 Hz, 16 bit, 2 channels | `metadata.named_tags` indexes into `entries`; `metadata.technical` |
| Full-file coverage | 0–186.7870068027211 s; 8,237,307 frames; end reached | `measurement_report.coverage` |
| Overall DR | 8.475130081176758 dB (drmeter), stored label DR8 | `tool_statistics.measurements.dr.overall`, `overall_integer_label` |
| Channel DR | index 0: 8.49225902557373; index 1: 8.458000183105469 | `tool_statistics.measurements.dr.channels[].numeric_dr` |
| Integrated loudness | −10.632156049767328 LUFS | `measurement_report.loudness.integrated_lufs` |
| Loudness range | 8.54 LU; histogram bounds 8.53–8.55 LU | `measurement_report.loudness.range` |
| Estimated true peak | index 0: +0.3193362699464511; index 1: +0.025199253177966036 dBTP | `measurement_report.true_peak[].estimated_peak_dbtp` |
| Reference cutoff | 21,748.53515625 Hz | `reference_assessment.features.cutoff` |
| Exercised bits | 16 in both native channels | `measurement_report.channels[].exact_used_bits`; reference depth candidates retain unavailable/masked noise-floor qualification |
| MQA sync | not_detected; 132,300 frames; metadata_evaluated false | `measurement_report.mqa` |
| Five source candidates | all matched false | `reference_assessment.source_candidates[]` |
| Assessment availability | available; missing_inputs empty | `reference_assessment.status`, `missing_inputs` |
| Ancestry | INCONCLUSIVE; evidence index null | `measurement_report.ancestry_verdict`, `evidence_index` |

The cutoff band is a frequency scale, 0–22.05 kHz (sample-rate/2), with the stored
cutoff marked at 21.749 kHz. It is not a quality score, probability or spectrogram.
Formatting rounds display values; the expanded evidence keeps original numbers.
Channel 1/2 labels mean zero-based engine channel indices 0/1, not inferred routing.

## Historical revision-3 layout (superseded by revision 4)

Default normal extent is a proposed 56% of measured workspace height, freely
draggable to compact or the full available height. It is not an expansion cap.
The review-only Sheet view selector demonstrates Normal and Fully expanded;
there are no app resize buttons. Both views use the same report and preserve
scroll/disclosure state, avoiding duplicate or contradictory summary layouts.

Normal: identity and achieved coverage first, then the qualified finding and
three aligned numeric cells (DR, integrated loudness, loudness range). These are
measurements, without star ratings, quality colors or a composite score. The
first evidence item reports estimated peaks with the explicit non-clipping limit.

Expanded: the same summary followed by peak evidence, a scaled frequency band,
exercised-bit evidence and the limited MQA scan. Expandable sections expose
channel dynamics, peak method caveats, each source candidate's rule states and
operands/features, detector coverage, source metadata and original fields.
The report scrolls inside the existing bottom sheet. No report page is added.

Do not promote “no strong lossy indicators” into lossless authenticity. The
4× FIR peak estimate is not a clipping/audibility diagnosis; filter boundary
effects and unverified certified-meter conformance remain available. DR, crest
and LRA are distinct. Noise-floor qualification remains attached to depth.
MQA covers only the first three seconds, not the entire track.

Full-file decode also does not imply identical detector windows: cutoff covers
0–8,237,056 frames; loudness complete windows end at 8,233,470 (3,837 trailing
frames); MQA ends at 132,300. Feature-specific domains/intervals stay inspectable.
Missing values remain unavailable, never zero. In revision 3, reference scores were advanced-only. Revision 4 follows the owner's
new brief: a visible qualified score beside the assessment, never a probability gauge.

## Failed candidates retained honestly

The first full-file trial, `04 - The Hardest Thing (feat. Tony Allen).flac`,
returned exit 1 / failed / resource_limit after 7.681 s: “Reference input resource
limit: HF FFT scratch exceeds 2N complex samples”. Its actual failed result is
the prototype's failure example; no successful finding is fabricated. Retained
locally as `sample-failed-product.json`, `sample-failed-receipt.json` and log.

`11 - The APL Song.flac` returned exit 1 / unsupported: “Container support limit:
native FLAC, RIFF/WAVE or M4A ftyp signature required”. The filename extension
alone does not establish format. Retained as `apl-failed-product.json` and receipt.
These runs did not change files, engine limits, dependency pins or source code.

This supplies one complete successful specimen and real failure cases. Positive
flagged, prefix, partial-availability, mixed-batch and unknown-version acceptance
fixtures remain future U6 work. Owner visual acceptance, browser gesture checks,
Android tests/build/CI/device checks and A07/A08 remain unclaimed.
