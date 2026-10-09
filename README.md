# Alfred

Offline Android audio workspace with independent Forensics, Spectrogram and Compare.
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

The current UI is a functional development scaffold. The owner is drafting its
replacement on a scratchpad; repository extraction does not approve that UI.
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
