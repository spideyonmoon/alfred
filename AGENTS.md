# Alfred project instructions

Read HANDOFF.md, README.md, BUILDING.md and ANDROID_CONTRACT.md before changes.
Alfred owns Android workspace/input/jobs/storage/navigation, feature screens,
native adapter and app tests. Consume the independent Rust engine through the
full-revision Git dependency and committed lockfile. Keep core-dependency.json,
manifest and lock revision consistent. Do not copy/refactor engine source or
fixtures; .core is an ignored validation checkout. Preserve exact PCM/JSON,
bounded admission, cancellation, structured failures and original result bytes.
No calibrated scoring, private audio uploads or unrequested new feature scope.
Preserve owner work. UI redesign is being drafted by the owner; coordinate before
implementing visual changes. Current scaffold is not visual acceptance.
Use targeted local checks and GitHub Actions for heavy Android validation; conserve
CI usage. Record exactly what passed/failed/not run and keep HANDOFF.md current.
No physical acceptance from emulator/link checks. A07 and release/signing remain.
Do not publish a release or change license/signing identity without owner approval.
