# Standalone repository extraction — 2026-10-09

Owner supplied local path and remote, asked to proceed while drafting UI.
App/shared/features/adapter/scripts/Android workflows move here; reusable engine,
schemas, generated fixtures and computation tests stay in audio-forensic-rust.
Accepted engine revision is 5c5ce00d44f6759dd6a7319804b5f7a21079d1b4, engine 0.32.0.
App manifest/core-dependency.json/Cargo.lock agree. No source is vendored; ignored
.core checkout supplies frozen fixture bytes. Tool caches and development key stay
ignored. Initial GPLv3 repository license preserved; MIT attribution retained.

Changed build/provision roots, debug signing location, fixture paths in Gradle,
native tests and APK verifier; emulator APK path; source-reuse paths and engine-pin
guard. Linux/Windows receipts distinguish app revision and core revision. Runtime
reuse workflow is manual, preventing a duplicate first-push empty-artifact run.
Full combined workflow owns both ABI builds, core checks, native controls, lint,
strict verification, alignment/signatures and API30–36 acceptance. UI unchanged.

## Checks

- Static: lock binding passed; other 83 lock package entries unchanged. All 28
  Kotlin/wrapper files byte-identical. Python syntax/local Markdown targets passed.
- prepare_core validated checkout revision/clean tracked state/manifest pin.
- Local Kotlin and lint passed in 5m57s, 0 errors,
  12 warnings, 4 hints. Used
  the prior accepted native library for compile-only checks, not extraction linking.
- Windows host native and core checks failed in dependency build-script linking:
  collect2/ld exit204. Documented GCC retry failed too; no further host retries.
- Linux CI core CLI/all-targets and no-CLI library checks, plus native controls,
  passed. Both ABI builds/emulators pending at this update.
- Source 0388042; [combined CI](https://github.com/spideyonmoon/alfred/actions/runs/37888964074).

Original app history remains traceable in the core repository; extracted source
provenance is recorded in README. Core-side move at 0a41632 removes tracked app and
Android workflows, retains local ignored caches and unrelated owner cleanup edits.
A07 physical memory/thermal/large-file/16-KiB runtime and A08 release are not closed.
No private audio, signing material, release publication or UI redesign uploaded.

## First extraction run failure and focused lifecycle correction

CI 37888964074 completed: both ABI builds, core CLI/no-CLI and native controls
passed; API30/32/33/34/35/36 passed. API31 failed: foreground-service startup
exception killed ViewerCompareSmokeActivity before its receipt; harness reported
a 240-second timeout. Native/input/Forensics checks had already passed. Logcat
records `ForegroundServiceDidNotStartInTimeException` and service teardown while
Android was still waiting for foreground promotion. This is a real crash, not
accepted as an unexplained emulator flake. The extracted Kotlin was unchanged
at that run; failure exposed a pre-existing service lifecycle race.

The old idle tick unconditionally demoted/stopped the service while submissions
could arrive concurrently; its promotion guard also skipped acknowledging later
foreground starts on the same instance. Focused app-only correction: atomically
claim idle shutdown under the admission lock, clear foreground-ready state and
keep busy until actual detach; use latest start ID with stopSelfResult to preserve
newer undelivered starts; restore readiness if shutdown was superseded; promote
every normal foreground request while allocating one wake lock/tick loop.
No UI, engine, adapter, fixture, admission limit or payload changes. Existing
generated viewer/cancellation/queue/restart/lifecycle controls supply the regression
exercise; all platform gates must pass against the corrected APK.

[Android Service reference](https://developer.android.com/reference/android/app/Service#stopSelfResult(int))
confirms the latest-start-ID shutdown contract (checked 2026-10-09). The exact
interleaving is inferred from the crash and the unlocked shutdown/admission paths;
it was not deterministically reproduced on a physical device.

Intermediate Kotlin/lint passed in 2m29s. Final corrected Kotlin/lint passed in 2m48s. Corrected source d550360 is pushed;
[one manual corrective combined CI](https://github.com/spideyonmoon/alfred/actions/runs/37890896503)
is running. Skip-CI source commit avoided duplicate push/PR runs. Initial artifacts
are retained at ignored build/accepted-extraction; API31 failure diagnostics at
build/extraction-failure-api31. These are local evidence, not source backup.

## Corrective admission regression — 2026-10-09

Corrective source d550360/run37890896503 built both ABIs. API30 rejected the
future-descriptor control with transient busy instead of unsupported_version;
API32 returned busy admitting the queue blocker. Both got through the earlier
native/input/Forensics and Spectrogram/live/saved controls without the original
foreground-start crash. Initial guard incorrectly reused the interruption flag
for idle teardown. Remaining superseded jobs were cancelled to conserve CI.

Refinement uses separate idleStopping state. Idle teardown clears readiness but
does not reject bounded new submissions; they queue and wait for actual foreground
promotion. Cancellation/interruption/release-unknown admission still uses its
original stopping guard. Busy includes the detach window for observers. Latest
start-ID shutdown and per-start foreground acknowledgement remain. No UI/core change.

CI now supports an API list on full manual builds; default remains all seven.
Verified APK reuse now runs the entire native/input/features/UI/jobs harness rather
than the legacy input-only mode. Next run targets API30/31/32, then the same source-
bound APK runs API33–36 through the reuse workflow if those first gates pass.
No accepted gate is skipped; this avoids rebuilding between affected-version
diagnosis and remaining-platform validation.

Final idle-admission refinement Kotlin compile passed in 52s. The cancelled
superseded run retained API30/API32 failures and no claim for remaining cancelled
platforms. Reuse guard now also rejects uncommitted compiled inputs; local receipt
verification was originally against committed HEAD, not the in-progress working
refinement. Ignored validation checkout Git objects are self-contained (no old
workspace alternates). Source/engine pins remain unchanged.

## Affected-platform checkpoint and viewport correction

Source84355e7/run37892173286: both ABI builds and API30/API31 full suites passed.
API32 passed all 22 backend controls and Forensics UI checks, then the one-shot
PNG visibility assertion failed. Saved XML showed the decoded-preview status
paragraph at the viewport bottom; the image follows it in SpectrogramPreview.
No foreground-start crash was present. This is a harness viewport assumption:
it inspected one hierarchy snapshot rather than scrolling to the actual image.
Changed only features-ui-smoke.py to bounded scroll/wait for that exact content
description; it still fails if the image never becomes visible, and still captures
the screenshot. Product UI and compiled sources are unchanged. Python syntax passed.

Next: full verified-source APK reuse on API32/33/34/35/36. API30/31 already passed
against this APK. Do not repeat builds or those accepted platform jobs. Both APK
hashes/core pins and current committed compiled-source equality passed locally;
uncommitted compiled-source rejection also passed. No oracle regenerated.

Standalone manual-only runtime workflow dispatch returned HTTP404 because this
new repository's default branch still contains only its initial license; GitHub
has not indexed that workflow yet. The already-indexed full workflow now accepts
reuse_build_run: it skips compilation, verifies exact source/core/APK bindings,
then runs the selected full emulator harness. Use that gateway until default-branch
integration. No repository settings or main branch changed; no rebuild needed.
Next dispatch alfred-android.yml with reuse_build_run=37892173286 and
apis=[32,33,34,35,36].
