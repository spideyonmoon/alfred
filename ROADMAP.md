# Alfred roadmap

Updated 2026-10-09. Offline Android11–16 workspace; FLAC/WAV/ALAC-M4A.
Engine 0.32.0 pinned in core-dependency.json; independent core ownership preserved.

- A01–A06b: original functional integration accepted; history in task-results.
- Repository extraction: DONE. Both ABI builds and all seven functional platform
  suites accepted against source 84355e7. Post-test API 34 diagnostic-only CI failure
  is recorded/fixed without another heavy run; see HANDOFF.md for exact boundaries.
- UI: owner is drafting the replacement. Current scaffold's visuals are rejected.
  Implement agreed design when supplied; preserve engine/host contracts and do not
  invent broader competitive viewer/comparison requirements.
- A07: physical ARM64, device/RSS/thermal/700MiB/16KiB resource acceptance remains.
  Identify tested APK/device explicitly; generated emulator results are not phones.
- A08: after A07/U03, candidate/notices/identity/owner-held signing/release handoff.
  Extraction does not close release gates. Publication requires owner authorization.

DSD, calibration/MQA research and future tools remain separate/deferred. Subsequent
Android decisions belong here; core research/delivery stays in audio-forensic-rust.
No active CI jobs. PR1 is open; main branch is not yet integrated.
