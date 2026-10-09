# Alfred roadmap

Updated 2026-10-09. Offline Android11–16 workspace; FLAC/WAV/ALAC-M4A.
Engine 0.32.0 pinned in core-dependency.json; independent core ownership preserved.

- A01–A06b: original functional integration accepted; history in task-results.
- Repository extraction: DONE. Both ABI builds and all seven functional platform
  suites accepted against source 84355e7. Post-test API 34 diagnostic-only CI failure
  is recorded/fixed without another heavy run; see HANDOFF.md for exact boundaries.
- UI v0.1: owner supplied owner-input/UI_v0_1.pdf and authorized implementation.
  Home/Settings, selectable/scrollable music table, resizable actions panel,
  Forensic/Metadata tabs and existing feature routing are implemented locally.
  Read-only complete metadata is independent of DSP; Compare retains 2-32 variants
  per owner clarification. Runtime/visual acceptance is pending; see
  task-results/UI_V0_1.md. Tag editing/conversion, image+cue/log and batch-directory
  PNG export remain separate requirements decisions. Preserve engine/host contracts.
- A07: physical ARM64, device/RSS/thermal/700MiB/16KiB resource acceptance remains.
  Identify tested APK/device explicitly; generated emulator results are not phones.
- A08: after A07/U03, candidate/notices/identity/owner-held signing/release handoff.
  Extraction does not close release gates. Publication requires owner authorization.

DSD, calibration/MQA research and future tools remain separate/deferred. Subsequent
Android decisions belong here; core research/delivery stays in audio-forensic-rust.
Extraction PR1 is merged at b0eca40; baseline CI37898652707 passed both builds and
all seven platform jobs. New UI source is on codex/ui-v0-1 and needs its own
runtime/visual validation. No release is published.
