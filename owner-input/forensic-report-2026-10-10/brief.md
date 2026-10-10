# Alfred — Forensic Report UI: Presentation & Visual Design

## Objective

I want to substantially redesign the **Audio Forensics report presentation** in Alfred.

The current U1 prototype has improved structurally, but the report still has a serious presentation problem: the overview contains some promising elements, while the expanded sections resemble a developer debugging interface.

Raw JSON objects, unexplained frame offsets, internal variable names, rule predicates, excessive decimal precision, and implementation-specific disclaimers dominate the deeper sections.

**This is unacceptable for the finished application.**

Alfred is intended to be a sophisticated, free, power-user audio utility. Its forensic engine is one of its defining features, and **the presentation of its findings must be one of the application's biggest selling points**.

I want users to feel that they're exploring a serious audio-forensics laboratory, not reading a serialized Rust report.

Importantly, this does NOT mean simplifying the engine's output into a handful of badges and a basic MediaInfo-style summary.

I want the opposite: make the sophistication of the analysis visible, accessible, visually engaging, and genuinely rewarding to explore.

The design must appeal to technically experienced audiophiles, audio engineers, archivists, and people interested in signal analysis—without requiring them to understand Alfred's internal implementation.

For this stage, work on the existing **HTML U1 prototype and its relevant UI documentation**. Do not begin Android implementation.

---

## 1. Core Design Philosophy

The report should follow three principles:

**Measurement — What did Alfred observe?**

Show the actual measured value, its units, and useful context.

**Interpretation — What does that measurement suggest?**

Explain its significance accurately, including the distinction between suggestive evidence and definitive conclusions.

**Evidence — Why did Alfred reach that conclusion?**

Allow users to investigate the underlying measurements, applicable detection rules, thresholds, channel differences, and relevant limitations.

The deeper someone explores the report, the more useful information they should discover.

**Do not confuse scientific depth with implementation detail.**

For example:

Bad:

`MQA window: 0–132,300 frames`

Good:

`MQA signalling: Not detected in the first 3 seconds.`

With appropriate detail:

`The detector examined the first three seconds of the decoded audio. No sync candidates were found. Signalling elsewhere in the track was not examined by this detector.`

A user gains meaningful information from the second and third presentations. The first merely exposes an internal counter.

Likewise, raw JSON, boolean values, null fields, implementation-specific sample encodings, and full-precision floating-point numbers should not dominate the report.

Preserve that information for reproducibility and export, but distinguish machine-readable diagnostics from valuable human-facing forensic evidence.

---

## 2. Visual Direction — Reproduce the Provided Reference

I have a visual reference from a ChatGPT-rendered forensic overview. I particularly like its actual UI elements, not just its general design philosophy.

**Use the screenshots I provide as direct visual references.**

I want Alfred to reproduce and develop this visual language rather than substitute a generic Material Design dashboard.

### Typography and hierarchy

Use:

- Strong, confident track titles.
- Understated artist, album, and format information.
- Prominent numerical measurements using tabular numerals.
- Smaller, carefully weighted labels.
- Clear section headings accompanied by compact icons.
- Muted secondary explanations that remain easily readable.
- Compact spacing that preserves information density without looking crowded.

Measurements should have visual prominence. Explanatory text should support them rather than compete for attention.

### Metric cards

Recreate the compact, rounded metric tiles from the reference.

For the sample report, the overview should prominently display:

| Measurement | Display |
|---|---|
| Dynamic range | DR8 |
| Integrated loudness | −10.63 LUFS |
| Loudness range | 8.54 LU |

These should be visually balanced cards with:

- Large, prominent values.
- Small, understated descriptions.
- Subtle surface contrast.
- Consistent corner radii.
- Minimal unnecessary decoration.

The cards should look like precision instrumentation, not marketing statistics.

### Icons

Use a consistent family of **Lucide-style outline icons**.

Examples of appropriate visual associations:

- Audio waveform or audio lines: forensic analysis.
- Activity: dynamics and loudness.
- Fingerprint: codec fingerprints.
- Binary or layers: bit-depth analysis.
- Scan/search: evidence investigation.
- Chevron: expandable sections.
- Checkmark: genuinely verified conditions.
- Information icon: qualifications or contextual explanations.

Icons should reinforce the meaning of a section, not become decorative clutter.

Use consistent stroke weights, proportions, and icon sizes.

### Color and surfaces

The reference uses a restrained visual treatment:

- Subtle rounded surfaces.
- Soft background differentiation.
- Minimal borders.
- Muted secondary labels.
- Clear typography.
- Small, meaningful accents.
- Fine dividers between analytical sections.

