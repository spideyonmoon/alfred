# UI functional dependencies: F1, F2 and F3

2026-10-09. Focused assessment and implementation design proposal. No contract,
admission limit, engine pin or source was changed. Read [UI specification](UI_SPEC.md)
and [work chunks](UI_WORK_CHUNKS.md). This is not physical/resource acceptance.

## Current scope after owner U1 corrections

F1/F2 remain active functional dependencies. Import must not assume replacement:
resolve append/merge versus explicit replacement and duplicate-identity policy
before acquisition changes. Clear removes only collection entries; submitted work
retains frozen membership/grants and source audio is never deleted.

F3 detector-selective partial analysis is **shelved by the owner**. The assessment
below is retained for later work, not an active UI gate or implementation task.
The corrected UI removes Duration coverage controls, while retaining honest
stored coverage and existing backend/tool scope behavior. No engine update now.

## F3: shelved detector-selective analysis assessment

The clean `.core` checkout equals pinned revision
`5c5ce00d44f6759dd6a7319804b5f7a21079d1b4` (engine 0.32.0).
This finding is about that revision, not a claim about later upstream releases.

Evidence chain:

- `.core/src/model.rs`, `AnalysisOptions`: track_id, max_seconds, deadline only.
- `.core/src/lib.rs`: public analysis variants select output collectors; the
  detector module is private. Collector choice is not a per-detector mask.
- `.core/src/decode.rs`, `analyze_source_product`: calls shared analysis with
  metadata, byproducts, tool statistics and reference-input collectors together;
  only spectrogram collection has a boolean switch. It documents three verified
  PCM passes on one source/worker/deadline.
- Same shared path initializes/pushes/finishes MqaScanner and loudness meters.
  The product tool collector computes DR; there is no MQA-only or DR-only request.
- `native/adapter/src/transport.rs`: deny_unknown_fields Request; Scope is Full
  or Prefix { seconds }. No detector field. operation.rs constructs AnalysisOptions.
- `ForensicsWork.kt`: persists scope/tracks and submits full product work.
- Native DetectorStatus is Measured/Hit/NotDetected/Inconclusive/Unsupported.
  There is no explicit user-skipped execution status in that enum. A null value
  cannot be relabelled Not run at user request.

| Requested group | Measurement already exists | Independently selectable through current product API | Required shared work / persisted semantics |
| --- | --- | --- | --- |
| MQA signalling | MqaObservation and bounded candidates | No | Native integer words, channels/precision, scanned frames; metadata claim separate; selected/skipped must be explicit |
| Dynamic Range | ToolMeasurements.dr, overall/per-channel/legacy labels | No | Exact PCM and tool collector block/tail/domain; do not equate it with crest factor or loudness range |
| Native loudness | Native loudness collection | No group selector | Survey/gating and measurement passes; method/channel/coverage preserved |
| Reference source/depth candidates | ReferenceAssessment with bound inputs | No group selector | Dependency graph of reference features, PCM verification and rules; partial input must not yield full verdict |
| Spectrogram | Separate public operation and optional product collector | Existing separate tool, not detector-selective Forensics | Preserve its existing operation and scope |
| Metadata | Independent bounded metadata API | Existing metadata read, not selective forensic DSP | Declarations only; no proof of decoded PCM |

**F3 assessment result:** the owner requirement is unsupported end to end at this
pin. Removing the current coverage dropdown does not implement F3; F3 is now shelved.
Do not display a selector, run everything then hide fields, or edit `.core`.

Retained independent-core requirements packet for a future, separately authorized decision:

1. Public stable group IDs and a validated nonempty requested-group set, with
   full-run behavior backward compatible. Coverage remains a separate field.
2. Document each group's required shared computations/passes and cancellation
   points. Prove unrequested detector evaluators are not executed; shared decode
   is allowed only as a declared prerequisite.
