# U1 report evidence and translation ledger

2026-10-10. Evidence ledger for [report refinement](UI_REPORT_REFINEMENT_PLAN.md).
Revision 4 consumes this ledger; see [review and validation](UI_REPORT_REVIEW.md).
Not a new detector policy. All sample values and rule states below were read from
the actual pinned-engine product; the user brief/screenshots are visual direction,
not the evidence source. Read [provenance](UI_REPORT_SAMPLE.md) for hashes/run details.

## Binding and component gates

Product `audio-forensic-product-v1`; engine 0.32.0; measurement 0.18.0,
`observations-only-v18`; reference assessment 1 / contract 1 /
`python-reference-c6ecce2-v1`; tool statistics 1 / contract 1 /
`ffmpeg-7.1.1-sox-14.4.2-v1`; metadata 1 / contract 1.
Rule descriptions below apply only to this supported reference method. Unknown
versions retain supported components and original access, without a trusted
translated verdict. Never execute raw predicate strings.

Paths are relative to array item 0 of `build/ui-plan/sample-product.json`.
Reference features carry their own availability, unit, domain, channel indices
and intervals. Do not give all detectors one shared scope.

## Overview and deep-measurement map

| UI observation | Original path | Sample / readable display | Qualification / deeper destination |
| --- | --- | --- | --- |
| Title, artist, album | `metadata.named_tags` → `entries[index].value` | Money, Money, Money / ABBA / Gold (Greatest Hits) | Do not assume uppercase tag keys or unique entries |
| Format/rate/precision | `metadata.technical` | FLAC / 44.1 kHz / 16-bit / 2 channels | Container declaration is not source authenticity |
| Decoded duration | `measurement_report.coverage.end_seconds` | 186.7870068027211 → 3:06.787 | Start 0, end reached; no requested prefix |
| Qualified finding | `reference_assessment.display_summary` | No strong lossy indicators | Uncalibrated reference method; source history unverified |
| Reference score | `reference_assessment.scores.main` | 0 | Heuristic, not probability; do not use raw_lossy_pct as confidence |
| Lossy / natural | `reference_assessment.scores.lossy`, `.natural` | 1 / 5 points | Point totals, not counts of detectors or hypotheses |
| Native ancestry | `measurement_report.ancestry_verdict`, `.evidence_index` | INCONCLUSIVE / unavailable index | Separate from reference summary |
| DR label / numeric | `tool_statistics.measurements.dr` | DR8 / 8.475130081176758 → 8.475 dB | Stored overall label; not crest or LRA |
| Channel DR | `tool_statistics.measurements.dr.channels[].numeric_dr.value` | 8.492259 / 8.458000 → 8.492 / 8.458 dB | Native lanes converted to f32; 3-second RMS/peak histograms |
| Integrated loudness | `measurement_report.loudness.integrated_lufs` | −10.632156 → −10.63 LUFS | Native-channel K-weighted programme measurement |
| Momentary / short-term maximum | `measurement_report.loudness.momentary_max_lufs`, `.short_term_max_lufs` | −5.227620 / −7.064497 → −5.228 / −7.064 LUFS | Grid-sampled maxima; not a time series |
| Loudness range | `measurement_report.loudness.range.range_lu` | 8.54 LU | Histogram quantization bounds 8.53–8.55; not perceptual confidence |
| Channel crest | `measurement_report.channels[].crest_factor_db` | 13.888734 / 13.661811 → 13.889 / 13.662 dB | Distinct from DR; no mastering-quality label |
| Native RMS | `measurement_report.channels[].rms` | 0.197509938 / 0.202731627 → 0.197510 / 0.202732 linear | Linear normalized amplitude; dB conversion, if used, must be labelled derived |
| Sample peak | `measurement_report.channels[].peak` | 0.977294922 / 0.977264404 linear | Full-track native sample peaks |
| Estimated true peak | `measurement_report.true_peak[].estimated_peak_dbtp` | +0.319336 / +0.025199 → +0.319 / +0.025 dBTP | 4× FIR estimate; not proof of clipping/audibility |
| Reference cutoff | `reference_assessment.features.cutoff` | 21,748.53515625 Hz → 21.749 kHz | Reference mid; do not substitute native channel cutoff |
| Nyquist axis | declared sample rate / 2 | 22.05 kHz | Derived frequency limit; no invented spectrum |
| Native channel cutoff | `measurement_report.channels[].spectral.cutoff_p95_hz` | 21,920.80078125 / 21,851.35620117187 Hz | Native per-channel statistic, not same estimator as reference cutoff |
| Reference cliff | `reference_assessment.features.cliff` | 8.104736328125 → 8.10 dB | Store exact field in technical access |
| Reference sharpness | `reference_assessment.features.sharpness` | 1.0033111572265625 → 1.003 dB/bin | Bin-dependent quantity, not a dB frequency slope |
| HF magnitude ratio | `reference_assessment.features.hf` | 0.007531738573286371 → 0.00753 | Magnitude ratio; not energy, probability or percent confidence |
| Envelope correlation | `reference_assessment.features.envelope` | 0.2544576406781989 → 0.254 | Signed Pearson; do not relabel absolute correlation |
| Banding / sparsity | `reference_assessment.features.banding`, `.sparsity` | 0.601827 ratio / 0.002206 fraction | Different detector quantities; no shared confidence meter |
| Reference spectral bound | `reference_assessment.features.bound` | 22,037.186988 Hz → 22.037 kHz | Not original sample rate |
| Reference noise | `reference_assessment.features.noise` | −43.04980018837297 → −43.05 raw FFT magnitude dB | Not a dBFS noise-floor measurement |
| AAC / Vorbis fingerprint | `reference_assessment.features.aac`, `.vorbis` | 0.015306122449 → 0.0153 / 0 → 0.0000 | Ratios; thresholds/outcomes in R29/R30 |
| Resampling | `source_candidates[id=resampling]`; features `resample`, `resample_wall` | matched false; 0 Hz (no hit); wall false | No rule indication, not proof of no resampling |
| MQA | `measurement_report.mqa` | not_detected; 0 sync matches | First 3 s only; metadata_evaluated false; no authentication claim |
| Reference exercised depth | `reference_assessment.features.effective_bits_channel_0/1` | 16 / 16 bits | First 30 s; original depth unverified |
| Native exercised depth | `measurement_report.channels[].exact_used_bits` | 16 / 16 bits | Whole decoded interval; distinguish from first-30s reference estimate |
| Reference depth floor | `reference_assessment.features.depth_floor` | −46.83231073044597 → −46.83 dBFS | First 30 s, native channel mean; interpreted as masked, not missing measurement |
| Reference floor color | `reference_assessment.features.depth_color` | −39.05763948213011 → −39.06 dB | Reference depth feature, distinct from native channel color |
| Native channel floor observation | `measurement_report.noise_floor[].nonzero_rms_p015_dbfs` | −47.760946 / −44.151894 → −47.76 / −44.15 dBFS | First-30s low-percentile nonzero block RMS; not proof of recording noise floor |
| Source candidates | `reference_assessment.source_candidates[]` | cassette/vinyl/bandwidth/resampling/independent_hf all matched false | Preserve each rule's state and missing inputs; no history exclusions |
| File verification | `measurement_report.coverage` | reached_end, length_matches_header, decoder_verification all true | Decoding checks only, not authenticity |

