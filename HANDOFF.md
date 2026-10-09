# Alfred handoff

Updated 2026-10-09. Owner requested extraction now into
`C:\Users\Bishal\code\alfred`, remote https://github.com/spideyonmoon/alfred.
Branch `codex/extract-android`. The owner is drafting the UI on a scratchpad;
preserve the current functional scaffold until those designs are supplied.
The owner rejected its visual quality; functional A06 acceptance is not UI approval.

## Current extraction

App/shared/three features/native adapter/build drivers/generated-input harnesses
are at this repository root. Engine remains independently owned in
audio-forensic-rust, pinned to 5c5ce00d44f6759dd6a7319804b5f7a21079d1b4.
Native manifest, lockfile and core-dependency.json must agree. `.core` is an ignored
validation checkout for unchanged generated fixtures, not vendored source.
Build scripts, APK verifier, fixture consumers and CI use standalone paths.
Android licenses/signing defaults/toolchain pins and admission limits are preserved.
GPLv3 destination LICENSE preserved; extracted MIT attribution in LICENSE-MIT.

Original accepted Android 11–16 behavior: core CI 37820435450, 22 feature controls
on every API 30–36 plus retained input/jobs/native/Forensics suites. This historical
evidence does not prove extracted packaging. A07 physical ARM64/resources/16-KiB
runtime and A08 signing/release remain pending. No private recordings are copied.

Extraction verification is in progress. Next: resolve pinned lockfile, host native
controls, local Kotlin/lint, standalone core CLI/no-CLI checks; then one full
combined CI build/API 30–36 run in this repository. Record receipts and failures
before acceptance. No release publication or UI redesign is included.

## Checks before first extraction CI

Pinned Git dependency resolved; only the audio-forensic source entry changed in
Cargo.lock (83 other package entries identical). Python syntax, local Markdown
links and 28 unchanged Kotlin/Gradle-wrapper files passed. `prepare_core.py`
validated the clean fixture checkout. Whitespace passed.
Local Windows native and CLI checks failed while linking dependency build scripts:
MinGW collect2/ld exit 204, including the documented GCC-driver retry. No further
local linker retries; Linux CI runs both core configurations and adapter controls.
Local Kotlin/lint is running with existing caches and the previous accepted native
library for compile-only purposes. That library is not extraction linking evidence.