Preserve that character.

Support light and dark themes.

Do not turn Alfred into a neon cyberpunk dashboard.

Avoid decorating every measurement with green or red status indicators. Color should communicate meaningful differences, conditions, or anomalies—not arbitrary notions of audio quality.

### Spacing and density

This is an information-dense Android utility, not a spacious desktop landing page.

Design for narrow mobile screens while keeping the report attractive.

Avoid:

- Huge empty cards.
- Excessive vertical padding.
- Multiple repeated explanatory paragraphs.
- Oversized buttons.
- Labels that consume more space than their measurements.
- Decorative graphics that displace useful findings.

The result should look sophisticated even on a 320 px viewport.

---

## 3. Overall Report Architecture

Build the forensic report as a **single continuous, scrollable investigation** inside Alfred's existing persistent expandable bottom sheet.

There are two major layers.

### Layer A — Rich Visual Overview

The overview is immediately visible when a completed report is opened.

It must deliver a strong first impression and summarize the most important findings without oversimplifying them.

### Layer B — Detailed Investigation

Below the overview, users can explore expandable sections containing increasingly detailed, carefully designed technical evidence.

These sections should remain visually coherent with the overview.

Expanding a section must reveal a thoughtfully organized analytical presentation—not a raw table or serialized object.

The report must remain usable in both normal and almost-full-screen bottom-sheet states.

---

## 4. Overview — Required Visual Components

Use the existing real sample report:

**ABBA — Money, Money, Money**

Album: Gold (Greatest Hits)

Format: FLAC

Sample rate: 44.1 kHz

Bit depth: 16-bit

Channels: Stereo

Duration: approximately 3:06.787

### A. Track identity

Start with a polished report header.

Prominently show:

**Money, Money, Money**

Then show ABBA and Gold (Greatest Hits) with a subdued typographic hierarchy.

Present FLAC, 44.1 kHz, 16-bit, stereo, and duration as compact supporting information.

An appropriate outline icon may complement this header.

Avoid turning the header into an oversized cover page.

### B. Forensic assessment

Give the assessment a visually distinctive but restrained presentation.

The current reference assessment reports:

- No strong lossy indicators.
- Reference score: 0.
- Lossy indicators: 1.
- Natural indicators: 5.
- Native ancestry: INCONCLUSIVE.
- Reference method: uncalibrated.

Clearly distinguish the reference heuristic output from the native ancestry assessment.

A zero reference score is not proof that a source is genuine, nor a calibrated probability of lossless ancestry.

An appropriate primary message would be:

**No strong lossy indicators**

Supporting text:

**Uncalibrated reference assessment. Original source history remains unverified.**

Preserve the actual ancestry status without burying or contradicting it.

Do not invent scientific certainty.

### C. Key measurements

Display the prominent metric-card trio from the visual reference:

**DR8 | −10.63 LUFS | 8.54 LU**

Use tabular numbers, compact cards, clear units, and restrained styling.

These three measurements describe different properties. Their visual grouping must not imply they are interchangeable.

### D. Spectral cutoff visualization

Create the slim, carefully labeled frequency bar shown in the reference.

Actual values:

- Reference cutoff: 21.748535 kHz.
- Nyquist: 22.05 kHz.
- Reference spectral cliff: approximately 8.10 dB.

The bar should place the measured cutoff relative to the frequency limit.

Show meaningful labels, including the measured cutoff and Nyquist.

Explain the significance of the measurement: the reference cutoff lies close to Nyquist, and the current reference rules do not indicate a strong low-pass signature.

**Do not invent a frequency spectrum from a single cutoff value.**

If the underlying report does not contain plotted spectral-bin data, a correctly labeled cutoff-position bar is appropriate. An invented FFT graph is not.

### E. Channel-by-channel true-peak comparison

Recreate the paired measurement cards from the reference.

Actual estimates:

**Left: +0.319 dBTP**

**Right: +0.025 dBTP**

Make the channel comparison visually immediate.

Explain that these are 4× FIR interpolation estimates above 0 dBTP, not definitive evidence of clipping or audible distortion.

When a measurement is estimated, preserve that qualification.

### F. Codec fingerprint overview

Create a compact, visually organized collection of the most relevant codec-related results.

For this sample:

- AAC reference fingerprint: approximately 0.0153.
- Vorbis reference fingerprint: 0.
- MQA sync: not detected within the first three seconds.
- Resampling candidate: not indicated by the reference rules.

Display the measured values where useful.

Do not manufacture arbitrary progress bars or confidence percentages from detector outputs that have not been calibrated that way.

