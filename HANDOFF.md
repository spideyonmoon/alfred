# Alfred handoff

Updated 2026-10-09. Owner requested extraction to C:\Users\Bishal\code\alfred,
https://github.com/spideyonmoon/alfred. Branch codex/extract-android; PR1 open.
Owner rejected the scaffold's visual quality and is drafting UI on a scratchpad.
Preserve visuals until the design arrives. Extraction is implemented; final
remaining-platform acceptance is running, not yet complete.

## Current source and running job

Compiled app source: 84355e7ff83756e8495c1f372d483486cded6924. Later commits only
change workflow/harness/docs; strict source/hash/core guard verified APK reuse.
Engine 0.32.0: 5c5ce00d44f6759dd6a7319804b5f7a21079d1b4 via Git dependency,
native Cargo.lock and core-dependency.json. .core is a clean ignored validation
checkout with self-contained Git objects; generated fixtures remain in the engine.
App/shared/three features/JNI adapter/scripts/Android workflows are here. Core-side
removal/pointers pushed at 0a41632; core PR1 open. Neither PR is merged. Original
history, standalone engine/schemas/fixtures and unrelated owner cleanup are preserved.
GPLv3 destination license and extracted MIT attribution retained. No private audio
or private signing keys in source. No release publication or UI redesign included.

Build checkpoint 37892173286: both ABI links/APKs, strict pins/lint/fixtures/
alignment/signatures, independent core CLI/no-CLI and adapter controls passed;
API30 and API31 full suites passed. API32 passed all22 backend controls but its
single-snapshot PNG visibility check ran with the image below the viewport.
Harness-only bounded scroll/wait correction is committed; product UI is unchanged.

Running reuse CI37893506220 verifies this exact APK then runs full harness on
API32/33/34/35/36. Source guard passed; build job intentionally skipped; five
emulator jobs running. Only already-built APKs are used. The indexed full workflow
supports reuse_build_run and apis inputs. The separate manual-only runtime workflow
is not indexed on the default branch yet (main still has the original license).
Use alfred-android.yml reuse mode until default-branch integration; no main/settings
change is required. No additional build or API30/31 repeat needed.

## Checks and retained failures

Static lock/source/syntax/local-link/whitespace checks passed; 83 non-engine lock
entries unchanged. Kotlin/lint initial5m57s, servicefix2m48s; final idle-admission
compile52s passed. Windows Rust host dependency linking failed (ld204 including
documented GCC retry); Linux core/native controls passed instead.
First CI37888964074 exposed API31 foreground-start crash. Focused service fix uses
latest start ID, per-request promotion and serialized idle detach. CI37890896503
exposed transient busy admission from an overly strict guard; it was cancelled.
Separate idleStopping now lets bounded new requests queue until promotion, retaining
the original interruption/release-unknown guard. Existing feature/queue/recovery
controls cover these paths; no engine, adapter, payload, fixtures or limits changed.
Details and all failure boundaries: task-results/EXTRACTION.md.

Next: await reuse CI37893506220, inspect actual failures if any; download API30/31
receipts from checkpoint37892173286 and API32–36 from reuse37893506220. Verify all
22 controls/native/input/Forensics receipts and final APK/source bindings. Then
close both handoffs/roadmaps and update PR descriptions without triggering rebuilds.
Local evidence: ignored build/accepted-extraction-final, earlier accepted-extraction
and accepted-extraction-fixed plus failure-api folders. These are not source backup.
A07 physical ARM64/RSS/thermal/700MiB/16KiB and A08 identity/signing/release remain.
Next owner UI design integration, then physical acceptance; no visual approval yet.
