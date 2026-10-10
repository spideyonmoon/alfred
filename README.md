# Alfred

**Status: Android development shelved as of 2026-10-10.** Current work is retained
for a possible return. A Windows app may be considered later; the owner's current
focus is planning the independent core engine. Android acceptance and release work
remain deferred, not complete. See [the shelving checkpoint](HANDOFF.md) before
acting on historical implementation plans or build instructions below.

Offline Android audio workspace. Audio Forensics is the available feature;
Spectrogram, Compare and Metadata studio currently have planned-feature shells.
The Android app, shared infrastructure and JNI adapter live here. The independent
[Audio Forensic Rust engine](https://github.com/spideyonmoon/audio-forensic-rust)
remains a one-way dependency, pinned in `core-dependency.json`, the adapter manifest
and Cargo.lock. No engine source or generated DSP fixtures are vendored.

Android 11–16; FLAC, WAV and ALAC/M4A. Broader spectrogram interaction, future tools,
DSD and calibrated detector conclusions are outside the current accepted scope.

Start with [HANDOFF.md](HANDOFF.md), [BUILDING.md](BUILDING.md) and
[the native contract](ANDROID_CONTRACT.md). Run commands from this repository root.
`python scripts/prepare_core.py` provisions an ignored, revision-bound fixture checkout.
The adapter resolves the same revision through Cargo's Git dependency/cache.

The owner's UI sketch now guides a minimal import home and a spreadsheet music
workspace above a resizable Forensic/Metadata panel. Audio Forensics setup stays
inside that workspace; history exposes summaries and expandable original fields.
The prior presentation was rejected and has been reworked, including themes,
settings and launcher branding. See [the redesign record](task-results/UI_REDESIGN.md)
for changes and precise validation limits. Other tools remain planned shells.
The approved forensic report is implemented in native Compose; see
[implementation and validation](task-results/UI_REPORT_ANDROID.md).
Runtime and owner visual acceptance of the Android presentation are still pending.
A07 physical-device/resources and A08 release/signing remain unaccepted.

## Provenance and licensing

Extracted from `apps/alfred/` at core repository commit
79470a3; accepted compiled behavior was tested at 5c5ce00d44f6759dd6a7319804b5f7a21079d1b4
in [Android 11–16 CI](https://github.com/spideyonmoon/audio-forensic-rust/actions/runs/37820435450).
Original commit history remains in that repository; retained task records link to
the revision-specific original documents. New extraction checks are separate evidence.

The owner-created repository's [GPLv3 license](LICENSE) is preserved. Extracted
MIT-licensed source retains its [MIT notice](LICENSE-MIT); the native adapter's
existing MIT package declaration and independent engine license are preserved.
No private recordings, caches, signing keys, APKs or local evidence are included.
