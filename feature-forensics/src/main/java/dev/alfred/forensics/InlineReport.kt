package dev.alfred.forensics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alfred.shared.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

private data class SavedTrack(val descriptor: ResultDescriptor, val title: String, val itemId: String?)
private data class SavedAttempt(val tracks: List<SavedTrack>, val outcomes: List<String>)

/** Inline result navigation keeps one bounded, validated original document in memory. */
@Composable
internal fun InlineReport(attempt: String, jobs: SharedJobs, onExport: ResultAction, onShare: ResultAction, focusedItem: String? = null) {
    var saved by remember(attempt) { mutableStateOf<SavedAttempt?>(null) }
    var error by remember(attempt) { mutableStateOf<String?>(null) }
    var selectedPath by rememberSaveable(attempt) { mutableStateOf<String?>(null) }
    var document by remember(attempt) { mutableStateOf<Map<*, *>?>(null) }
    var loadedPath by remember(attempt) { mutableStateOf<String?>(null) }
    var loading by remember(attempt) { mutableStateOf(true) }
    val states = rememberSaveableStateHolder()
    LaunchedEffect(attempt) {
        try {
            saved = withContext(Dispatchers.IO) {
                jobs.awaitReady()
                jobs.results.pin(attempt).use {
                    val manifest = jobs.results.load(attempt)
                    val payloads = manifest.getJSONArray("payloads")
                    val outcomes = manifest.getJSONArray("outcomes")
                    val tracks = manifest.optJSONObject("options")?.optJSONArray("tracks")
                    val names = (0 until (tracks?.length() ?: 0)).associate { index ->
                        val track = tracks!!.getJSONObject(index)
                        track.getString("item_id") to track.optString("name", "Track ${index + 1}")
                    }
                    val titles = mutableMapOf<String, String>()
                    val identities = mutableMapOf<String, String>()
                    val failures = mutableListOf<String>()
                    for (index in 0 until outcomes.length()) {
                        val outcome = outcomes.getJSONObject(index)
                        val name = names[outcome.optString("item_id")] ?: "Track ${index + 1}"
                        val detail = outcome.optString("diagnostic")
                        val path = detail.substringAfter("payload:", "").substringBefore(';')
                        if (path.isNotEmpty()) { titles[path] = name; identities[path] = outcome.optString("item_id") }
                        if (outcome.optString("status") !in listOf("analyzed", "available", "completed")) {
                            failures += "$name · ${outcome.optString("status", "unknown")} · ${if (path.isEmpty()) detail else "See retained report diagnostics"}"
                        }
                    }
                    SavedAttempt((0 until payloads.length()).map { ResultDescriptor.from(payloads.getJSONObject(it)) }
                        .filter { it.kind == "product" && !it.artifact }.mapIndexed { index, descriptor -> SavedTrack(descriptor, titles[descriptor.path] ?: "Track ${index + 1}", identities[descriptor.path]) }, failures)
                }
            }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (failure: Exception) { error = resultFailure(failure) }
    }
    LaunchedEffect(focusedItem, saved) {
        if (focusedItem != null) selectedPath = saved?.tracks?.find { it.itemId == focusedItem }?.descriptor?.path
    }
    val selected = saved?.tracks?.find { it.descriptor.path == selectedPath }
        ?: if (focusedItem == null) saved?.tracks?.firstOrNull() else saved?.tracks?.find { it.itemId == focusedItem }
    LaunchedEffect(attempt, selected?.descriptor?.path, saved) {
        document = null; loadedPath = null; loading = true
        val descriptor = selected?.descriptor
        if (descriptor == null) { loading = false; return@LaunchedEffect }
        error = null
        try {
            document = withContext(Dispatchers.IO) {
                jobs.results.pin(attempt).use {
                    val dispatch = jobs.results.dispatch(descriptor, setOf("product" to "audio-forensic-product-v1"))
                    if (dispatch != "available") throw InputFailure(dispatch)
                    val runtime = Runtime.getRuntime()
                    val free = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory()
                    if (free < descriptor.bytes * 8 + 16L * 1024 * 1024) throw InputFailure("resource_limit")
                    ExactJson.parse(jobs.results.validate(attempt, descriptor).readBytes(), 64 * 1024 * 1024) as? Map<*, *> ?: throw InputFailure("invalid_payload")
                }
            }
            loadedPath = descriptor.path
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (failure: Exception) { error = resultFailure(failure) }
        finally { loading = false }
    }
    if (saved == null && error == null || loading) LinearProgressIndicator(Modifier.fillMaxWidth())
    error?.let { Text("Report unavailable · $it. Original bytes remain exportable when retained.", style = MaterialTheme.typography.bodyMedium) }
    saved?.let { attemptData ->
        if (attemptData.outcomes.isNotEmpty()) Disclosure("Track outcomes") { attemptData.outcomes.forEach { Text(it, style = MaterialTheme.typography.bodySmall) } }
        if (attemptData.tracks.isEmpty()) Text("No completed forensic report was saved for this attempt.")
        else if (selected == null) Text("No saved report for the focused track. Select another retained report below.")
        if (attemptData.tracks.size > 1 || selected == null) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            attemptData.tracks.forEach { track -> FilterChip(selected = track == selected,
                onClick = { selectedPath = track.descriptor.path }, label = { Text(track.title) }) }
        }
    }
    selected?.descriptor?.let { descriptor ->
        if (loadedPath == descriptor.path) document?.let { value -> states.SaveableStateProvider("$attempt/${descriptor.path}") { ForensicReport(value, descriptor) } }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onExport(attempt, descriptor) }, modifier = Modifier.weight(1f)) { Text("Export original") }
            OutlinedButton(onClick = { onShare(attempt, descriptor) }, modifier = Modifier.weight(1f)) { Text("Share") }
        }
    }
}