The two channel indices are 0 and 1. Verify decoder ordering before presenting
Left/Right; otherwise use Channel 1/2. Do not infer surround layouts from counts.
Precision tiers: overview ~2 decimals; cutoff kHz / peaks / detailed dynamics ~3;
fingerprints 4–5 significant decimals; original exact numeric tokens remain in
unchanged JSON. Do not round a positive tiny value into an unqualified exact zero.

## Scope ledger

| Measurement | Exact stored scope | Ordinary presentation |
| --- | --- | --- |
| Decoded stream, native channel amplitudes and true peak | 0–8,237,307 frames at 44,100 Hz | Entire track, 3:06.787 |
| Reference cutoff | 0–8,237,056 frames | Approximately entire track; exact last boundary in technical access |
| Loudness complete-window union | 0–8,233,470 frames; 3,837 trailing frames | Complete loudness windows; ~87 ms tail excluded, in method note |
| AAC reference feature | 0–7,938,000 frames | Search within first 3 minutes; sparse eligible probes, not every instant |
| Vorbis reference feature | 0–7,938,000 frames | First 3 minutes; reference reconstruction, not a whole-track absence claim |
| MQA scan | 132,300 frames | First 3 seconds; zero sync candidates |
| Reference depth and native noise-floor observation | 0–1,323,000 frames | First 30 seconds |
| Spectral probes | Twelve individually stored 88,200-frame intervals | Twelve measured 2-second excerpts; ten eligible for wall voting |

