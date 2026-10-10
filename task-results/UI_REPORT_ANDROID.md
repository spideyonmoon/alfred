# Native forensic report implementation — 2026-10-10

Owner approval: “this is good. implement it”, followed by “Resume”. This supersedes
the prior HTML-only scope. Revision 4 remains the approved design reference;
approval of that design is not device or physical acceptance of this implementation.

## Delivered

- Native Compose report, shared by inline results and saved history. Rich overview,
  spectral investigation, dynamics laboratory, codec fingerprints, bit-depth
  investigation and evidence explorer consume actual retained product fields.
- Named measurements, per-channel cards, frequency scales, all 34 translated rule
  criteria and stored effects, probe scopes and score trail replace ordinary raw
  JSON/predicates. No DSP or rule re-evaluation, calibrated scoring or probability.
- Explicit technical inspection retains exact values; export/share use existing
  original-byte routes. Independent version gates abstain on unknown components;
  failures and unavailable evidence never receive a successful verdict.
- Adaptive section order derives from recorded findings; narrow/large-font layouts
  stack metric/channel cards. Disclosure state survives parent collapse.
- Workspace integration: one toolbar, quick controls, column visibility, persistent
  widths, three-state sorting, frozen check column, separate row focus/selection,
  persistent overlay sheet and direct forensic tool navigation. Scan/progress and
  saved results use the same sheet; obsolete partial-analysis UI removed.
- Inline loading is pinned and bounded, on IO, with identity-based track selection
  and a loaded-path guard. Stored JSON is never rewritten by presentation.

The quick row uses existing Saved reports and Settings actions. The HTML's future
conversion controls remain layout specimens, not unfinished Android controls.
Other tool shells remain non-executing. Clear removes visible workspace membership
and checked state without deleting source files or cancelling jobs; existing input
ownership/grant cleanup is retained. F1/F2 acquisition/recursion work is not included.

## Validation

Passed: offline `:app:assembleDebug :app:lintDebug`, including Kotlin compilation.
Lint reports **0 errors, 16 warnings, 4 hints** (dependency/platform/storage and
KTX suggestions remain; this is not a clean-warning claim).

Passed: `scripts/check-report-projection.py` against the actual saved ABBA product:
identity and measurements, all five sections, 34 rules, 12 probe scales, score
trail, scopes, unchanged input, null/zero, unknown versions, adaptive priorities,
veto/unavailable and failed/unknown products. Variant checks are presentation-model
tests, not new successful audio analyses. Original JSON SHA-256 remains
`069c2ca35351ad2889f1326339006f8a055ed19796309dd64196be7419600dc3`.

Passed: Python syntax for updated feature/SAF smoke scripts and the host checker.
Feature smoke now includes all five sections and explicit original-field access.
SAF smoke follows the approved toolbar/selection/sheet UI; underlying grant checks
remain in InputSmokeActivity. Neither device script was executed in this pass.

One initial app compile failed on a missing brace; fixed and subsequent builds
passed. An initial sandbox cache-lock access failure was resolved by approved
access to the existing provisioned toolchain. No toolchain upgrade or Rust rebuild.

Not run: Android device/emulator, instrumentation, all 16 rendered combinations,
pointer/gesture/accessibility review, rotation/process-death runtime checks, or CI.
Browser preview restrictions were respected without a workaround. Build/model
success does not validate layout, scrolling or physical resources. A07/A08 remain open.

Local receipts: `build/report-android/` contains baseline preservation hashes,
compile/build/lint logs, host check logs and packaging results. These are ignored
local outputs. No commit, push, release, license or signing identity change.

Preservation check: 133 baseline files inventoried; only 13 intended existing
integration/documentation files changed, with 120 byte-identical. New Kotlin and
host-check files are additions. HTML, owner-input references, engine revision
files, native source, storage/input implementation and unrelated dirty work remain
unchanged. Local documentation links and `git diff --check` pass.

## Debug APK

[Local debug APK](../app/build/outputs/apk/debug/app-debug.apk), arm64-v8a.

Final offline build/lint passed in 1m 53s. APK verification passed native ELF
dependencies, 16 KiB ZIP/load alignment, debug signature and packaged smoke-fixture
byte equality. This checks packaging only, not Android runtime loading.

APK SHA-256: `072174fec31b4fad7a81893fb17a423856dac1599c65fbac6bdac85d124adfa6`.

Packaged native SHA-256: `af67d3757147f7e5d5cc7cef4721a769c9783aab70d787396eea1339603c29bc`, equal to the
pre-existing binary; no native rebuild. No named ABBA/sample-product asset is packaged.
