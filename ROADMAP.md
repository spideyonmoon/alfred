# Alfred roadmap

Updated 2026-10-09. Independent engine: Audio Forensic Rust 0.32.0, pinned revision
in core-dependency.json. Android 11–16, FLAC/WAV/ALAC-M4A; offline workspace.

- A01–A06b: functional integration accepted in the original repository. Retained
  task records preserve those exact checks and evidence boundaries.
- Repository extraction: owner requested now; source at 0388042, combined CI
  37888964074 passed builds/six APIs but API31 crashed; focused lifecycle fix d550360 passed local Kotlin/lint; corrective CI 37890896503 running. Core stays independently consumable. See HANDOFF.md.
- UI: owner is drafting a scratchpad. Current functional scaffold is visually
  rejected. Implement the agreed design when provided; do not invent broader
  Spectrogram/Compare requirements or alter numerical/ownership contracts.
- A07: physical-device/resource gate remains pending. Redmi 13 4G/Android 16 and
  Hot 11S/Android 11 evidence must identify the actual tested APK. Emulator and
  linked APK results do not establish physical memory/thermal/16-KiB acceptance.
- A08: after A07/U03, reproducible candidate, notices, app/engine versions, final
  identity, owner-held signing and release handoff. Publication needs owner approval.

DSD, calibration/MQA research and future tools remain deferred/separate. Original
[task cards](https://github.com/spideyonmoon/audio-forensic-rust/blob/79470a3/ROADMAP_TASKS.md)
and [A01](task-results/A01.md) explain the initial contracts. Subsequent Android
decisions belong here; core research/delivery stays in the engine repository.