Use frame/rate conversion for display only; retain original bounds in explicit
technical access. Charts must not bridge unmeasured gaps as though observed.

## Measured spectral excerpts

Source: `reference_inputs.segments.probes[]`. Preserve these in the projection
in the new section. Revision 4 now embeds the complete probe structure,
compared directly against the unchanged original report. Every stored wall_observed is false. Adaptive wall is
16.5 kHz, ten eligible probes, zero eligible walled probes, majority false.
The two excluded probes remain observations, not counted as negative votes.

| Excerpt | Time interval (seconds) | Cutoff (kHz) | Cliff (dB) | Eligible for wall vote |
| --- | --- | --- | --- | --- |
| 1 | 0.000–2.000 | 21.267 | 6.670 | No |
| 2 | 16.799–18.799 | 19.210 | 1.387 | Yes |
| 3 | 33.598–35.598 | 17.558 | 4.800 | Yes |
| 4 | 50.396–52.396 | 19.590 | 0.588 | Yes |
| 5 | 67.195–69.195 | 19.553 | 2.472 | Yes |
| 6 | 83.994–85.994 | 19.901 | 1.325 | Yes |
| 7 | 100.793–102.793 | 18.738 | 1.079 | Yes |
| 8 | 117.592–119.592 | 19.397 | 1.644 | Yes |
| 9 | 121.630–123.630 | 18.447 | 1.617 | Yes |
| 10 | 134.390–136.390 | 17.768 | 0.884 | Yes |
| 11 | 151.189–153.189 | 18.303 | 2.429 | Yes |
| 12 | 184.787–186.787 | 22.029 | Unavailable | No |

Recommended visual: discrete aligned cutoff markers/bars on a labelled common
0–22.05 kHz scale, with time ranges and readable numeric values. No smooth curve,
spectrogram or waveform. Distinguish eligibility with text/shape as well as color.

## Reference-rule translation inventory

The following are version-bound presentation explanations, not runnable logic.
Recorded states/effects below are authoritative for the specimen. A shortened
human explanation must not erase applicability gates; exact original predicates
remain available only via explicit technical data. Sample-specific interpretation
is not a universal rule evaluation for future files.

State language: applied → criterion contributed or produced a label as recorded;
not_triggered → rule did not trigger, not blanket absence of a source history;
excluded_by_contract → outside this reference policy, not measured negative;
unavailable → missing required evidence; vetoed → suppressed by another recorded
rule, with the causal rule retained. Do not collapse these states into pass/fail.

