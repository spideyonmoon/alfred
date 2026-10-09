# Alfred handoff

Updated 2026-10-09. Owner requested standalone extraction to
`C:\Users\Bishal\code\alfred`, https://github.com/spideyonmoon/alfred.
Branch codex/extract-android; [PR1](https://github.com/spideyonmoon/alfred/pull/1).
The owner is drafting UI on a scratchpad. Existing scaffold's visual quality was
rejected; leave visuals unchanged until those designs arrive.

## Current extraction and checks

Android app/shared/three features/adapter/build drivers/harnesses are at this
repository root. Independent engine 0.32.0 stays in audio-forensic-rust, pinned
to 5c5ce00d44f6759dd6a7319804b5f7a21079d1b4 in native manifest, lock and
core-dependency.json. Ignored clean .core checkout supplies unchanged generated
fixtures; Cargo resolves engine source through its own Git cache. No vendoring.
GPLv3 destination license preserved; extracted MIT notice retained. No private
audio or private signing keys in source. Core-side removal/pointers pushed at
0a41632; [core PR1](https://github.com/spideyonmoon/audio-forensic-rust/pull/1).
Core engine/schemas/fixtures are unchanged; old local caches and owner cleanup
edits preserved. Original history remains in the core repository.

Static dependency/83 unchanged lock entries/source/syntax/link/whitespace checks
passed. Initial Kotlin/lint passed 5m57s (0 errors/12 warnings/4 hints). Linux core
CLI/no-CLI and adapter controls passed. Windows host dependency linking failed
with collect2/ld exit204 including the documented GCC retry; no further retries.
Both extracted ABI APK hashes/app+engine receipts and initial docs source reuse
were verified. These initial APKs predate the lifecycle correction below.

First combined CI 37888964074 passed build and six APIs; API31 crashed during
ViewerCompare controls with ForegroundServiceDidNotStartInTimeException.
App-only shutdown/promotion fix d550360 passed final Kotlin/lint (2m48s);
corrective CI37890896503 exposed transient busy admission and was superseded.
Separate idleStopping refinement compiled in 52s. Source84355e7 is pushed;
targeted CI37892173286 passed both builds and API30/31. API32 passed all
backend controls then failed one-shot PNG viewport detection. Harness now scrolls
to the actual image. Next full-harness reuse on API32–36 with the same APK; see the extraction record. Do not accept runtime yet.
See task-results/EXTRACTION.md for exact evidence and inferred race explanation.
Next: final compile, targeted build/API30–32 acceptance, then source-bound full
harness reuse for API33–36 and receipt/hash/closure checks. Do not mark extraction runtime accepted yet.

A07 physical ARM64/resources/700MiB/thermal/16KiB runtime and A08 identity/signing/
release remain pending. No release publication or UI redesign is included.

Standalone manual-only runtime workflow dispatch returned HTTP404 because this
new repository's default branch still contains only its initial license; GitHub
has not indexed that workflow yet. The already-indexed full workflow now accepts
reuse_build_run: it skips compilation, verifies exact source/core/APK bindings,
then runs the selected full emulator harness. Use that gateway until default-branch
integration. No repository settings or main branch changed; no rebuild needed.
Next dispatch alfred-android.yml with reuse_build_run=37892173286 and
apis=[32,33,34,35,36].
