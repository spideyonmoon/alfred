# Owner scratchpad implementation - 2026-10-09

Historical implementation record. The owner rejected this presentation; see
[the subsequent redesign](UI_REDESIGN.md) for the current working tree and APK.

Source: local `owner-input/UI_v0_1.pdf`, supplied by the owner. The PDF remains
owner input; its diagram is a flow/layout reference, not final visual acceptance.
The owner clarified that Compare retains 2-32 variants, then narrowed the current
app to Audio Forensics with shells for the remaining features. Current scope and
checks follow; the earlier full-route implementation/checkpoints below are history.

## Current Forensics-only scope

Audio Forensics retains input acquisition, selected-subset admission, explicit
full/partial scope, qualified results, exact raw fields, history, export and share.
Home/Settings, the selectable/scrollable music table and adjustable Forensic/
Metadata panel remain. Spectrogram and Compare open cards marked Planned feature;
Metadata studio is a shell in its tab. These routes expose no execution, inspector,
export or history controls. PNG resolution controls are disabled. Future Compare
keeps the owner's 2-32 same-track contract. No tag editing, conversion, cue/log,
image processing or batch directory export is implemented.

Existing feature modules and generated backend controls remain intact internally.
The preparatory bounded original-probe cache/read/export implementation is retained
but has no production Metadata studio UI entry. Complete metadata remains visible
inside Audio Forensics results. Core/adapter/pins/lock are unchanged.

The SAF/feature UI harness now expects shells and chosen-subset Audio Forensics.
Native/input/Forensics/jobs controls and 22 internal viewer/Compare backend controls
remain in emulator-smoke.py. Old PNG/history UI expectations are superseded by the
owner's explicit scope change; no old pass flags are emitted for the shells.

Final local offline Kotlin/lint/assemble passed in 2m20s: 0 errors, 13 warnings,
5 hints. ARM64 fixture bytes, every native ELF/ZIP 16KiB alignment, zipalign and
development signature passed. Current APK app/build/outputs/apk/debug/app-debug.apk
SHA256: 0463bccf8c8752d6bb8464012a3b25ab2a1f3656c2e98a5d7c3ba08ed479bca0.
Native is the byte-verified accepted extraction library described below. Host
Python syntax, source UTF-8 and whitespace checks passed. No local device/runtime,
new CI compilation/emulator job, owner visual acceptance, A07 or release acceptance.
Current Kotlin differs from source-bound CI APKs, so those APKs cannot validate
the shells. A fresh build checkpoint is required when the owner elects to spend CI.

## Earlier implementation before the shell clarification

- Home file/multiple/folder selection, separate Settings, saved feature histories.
- Music table with select-all and individual row selection, independent horizontal
  and vertical scrolling, and a draggable/accessible adjustable table/panel split.
- Forensic/Metadata tabs and an actions panel for the three existing operations.
  Selected subsets preserve URI/item identity, order, grant ownership and hashes.
  Spectrogram accepts one chosen row from a larger selection; Compare retains its
  explicit same-track assertion and 2-32 bound. Incomplete folders still require
  confirmation of a smaller set before probing or operation admission.
- Resolution uses the three existing Rust PNG presets. Appearance and default PNG
  resolution are saved locally. Existing result/history/export/share flows remain.
- Forensics accepts an explicitly chosen positive whole-second prefix within the
  existing frame budget for single or multiple selected inputs.
- Metadata studio reads the complete original probe JSON independently of DSP.
  Every retained field/null/integer remains inspectable through ExactFields; SAF
  export streams the original bytes. Container precision/rate/channel declarations
  appear in the table, without a fabricated quality score or PCM verification.
- Metadata output is selection-scoped in `input-metadata`, separate from transient
  operation `input-probes`. Native release precedes adoption. Paths, lengths and
  SHA-256 are checked; readers/exporters pin the directory. Replacement/close drops
  cache owners; process initialization removes prior-process metadata orphans.
  Maximum 32 documents / 512 MiB including native manifests, 64 MiB per document;
  disk margin and existing probe reservation still apply. Parsing is serialized,
  checks available heap and displays one document. This is an unmeasured engineering
  bound, not A07 resource acceptance or a new analysis history quota.

## Remaining scratchpad decisions

Tag writes, conversion and audio-image plus cue processing need explicit format,
mutation/export, error and core ownership contracts. Log/cue discovery/parsing and
a contextual Log tab are not implemented. A batch directory PNG export needs SAF
tree destination, naming/collision/partial-failure requirements; current per-result
SAF export remains available. Those absent controls are not represented as working
actions. The current PNG viewer and Compare semantics remain the accepted scope.

## Earlier local validation before the shell clarification

- Initial offline Kotlin/lint passed in 5m49s (0 errors, 14 warnings, 5 hints).
  Additional changes followed; this is an intermediate result.
- Offline Kotlin/lint/assemble passed in 5m14s; final product/panel-scroll and
  verified native packaging check passed in 1m29s. Final debug metadata-export
  assertion Kotlin/assemble passed in 59s. Lint: 0 errors, 14 warnings, 5 hints.
- Final ARM64 review APK: app/build/outputs/apk/debug/app-debug.apk, SHA-256
  0cb4c15faadb4243e83d7c56a7da20cf8a026a3a0b0553aa4403c00c450451f4.
  Generated fixture byte checks, all native ELF/ZIP 16 KiB alignment, zipalign
  and development signature verification passed. Runtime is not tested; native
  is reused from the accepted extraction APK, not newly linked locally.