| ID / UI subject | Human-readable criterion | Sample state | Recorded effect |
| --- | --- | --- | --- |
| R01 · Low bandwidth | Cutoff below both 85% of Nyquist and 18.5 kHz adds 2 lossy points. | not_triggered | No score change |
| R02 · Abrupt spectral edge | Sharpness above 15 dB/bin, or cliff above 35 dB with cutoff below 93% of Nyquist, adds 3 lossy points. Otherwise sharpness above 8 or cliff above 20 adds 1. | not_triggered | No score change |
| R03 · Very low HF magnitude | HF magnitude ratio below 0.005 adds 1 lossy point. Do not label a magnitude ratio an energy ratio. | not_triggered | No score change |
| R04 · Above-cutoff noise | Reference FFT-magnitude noise below −70 dB adds 3 lossy points; otherwise below −40 dB adds 1. This scale is not dBFS. | applied | lossy points +1 |
| R05 · Stable low cutoff | Cutoff below 85% of Nyquist together with cutoff variance below 1,000 Hz² adds 1 lossy point. | not_triggered | No score change |
| R06 · Banding / side evidence | Banding above 0.92 together with cutoff below 80% of Nyquist adds 1 lossy point; side evidence above 0.60 adds 2. | not_triggered | No score change |
| R07 · Broadband natural evidence | For non-DSD spectra, three tests add one natural point each: HF ratio above 0.05 with cutoff above 85% Nyquist; noise above −50 on its reference scale; entropy above 8.5 bits with cutoff above 85% Nyquist. | applied | natural points +2 |
| R08 · Smoothness / variation evidence | One natural point each for sharpness below 5 dB/bin; variance above 100,000 Hz² for non-DSD (or above 10,000 with cutoff above 85% Nyquist in the other branch); side evidence below 0.2. | applied | natural points +3 |
| R09 · Initial heuristic combination | Subtract natural from lossy points, floor the net at zero, multiply by 45/14, then use the stored ties-to-even rounded heuristic. Explain the stored result; do not recalculate a new policy. | applied | No score change |
| R10 · Sample-rate conversion | An ordered resampling hit adds 45 to the reference main score. | not_triggered | No score change |
| R11 · Header-based score overrides | Duration +20 and bitrate +25 overrides are excluded by the reference contract. Retained header observations are not negative detector evidence. | excluded_by_contract | Excluded; no score contribution |
| R12 · Preceding-energy evidence | This rule first requires cutoff below 21 kHz or an MP3 profile. Eligible preceding-energy evidence above 10% adds 15, otherwise at least 5% adds 10. A large scalar alone cannot bypass the gate. | not_triggered | No score change |
| R13 · Band-correlation override | The negated absolute-band-correlation override is excluded by contract. The excluded branch would use above 0.5 / at least 0.3; do not present it as a supported mirroring finding. | excluded_by_contract | Excluded; no score contribution |
| R14 · Comb evidence | The psychoacoustic gate and at least two qualifying comb peaks are required to add 10. | not_triggered | No score change |
| R15 · Cassette candidate construction | Cutoff below 19 kHz is required. Hiss above −55 dBFS with correlation below 0.2 adds 30 candidate points; slope strictly between −6 and −3 dB/kHz adds 20, below −10 subtracts 20; no comb adds 15; cutoff standard deviation strictly between 50 and 300 Hz adds 15, below 30 subtracts 10; floor candidate score at zero. | not_triggered | Label/descriptive only |
| R16 · Cassette candidate effect | Cassette candidate score at least 30 with hiss subtracts 40 from main score and selectively vetoes downstream rules. Not a proof of tape origin. | not_triggered | No score change |
| R17 · High-rate bandwidth limitation | Requires no resampling hit, sample rate at least 88.2 kHz, positive cutoff below 60% Nyquist, cliff above 25 dB and measured void below −80 dBFS; adds 20. | not_triggered | No score change |
| R18 · Repeated spectral wall | Adaptive wall may rise from 16.5 kHz to cutoff +400 Hz when cliff exceeds 30 dB, void is below −85 or a fingerprint matches, no resampling wall is present and cutoff is below 22.5 kHz. A qualifying positive at-least-half vote on eligible excerpts adds 55. Preserve stored eligibility and vote; no new inference from the chart. | not_triggered | No score change |
| R19 · Single anomalous region | With no majority wall or cassette veto, one unique anomalous region with cliff above 35 dB and nearest fingerprint adds 25. | not_triggered | No score change |
| R20 · Multiple anomalous regions | Two or more unique regions add 30; four or more add 40; any qualifying void adds 25; a median fingerprint adds 15, subject to recorded applicability/vetoes. | not_triggered | No score change |
| R21 · Strong silence-band ratio | At least 2 seconds of qualifying silence and measured HF ratio above 0.3 add 50 and take an early return. Do not substitute other HF features for the silence ratio. | not_triggered | No score change |
| R22 · Silence / vinyl evidence | Noise below −70 with cutoff below 22.5 kHz and cliff above 25 adds 20. Otherwise noise at least −70, correlation below 0.3 and temporal standard deviation below 5 support the vinyl candidate and subtract 40; 5–50 clicks/minute can subtract another 10. | not_triggered | No score change |
| R23 · Broadband counter-evidence | With no cassette/majority-wall/resampling evidence, wall at most 16.5 kHz, cutoff above 85% Nyquist and silence ratio from zero up to but excluding 0.15, subtract 30 from main score. | applied | main score -30 |
| R24 · Fingerprint plus strong edge | Fingerprint match together with void below −85 or cliff above 30 adds 10, subject to recorded source vetoes. | not_triggered | No score change |
| R25 · Low spectral bound | At sample rates at least 40 kHz, a positive bound below 16.5 kHz adds 25, subject to recorded source vetoes. | not_triggered | No score change |
| R26 · Phase with low sharp cutoff | Cutoff below both 85% Nyquist and 22.5 kHz, cliff above 25 dB and phase entropy above 4.5 bits add 10. | not_triggered | No score change |
| R27 · Spectral sparsity | Cutoff below 95% Nyquist and sparsity above 0.30 add 10. | not_triggered | No score change |
| R28 · Independent high-frequency envelope | Cutoff above 16.5 kHz, positive bound more than 2 kHz below cutoff and envelope correlation below 0.15 add 15. Added noise is only one possible explanation. | not_triggered | No score change |
| R29 · AAC fingerprint | At 44.1/48 kHz without source veto, fingerprint at least 0.10 adds 55; otherwise at least 0.06 adds 15. The actual sample 0.0153 reaches neither threshold. | not_triggered | No score change |
| R30 · Vorbis fingerprint | At 44.1/48 kHz, fingerprint at least 0.03 raises main score to at least 55, independently of the source veto. The sample value is zero. | not_triggered | No score change |
| R31 · Final reference classification | Clamp stored main score to 0–100. The pinned classifier considers parsed lossy codec first, then score at least 86 before resampling/fake branches, then 55/31/11 thresholds. Display the stored qualified summary rather than reimplementing classification. | applied | main score +30 |
| R32 · MQA certainty override | The legacy known-codec / score-100 MQA certainty override is excluded by contract. Sync observations remain separately reportable. | excluded_by_contract | Excluded; no score contribution |
| R33 · Independent depth interpretation | Tests padding of at least eight claimed bits, reduced exercised precision, masked floor above −86 dBFS, floor below −102, and a flat floor at or below −89 with at least 24 declared bits. Preserve the stored qualified per-channel depth statements and first-30-second scope; no main ancestry-point effect. | applied | Label/descriptive only |
| R34 · Descriptive interpretations | Stored scalar interpretations are audit/descriptive outputs. Missing values stay unavailable; native units, normalisation and ReplayGain add no ancestry points. No new scoring from descriptive measurements. | applied | Label/descriptive only |