Different detectors have different units, domains, and thresholds. Their visual treatments should respect those differences.

### G. Bit-depth overview

Display the actual bit-depth observation:

**16 bits exercised on both channels**

The source file declares 16-bit precision.

The reference measurements exercise all 16 bits on both channels, but do not establish the original recording's source depth.

Show declared versus exercised precision clearly, with per-channel details available below.

### H. Significant findings

The overview should adapt to the analyzed file.

If the engine finds strong codec evidence, suspicious spectral behavior, unusual bit-depth properties, resampling signatures, or other meaningful anomalies, those findings should receive more prominence.

A track without strong lossy indicators should still produce an interesting overview of its actual measured properties.

Do not use the exact same fixed collection of warnings and empty sections for every result.

---

## 5. Detailed Investigation Sections

Below the overview, build expandable analytical sections.

Use the same visual language as the overview: refined typography, carefully placed outline icons, rounded measurement elements, informative visual comparisons, and compact explanatory text.

### 5.1 Spectral Investigation

Include the relevant available measurements and observations:

- Reference cutoff.
- Per-channel cutoff measurements.
- Spectral cliff depth.
- Spectral sharpness.
- Bandwidth characteristics.
- High-frequency energy indicators.
- Spectral envelope behavior.
- Banding and sparsity indicators.
- Segment-based cutoff observations.
- Relevant adaptive-wall evidence.
- Supported source-history interpretations.

Make the section genuinely analytical.

For example, channel cutoff measurements can be compared side by side.

The relationship between cutoff frequency, Nyquist, and spectral-cliff strength can be presented visually without inventing a complete spectrum.

For segment-based findings, use actual measured intervals and meaningful time positions rather than exposing unexplained frame indices.

Explain which observations matter, why they matter, and whether they contributed to an assessment.

Do not label every ordinary spectral variation as suspicious.

If the report lacks sufficient data for a particular visualization, use accurate numerical comparisons or another appropriate presentation.

### 5.2 Dynamics Laboratory

This should be a particularly polished section.

Include:

- Overall DR.
- Stored DR label.
- Per-channel DR.
- Integrated LUFS.
- Maximum momentary and short-term loudness.
- Loudness range.
- Per-channel crest factors.
- Sample peaks.
- Estimated true peaks.
- RMS measurements where available.
- Relevant channel differences.

For the sample report:

Overall DR: 8.475 dB.

Left DR: 8.492 dB.

Right DR: 8.458 dB.

Integrated loudness: −10.632 LUFS.

Loudness range: 8.54 LU.

Momentary maximum: −5.228 LUFS.

Short-term maximum: −7.064 LUFS.

Left crest factor: 13.889 dB.

Right crest factor: 13.662 dB.

Present the relationships between these measurements intelligently.

Use compact channel-comparison cards, aligned values, proportional visualizations where mathematically appropriate, and well-designed measurement groups.

Do not display an invented loudness-over-time graph unless the engine exposes a genuine time series.

Preserve method-specific distinctions.

DR, crest factor, true peak, and LRA are not interchangeable, and none is automatically a mastering-quality grade.

### 5.3 Codec Forensics

Provide detailed but understandable examination of codec-related evidence.

Potential categories include:

- AAC fingerprint evidence.
- Vorbis fingerprint evidence.
- MQA signalling.
- Resampling candidates.
- Other relevant codec-specific observations supported by the engine.

For each detector, show:

**Observation:** What measurement did the detector produce?

**Evaluation:** How does that value relate to the actual detection criteria?

**Finding:** Did the applicable rule trigger?

**Significance:** What does the result support or fail to establish?

**Scope:** Was the detector examining the whole track or a limited region?

For AAC and Vorbis, users investigating the results should be able to examine actual fingerprint values and relevant thresholds.

For MQA, show the actual scan scope in seconds.

For resampling, show which relevant indicators were evaluated.

Do not repeat general disclaimers under every measurement. Explain specific limitations where they affect interpretation.

### 5.4 Bit-Depth Investigation

Make declared versus measured precision easy to understand.

Include:

- Container precision.
- Exercised bits.
- Per-channel results.
- Available noise-floor measurements.
- Relevant depth-candidate findings.
- Whether depth evidence supports a meaningful conclusion.

Show the left and right channels in a comparable layout.

If a noise-floor estimate is masked, unavailable, or unsuitable for inferring original source depth, state that clearly.

Do not convert an effective-bit measurement into proof of original recording or mastering depth.

### 5.5 Evidence Explorer

This is where Alfred can go beyond ordinary audio-information tools.