3. Persist requested/executed groups, prerequisites and explicit not-requested /
   unavailable / failed statuses with actual per-group coverage. Version the
   envelope/components as needed; support old saved reports without guessing.
4. Do not score missing required reference inputs as zero or issue a full reference
   verdict for a subset. Preserve exact PCM, null ancestry and original data.
5. Generated tests: MQA-only, DR-only, combined, full parity, independent prefix,
   short/silent/unsupported input, cancellation, failed group, saved round-trip and
   instrumented proof that unselected work did not run.
6. After an independently accepted engine revision: update Alfred's full Git pin,
   Cargo manifest/lock together, transport validation, persisted job options,
   capability gating and result renderer. No speculative pin update now.

## F1: capacity is several policies, not one constant

| Enforcing layer | Current behavior | Why simply raising 32 fails |
| --- | --- | --- |
| SafPicker.parseResult | Retains up to 33 unique identities to detect overflow | Truncating before admission loses import membership |
| SafSelection.read | Direct >32 rejects; folder stops at 512 records/32 candidates | Provider enumeration and incomplete outcome are coupled |
| WorkspaceInput.confirmFolder | Accepts only 1–31 selected from incomplete list | Confirmation assumptions must change with traversal semantics |
| WorkspaceMetadata.adopt | 32 original probe outputs / 512 MiB | Original payload ownership and lazy data projection need separate quotas |
| forensicsOperation | Selection shape 1–32 | UI capability check can reject before shared jobs |
| SharedJobs.submit | 1–32 IDs; one active/two queued; job JSON persistence | Large arrays/options can exceed persisted/control bounds; queue segmentation is not a safe shortcut |
| ForensicsWork | Serial per-item acquisition; aggregate reservation capped at 512 MiB | Large collection cannot promise retention of every maximum-sized product |
| ResultStore.finalize/load | At most 32 outcomes / 64 descriptors; document 64 MiB | A larger batch cannot fit existing manifest assumptions |
| ResultStore history / SharedJobs terminal records | 32 completed entries / 512 MiB history; terminal pruning | Creating many jobs can evict earlier results during the same user request |
| Compare / exports / share cache | Distinct arity, receipt and byte bounds | Unrelated limits must not change because the collection grows |

Current native Forensics work receives one acquired item at a time. The core CLI's
32-product batch limit is not the cause of every Android collection restriction;
the library's single-source API does not require a 32-item workspace. This does
not prove that arbitrarily large Android collections are safe.

### Proposed architecture for review

Separate **collection** (discovered identities/properties) from **operation**
(frozen checked membership) and **retained results** (byte-admitted originals).
Recommend disk-backed indexed collection/membership tables rather than serializing
an unbounded array into WorkspaceState or each job. Use Android's existing SQLite
facilities if this option is accepted; a new framework dependency is not required
by the design. Database choice/schema remain part of functional implementation review.

Suggested logical records:

- collection(id, generation, completeness, reason, discovery cursor/status);
- item(id, collection_id, import_ordinal, provider_identity, uri, grant_id,
  display_name, declared_size, probe_status, typed display properties);
- operation membership(attempt_id, ordinal, item_id, frozen source identity,
  options/version); result/outcome entries keyed by attempt_id/item_id;
- grant ownership: collection, queued/active attempt and live reader references.

Use one job for a user batch, with a disk-backed immutable membership cursor and
serial native calls. Do **not** enqueue one job per 32 tracks or refill the queue
behind the user. One active/two queued logical operations remains. Queue entries
open/stage no audio. Grant exhaustion is an explicit incomplete/reselect outcome,
not evidence that all chosen files can be reopened after process death.

Probe lazily for visible/focused rows and before actual operation admission.
Off-main parsing keeps one original metadata document at a time and a bounded
projection/page cache. Evict only unleased cache entries; absence means reload or
unavailable, not zero values. Selection/focus/sort use indexed stable IDs; global
sort cannot mean sorting only the currently loaded viewport.

### Result retention and admission decision

