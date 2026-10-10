# Sketch-led presentation redesign — 2026-10-09

The owner rejected the built app's presentation and explicitly requested a complete
UI change using the sketch in this directory. `owner-input/UI_v0_1.pdf` was read
and visually inspected; its one-page diagram supplies the home/selection/music
workspace layout. This request authorizes visual implementation. It does not
expand the previously agreed Forensics-only feature scope.

## Implemented

- Minimal home with a centered waveform mark, one Select file / folder entry,
  a single/multiple/folder chooser, resume-workspace link and Forensics history.
- Warm neutral light and dark palettes, green accent, consistent typography,
  spacing/surfaces, matching system-bar contrast and an adaptive launcher icon.
- Music remains visible above the tools. Compact spreadsheet header, alternating
  rows, selected-row tint, horizontal and vertical scrolling, full names in row
  selection accessibility labels and exact declared resolution/size values.
- Small draggable divider replaces the exposed slider. It exposes range semantics
  and accessibility actions. Forensic/Metadata tabs occupy the lower panel.
  Table ratio/tab state survives navigation to history/settings and back.
- Audio Forensics setup opens inside the lower panel. Full analysis remains one
  explicit action; Partial analysis expands the existing bounded seconds input.
  Changing selection updates the setup. Existing submission/admission/retry,
  cancellation and per-item outcomes remain owned by the existing work layer.
- A compact running-job strip retains progress, cancellation and cancellation-
  requested state without listing internal attempt identifiers in the main flow.
- History has dated report cards and a focused selected-report view. Payload
  choices scroll horizontally; export/share retain original bytes. Interpretation
  remains qualified, with expandable measurement explanations, report details,
  unsuccessful attempts and exact raw fields. Nulls/large integers are unchanged.
- Settings groups appearance, notifications and planned resolution, with storage
  limits expanded on demand. Spectrogram/Compare/Metadata stay planned shells.

The sketch's conversion, cue/log/image and directory PNG ideas remain deferred.
No invented quality score, detector recalibration, engine/native dependency change,
private audio transfer, release, license or signing identity change. Owner input
PDF remains untracked and untouched.

## Validation

Local Kotlin compile passed (49s); intermediate offline assemble/lint checks
passed (2m7s and 2m). The final frozen-source check passed as recorded below. Accepted ARM64 native
library reuse was compared byte-for-byte against the accepted extraction APK.

UI harness updated for the import chooser, report accessibility label, expandable
raw fields, inline analysis, partial setup and persistent track table. Shell
assertions live in the workspace SAF suite; history is now report-only. Python
syntax passed. These edits are prepared assertions, not Android runtime evidence.
API30's existing manual-picker substitution does not validate this new UI.

ADB reports no connected device. The local SDK has no emulator installed. No new
GitHub Actions run, device/emulator interaction, screenshots of the running new
app, font-scale/rotation/touch visual validation, owner visual acceptance, A07 or
release/signing acceptance. Older CI APKs cannot validate these new sources.
No release/push/CI dispatch was performed.

Local evidence: `build/ui-redesign/` (ignored). The PDF extraction initially hit a
Windows console encoding error; UTF-8 extraction and rendered-page inspection then
succeeded. That was an inspection-tool issue, not an application build failure.

## Final local artifact

- Offline `:app:assembleDebug :app:lintDebug`: passed, 2m2s; 0 errors,
  13 warnings and 5 hints.
- `verify_apk.py --smoke-assets`: generated fixtures, native ELF and ZIP 16KiB
  alignment, zipalign and development signature passed. Final packaged native
  library equals the accepted extraction APK's library byte-for-byte.
- Python syntax, Android resource/manifest XML, UTF-8, `git diff --check` and
  full-revision core/manifest/lock consistency passed. Native source/lock untouched.
- No application build/check failed in this turn. Android runtime suites, visual
  interaction, Rust rebuild/tests, CI and physical acceptance were not run.
- APK: `app/build/outputs/apk/debug/app-debug.apk`
- SHA256: `7332d151634bf1c0fafa0ab0f35207ec757c0aa314d8cb904eb529e9198101fa`
- Source hashes and validation receipt: `build/ui-redesign/receipt.json`.

Source changes are uncommitted in the existing working tree. No owner input was
overwritten. This development APK is reviewable, not visually accepted or released.