The evidence explorer must connect measured observations to the forensic assessment.

For each relevant rule or hypothesis, make it possible to understand:

**Measured evidence → Applied threshold or condition → Rule outcome → Assessment effect**

For example, if an AAC reference fingerprint does not reach its triggering threshold, explain that using the actual value and actual applicable threshold.

If an applicable rule contributes evidence, show what triggered it and what contribution it made.

If a rule was excluded by the reference contract, unavailable because of missing inputs, or not triggered, distinguish those states correctly.

Rule IDs can be available for people who want to trace the result, but they should not be the primary language of the interface.

Don't reproduce raw Python predicates as paragraphs or paste entire feature objects under accordions.

Instead, design a genuinely useful forensic investigation interface.

Include pertinent source-candidate findings, such as cassette, vinyl, resampling, bandwidth, and independent high-frequency evidence, when supported by the report.

Avoid suggesting that a source candidate being unflagged proves that source history impossible.

---

## 6. File Properties and Verification

Keep useful file and verification information accessible, but do not allow it to dominate the forensic investigation.

The sample report records:

- Full-track decode completed.
- Header duration matches.
- Decoder verification passed.
- Approximately 3:06.787 of audio decoded.
- Detector-specific coverage limitations.

Present this as meaningful verification information.

For example:

**File verification: Passed**

**Audio decoded: Entire track**

Then show detector-specific scope only where relevant.

The current UI displays entries such as:

`Reference cutoff window: 0–8,237,056 frames`

`Loudness complete windows: 0–8,233,470 frames`

`MQA window: 0–132,300 frames`

These are not appropriate default user-facing descriptions.

Translate meaningful intervals into ordinary time units.

More importantly, decide whether the interval belongs in the interface at all.

The fact that the final loudness window excludes a tiny fraction of a second may be worth mentioning in a measurement-method explanation, but it does not automatically deserve a prominent report row.

The MQA detector's first-three-second limitation is important and should be visible alongside its finding.

Do not add vague implementation explanations such as:

"A full-file decode does not give every detector identical coverage. Each evidence field retains its own interval and domain."

Explain the consequences of limitations, not the architecture of the engine.

---

## 7. Technical Depth Without Raw-Data Pollution

I do not want to sacrifice expert-level detail.

But **expert detail and raw serialization are different things**.

Preserve useful numerical precision, per-channel measurements, detector thresholds, evidence scopes, rule outcomes, and measurement limitations.

Do not automatically render raw JSON inside expanded analytical sections.

The current prototype does this repeatedly, including inside:

- Peak evidence and method limits.
- Source candidates.
- Coverage and verification.
- Reference method and original fields.

Replace these presentations.

The original machine-readable report should remain available separately through an explicit export or raw-data inspection action.

For reproducibility, preserve access to:

- Report and decoded-audio hashes.
- Exact intervals and frame boundaries.
- Engine and method versions.
- Relevant rule IDs.
- Complete original report contents.

But ordinary forensic investigation should not require reading them.

If a technical detail does not help the user understand, interpret, verify, or reproduce a finding, it does not deserve space in the normal report.

Avoid repeating already displayed values in progressively uglier formats.

---

## 8. Scientific Integrity and Responsible Visualizations

Visual beauty cannot come at the expense of scientific accuracy.

Requirements:

- Never fabricate measurements.
- Never fabricate frequency spectra.
- Never fabricate waveforms.
- Never invent loudness histories from integrated statistics.
- Never turn uncalibrated detector scores into probabilities.
- Never convert absence of a detected signature into proof of genuine ancestry.
- Never confuse container losslessness with source-history authenticity.
- Never interpret estimated true-peak overs as definitive audible clipping.
- Never conceal detector scan limitations that materially qualify a finding.
- Never present unavailable measurements as zero.
- Never treat a rule excluded by contract as equivalent to a negative measurement.

The reference method remains uncalibrated.

The sample's native ancestry status is INCONCLUSIVE.

The overview must preserve these distinctions even when the reference method reports no strong lossy indicators.

If a graph requires data the engine currently does not expose, design a scientifically valid alternative or mark the graph as a future capability in documentation.

Do not invent data to make the interface look impressive.

---

## 9. Reusable Visual Components

Develop the report presentation around reusable components rather than a collection of unrelated one-off designs.

Potential components include:

**MetricTile**

Prominent numerical measurement with a compact label and optional contextual note.

**MeasurementScale**

Precisely labeled bar or scale representing a meaningful numerical relationship.

**ChannelComparison**

Side-by-side left/right measurements with aligned units and meaningful differences.