- A preexisting generated native library differed from the accepted extraction
  APK. Before the final packaging check, replaced that ignored build input with
  the exact ARM64 library from the hash-verified accepted extraction APK (source
  84355e7, native/core inputs unchanged through current main). Native SHA-256:
  af67d3757147f7e5d5cc7cef4721a769c9783aab70d787396eea1339603c29bc.
- Python harness syntax, Kotlin UTF-8, unchanged adapter/core sources and full
  revision manifest/lock binding passed. Initial native byte-binding check failed
  against the preexisting build input; replacement verification passed. No native
  rebuild claim is made for local packaging.
- Debug input harness now covers selected subsets, independent metadata access,
  stale selection rejection and cleanup. Real SAF harness covers Metadata tab,
  select-all/subset, resize control and single-row Spectrogram admission. These
  new runtime assertions are not yet accepted. One API31 checkpoint was run:
  https://github.com/spideyonmoon/alfred/actions/runs/37902157751.
- Local ADB initially could not start inside the sandbox; normal server startup
  succeeded outside it and listed no connected devices. No local runtime/visual
  acceptance, physical phone checks or A07 closure.
- Rust adapter/core/pins/lock and engine fixtures are unchanged. No native rebuild,
  scoring, private-audio upload, release, license or signing-identity change.

At that point the next validation was to build the new application sources once, exercise an
affected API through the full generated harness, and reuse that source-bound APK
for additional APIs only when justified. An old whole APK cannot validate this UI.

## Push / CI authorization boundary

Source commit ee704d2 is on codex/ui-v0-1 from the owner's merged main.
Automatic approval review rejected pushing that branch to spideyonmoon/alfred:
the source/docs payload's egress was not specifically authorized. Push and the
dependent one-API31 manual workflow dispatch did not execute in that rejected
attempt. The owner then explicitly approved pushing ee704d2 / 84e1171 and one
API31 CI checkpoint. Push and manual dispatch succeeded: CI37902157751 failed as
recorded below.
The PDF, APK, caches, native build inputs and private audio are excluded from Git.

## First UI checkpoint and harness-only correction

CI37902157751 at app/source revision 84e1171 built both ABIs and passed core
CLI/no-CLI, native adapter controls, strict pins/lint/assets/alignment/signatures.
Downloaded receipts/APK SHA-256/core revision binding passed locally:

- ARM64 e7149bb118eb36609d27e6cd9a43780a2dade39c309f4ee847dea5ae656ef9ec.
- x86_64 3d9a1c5ea87b414ad00e6f00cb704cf8db9acff44ccdb1997fe74f4cfe8675ea.

API31 job failed after rendering Home, before native/input/features/jobs runtime
suites: emulator-smoke.py searched for `Native host v1 loaded` by scrolling Home.
The redesigned diagnostic is in Settings. Saved workspace.xml shows the correct
Home/pickers/Settings button; logcat has no app crash or native_load_failed.
This is a stale harness assumption, not accepted runtime validation.

Harness-only correction navigates to the observed Settings button, scrolls there
for the actual native label, and retains the actual Home screenshot. Python syntax
and saved-hierarchy observation/button coordinates passed locally. No production
Kotlin/native changes or additional build. New runtime assertions still await a
build-free API31 retry of the same verified artifacts; request owner approval for
the additional run after the originally approved one checkpoint failed.

## First build-free retry

Owner approved de9a881 and CI37903457491. Compilation skipped; full source/core/APK
reuse guard passed. Actual Settings bootstrap, native controls and all six shared
input groups passed, including independent metadata/exact-byte export, subset
capability, stale metadata rejection and current-cache/orphan cleanup. Real SAF
single/multiple/folder acquisition passed. Home and music screenshots are retained
under build/ui-ci/37903457491/api31 (generated inputs only).

Metadata UI failed observing All report fields. The small 320x640 panel was
scrolled to history in the final hierarchy. A clipped inspect-button tap or
overshooting the label is the current inference; backend metadata is successful,
but this does not establish UI success. Forensics/viewer/Compare/jobs runtime
suites were not reached, and the run remains red.

Further harness-only correction selected the enabled clickable ancestor only
when it has sufficient visible height and bottom clearance, uses shorter swipes
within the observed lower scroll surface, and saves before/after-tap hierarchy.
Syntax, actual retained Metadata-tab visibility and host clipped/full-button
selection checks passed. Production sources/APKs remain unchanged. Additional
build-free API31 attempts required approval beyond the single retry already used.

## Second build-free retry and scope change

Owner approved pushing 9fa28c1 and up to two reuse attempts, stopping on success or
if APK source changes were needed. Only one was used: CI37904658295 reused the
verified 84e1171 APKs from CI37902157751, with no compilation. Source/core/APK guard,
actual Settings/native bootstrap, 16 native controls, all six input groups and
real SAF single/multiple/folder checks passed. The corrected Metadata UI tap and
All report fields observation passed, as did selection/subset/resize assertions.
18 Forensics backend checks, five history/raw-field UI checks and all 22 internal
viewer/Compare backend controls passed. It then failed in features-ui-smoke.py
finding `3: png`; later PNG/Compare UI and all job lifecycle suites were not run.
The aggregate run is red. Saved hierarchy shows a spectrogram result/raw fields;
the PNG observation failure is not claimed solved or waived.

Receipts/screenshots/XML/logcat are under build/ui-ci/37904658295/api31. These are
generated inputs only and evidence for the previous full-route revision. The
owner's subsequent Forensics-only instruction produced the shell changes above.
That requires new APK source, so the remaining reuse attempt was not dispatched.
No additional CI or other APIs were run. Earlier failures remain part of the ledger.