### Explain this specimen's score without a probability gauge

R04 contributes 1 lossy point. R07 contributes 2 natural points and R08 adds 3.
The net and initial heuristic are zero (R09). Broadband counter-evidence subtracts
30 from the main score (R23); the final clamp brings −30 to 0 (R31). R33/R34 are
label/descriptive steps. Three overrides (R11/R13/R32) are excluded, not negative.

This is why the final reference main score is 0 despite one lossy point. Display
this as a short ordered evidence trail, with access to measured operands and
criteria—not a good/bad balance meter or “100% genuine” conclusion. Main score is
not simply lossy minus natural, and the 1/5 totals are not detector counts.

### Useful explanations to surface

- AAC 0.0153 is below both 0.06 and 0.10 reference triggers. At 44.1 kHz the
  rate gate is satisfied; the sample has no source veto, but no fingerprint trigger.
- Vorbis 0.0000 is below the 0.03 score-floor trigger. This does not exclude every
  possible prior Vorbis encode.
- Reference cutoff 21.749 kHz is above both the low-bandwidth thresholds
  18.5 kHz and 18.7425 kHz (85% of this Nyquist); R01 did not trigger.
- Cliff 8.10 dB and sharpness 1.003 dB/bin do not meet R02's stronger thresholds.
- Preceding-energy scalar 81.63% must not be promoted into a pre-echo verdict:
  R12's initial cutoff/MP3 applicability gate did not trigger on this sample.
- Depth floor is measured at −46.83 dBFS but the depth interpretation regards it
  as masked (above −86 dBFS). “Measured but unsuitable for this inference” is
  different from a missing numerical measurement. Original source depth is unknown.
- The 16-bit reference observations cover first 30 s; native exact-used-bits
  observations separately cover the full decoded track. Avoid combining their scopes.
- The estimated peaks exceed zero but sample peaks remain below full scale;
  neither observation alone establishes audible distortion or an error to fix.

## Technical access and further validation

Keep hashes, exact intervals/frames, engine/method versions, original predicates,
rule IDs and full report in explicit technical-data/export access. The normal
investigation should use named measurements, readable units, time scopes and
criterion/outcome/effect relationships. No serialized objects under ordinary
Spectral/Dynamics/Codec/Depth/Evidence accordions.

Revision 4 extends the projection from the unchanged full report for spectral
probes, native noise-floor measurements and per-detector scopes. Original-byte
hash and projection equality checks passed against the source. Do not rerun old revision-3 generator scripts blindly: they were one-time
transformations and can duplicate declarations/renderers.

This audit supports this one successful file and the retained real failure cases.
Positive/partial adaptive layouts remain unverified until grounded in suitable
real specimens. No new engine execution or Android validation was run for this
planning checkpoint.