**AssessmentPanel**

Prominent forensic interpretation with accurate scientific qualifications.

**EvidenceFinding**

A detector observation presented with its evidence, interpretation, and significance.

**DetectorDetail**

Expandable technical explanation containing actual measured values, applicable criteria, and rule outcome.

**InvestigationSection**

Consistent analytical section header, outline icon, secondary description, and expandable content.

**TechnicalDataAction**

An explicit pathway to original report data, without embedding raw JSON in ordinary evidence sections.

These names are conceptual. Adapt them to the implementation language and established project architecture.

The key requirement is visual consistency across the entire forensic report.

Eventually, the same visual system should be reusable throughout Alfred's Metadata, Converter, Log Checker, and other tools where appropriate.

For now, do not implement those unfinished tools.

---

## 10. Mobile and Bottom-Sheet Behavior

Preserve the existing workspace architecture.

Specifically:

- One compact main toolbar.
- The existing expandable quick-controls row.
- The existing horizontally and vertically swipeable spreadsheet.
- Resizable spreadsheet columns.
- Shared file selection.
- The persistent expandable bottom sheet.
- Near-full-screen bottom-sheet expansion.
- Main categories: Forensic, Metadata, Log, Convert.
- Directly accessible Audio Forensics, Spectrogram, and Compare sub-tabs within Forensic.

Do not redesign these areas as part of this task.

The forensic report should scroll naturally within the bottom sheet.

Expanding and collapsing investigation sections must not create unexpected navigation, reset the report, or unnecessarily rearrange the workspace.

Test normal and fully expanded sheet heights.

Test at:

- 320 px width.
- 400 px width.
- Large text settings.
- Light theme.
- Dark theme.

Pay particular attention to:

- Numerical labels.
- Long track titles.
- Measurement cards.
- Two-column channel comparisons.
- Icon alignment.
- Expandable technical sections.
- Horizontal overflow.

Avoid clipping, tiny unreadable labels, and excessive unused space.

---

## 11. Adaptive Investigation Layout

Do not design an identical fixed report for every audio file.

Different findings should produce different investigative emphasis.

Examples:

**Strong AAC evidence:** Highlight the codec fingerprint and explain the relevant detection criteria.

**Suspected resampling:** Give the resampling observations and supporting spectral evidence prominence.

**MQA signalling:** Present the signalling evidence, scan scope, and limitations clearly.

**Bit-depth mismatch:** Emphasize declared versus exercised precision and supporting measurements.

**No strong lossy indicators:** Present the relevant spectral, dynamic, codec, and bit-depth observations without manufacturing anomalies.

All reports should share a coherent visual identity, but their emphasis should follow the actual findings.

This should feel like a forensic investigation tailored to the analyzed file, not a static form filled with numbers.

---

## 12. Deliverables and Review Requirements

For this stage, produce:

1. An updated HTML U1 prototype implementing the complete redesigned report.
2. The rich visual overview using the real ABBA sample.
3. The complete detailed investigation sections.
4. Working expand/collapse interactions.
5. Responsive behavior for normal and fully expanded bottom-sheet states.
6. Updated relevant UI specifications documenting the visual system.
7. An intelligible evidence presentation that preserves scientific traceability.
8. Explicit access to original technical data without automatic JSON dumps in the report body.

### Self-review before presenting the result

Inspect the full overview.

Then expand **every** investigation section and inspect its contents.

Do not stop after checking the initial viewport.

Ask:

- Does each section contain useful forensic information?
- Can an expert understand the measurement and its implications?
- Does the presentation distinguish actual evidence from inference?
- Is the information organized visually rather than dumped?
- Does any section still resemble a debug console?
- Are any charts unsupported by the available data?
- Are there redundant explanations or repeated numbers?
- Is the layout attractive and readable on a narrow phone?
- Does expanding the bottom sheet enhance the experience?

If a section still displays raw predicates, large serialized objects, incomprehensible frame counters, or developer-oriented implementation notes as its primary content, it is not finished.

### Final instruction

**Take creative initiative with presentation, but remain strictly faithful to the actual forensic data and established workspace design.**

The visual reference demonstrates the kind of polished component styling I want. Use it as a foundation, then build an even richer forensic investigation experience around it.

Do not settle for a handful of generic cards.

Do not hide most of the engine's sophistication.

Do not fill the interface with raw internal data.

Make the analysis itself enjoyable to explore.

**Alfred's forensic report should look and feel like a professional audio-forensics instrument that just happens to fit in someone's pocket.**

Show me the complete prototype for approval before Android implementation.