Recommend incremental byte reservation for each next product, with every committed
item of the current logical operation pinned until that operation terminates.
When another result cannot fit the retained-results quota, stop with explicit
storage_full and keep completed items inspectable. Record remaining membership
as not attempted because storage was exhausted. No silent eviction of earlier
results in the same run and no automatic resume/retry. Explicit continuation is
a fresh attempt after user storage action; never imply it has already run.

This policy removes a count-only batch cap but does not promise a whole arbitrary
collection will fit 512 MiB. A later user-chosen streamed export design could
extend that behavior, but is outside this UI repair. Retained history should count
logical operations separately from per-item payloads; completed job-count policy
may remain 32, while item manifests must be paged/versioned.

### Draft contract amendment (not applied)

Replace only affected selection/batch clauses with:

> Collection membership is disk-backed and admits records against a documented
> storage/working-set policy, with truthful incomplete outcomes. Collections have
> no product-level 32-track cap. A Forensics operation freezes checked membership
> and processes it serially under the existing one-active/two-queued scheduler.
> Per-item acquisition, exact PCM, staging, per-call deadline and memory bounds
> remain. Result retention is admitted incrementally; exhaustion stops further
> work explicitly and preserves committed items. Comparison remains 2–32 inputs.

Amend host job/result manifest versions for paged membership/outcomes, and keep
legacy v1 readers/export. Do not change the 64 KiB native control limit: one item
per native call already fits that model. Do not silently extend the 30-minute
operation watchdog; large batches can terminate with completed items retained.
Exact resume semantics and the effect of the watchdog must be visible in the
functional design before implementation.

No replacement numeric capacity was chosen: device memory/probe expansion, row
size, grant availability and provider behavior have not been measured. Required
measurements are a bounded page/cache working set, import database growth, peak
RSS and storage exhaustion, at 33, 513 and a materially larger generated collection.
Retain 256 MiB disk safety plus staging/result reservations; account for the
collection database, metadata cache, journals and temporary files too. Select page
sizes from those measurements, not an unexplained new maximum track count.

Migration: retain existing result directories/hashes and v1 records; new collection
state need not reconstruct lost old SAF grants. Missing grants mean reselect.
Test transactional membership commits, crash before/after manifest commit, old
saved results, retry identity and lease-safe deletion. No private audio fixtures.

**F1 status:** enforcing-layer assessment and design proposal drafted; resource
policy measurements, schema/migration implementation and boundary checks pending.

## F2: recursive discovery specification, dependent on F1

Use an explicit disk-backed traversal frontier; no recursive call stack and no
in-memory list of an entire tree. Persist visited `(authority, documentId)`
identities to prevent duplicate/cyclic traversal. Reuse tree-grant ownership;
never synthesize filesystem paths. Discovery records retain the provider identity
and import ordinal separately from display name.

For deterministic completed order, propose depth-first traversal with each child
directory's entries sorted by display name then document ID using disk-backed
ordering. Show discovery progress as counts, not an invented total. Folder
providers may change during enumeration: freeze what was actually discovered;
report interruption/incomplete branches rather than asserting a coherent snapshot
of an externally changing directory.

Keep extension/MIME candidate filtering as a hint, with actual codec probing
deciding support. Record inaccessible branch, provider query failure, cancellation,
duplicate identity and storage/admission stop separately. Present loaded count and
incomplete reason; explicitly accept a discovered subset before analysis. A null
query or cancelled signal is not an empty successful folder. Generation checks
discard obsolete discovery/probe results; close cursors/leases on every exit.

Boundary scenarios: nested/deep tree; cycle/alias; duplicate filenames; duplicate
document identity across paths; empty tree; denied branch; slow/cancelled query;
provider mutation; over 32 tracks; over 512 records; exhausted grants/storage;
replacement while discovery runs; process death; retry with fresh source/grants.
Current nonrecursive contract/tests remain until these changes are implemented
together. No claim of recursive import is made by this planning packet.
