package dev.alfred.shared

/** A UI subset retains input identity/order and never re-acquires or rewrites audio. */
fun WorkspaceState.withSelectedItems(ids: Set<String>): WorkspaceState = copy(
    selection = selection?.copy(items = selection.items.filter { it.id in ids }),
    probes = probes.filterKeys { it in ids }
)

fun FeatureInputs.withSelectedItems(ids: Set<String>): FeatureInputs {
    if (ids.isEmpty() || ids.any { id -> selection.items.none { it.id == id } }) throw InputFailure("input_changed")
    val items = selection.items.filter { it.id in ids }
    return copy(selection = selection.copy(items = items), probes = probes.filterKeys { it in ids })
}
