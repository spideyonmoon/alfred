package dev.alfred.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias ResultAction = (String, ResultDescriptor) -> Unit

/** One document in memory; navigation pages every field and long text. */
@Composable
fun ExactFields(value: Any?) {
    var path by remember(value) { mutableStateOf<List<String>>(emptyList()) }
    var page by remember(value, path) { mutableStateOf(0) }
    var current = value
    for (part in path) current = when (val node = current) {
        is Map<*, *> -> node[part]
        is List<*> -> node[part.toInt()]
        else -> null
    }
    Text(if (path.isEmpty()) "All report fields" else path.joinToString(" / "), style = MaterialTheme.typography.titleSmall)
    if (path.isNotEmpty()) TextButton(onClick = { path = path.dropLast(1) }) { Text("Up one field") }
    val entries = when (val node = current) {
        is Map<*, *> -> node.entries.asSequence().drop(page * 24).take(24).map { it.key.toString() to it.value }.toList()
        is List<*> -> (page * 24 until minOf(node.size, (page + 1) * 24)).map { it.toString() to node[it] }
        else -> emptyList()
    }
    if (current is Map<*, *> || current is List<*>) {
        val size = when (val node = current) { is Map<*, *> -> node.size; is List<*> -> node.size; else -> 0 }
        Text("$size fields · page ${page + 1}")
        entries.forEach { (key, child) ->
            TextButton(onClick = { path = path + key }) {
                val preview = when (child) { null -> "null · unavailable"; is Map<*, *> -> "${child.size} fields"; is List<*> -> "${child.size} entries"; else -> child.toString().take(100) }
                Text("$key: $preview", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodySmall)
            }
        }
        if ((page + 1) * 24 < size) Button(onClick = { page++ }) { Text("Next fields") }
    } else {
        val text = current?.toString() ?: "null · unavailable (not zero)"
        SelectionContainer { Text(text.drop(page * 2000).take(2000)) }
        if ((page + 1) * 2000 < text.length) Button(onClick = { page++ }) { Text("Next text") }
    }
    if (page > 0) TextButton(onClick = { page-- }) { Text("Previous page") }
}

@Composable
fun ResultBrowser(feature: String, jobs: SharedJobs, records: List<JobRecord>, supported: Set<Pair<String, String>>,
                  onExport: ResultAction, onShare: ResultAction,
                  artifactPreview: @Composable (String, ResultDescriptor) -> Unit = { _, _ -> },
                  showRawFields: Boolean = true,
                  summary: @Composable (ResultDescriptor, Any?) -> Unit = { _, _ -> }) {
    var entries by remember { mutableStateOf<List<org.json.JSONObject>>(emptyList()) }
    var selected by rememberSaveable(feature) { mutableStateOf<String?>(null) }
    var descriptorIndex by rememberSaveable(selected) { mutableStateOf(0) }
    var revision by remember { mutableStateOf(0) }
    var notice by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var historyLoading by remember { mutableStateOf(true) }
    var confirmDelete by remember { mutableStateOf(false) }
    var descriptors by remember { mutableStateOf<List<ResultDescriptor>>(emptyList()) }
    var document by remember { mutableStateOf<Any?>(null) }
    var loadState by remember { mutableStateOf("loading") }
    val scope = rememberCoroutineScope()
    LaunchedEffect(feature, records, revision) {
        try {
            entries = withContext(Dispatchers.IO) {
                jobs.awaitReady(); jobs.results.entries().filter { it.getString("feature_id") == feature }.reversed()
            }
        } catch (error: Exception) { notice = resultFailure(error) }
        finally { historyLoading = false }
    }
    HorizontalDivider()
    if (notice.isNotEmpty()) Text(notice, style = MaterialTheme.typography.bodySmall)
    if (historyLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
    if (!historyLoading && entries.isEmpty()) DetailCard("No saved reports yet") {
        Text("Completed analyses will appear here. Select music in the workspace to begin.", style = MaterialTheme.typography.bodyMedium)
    }
    val unsuccessful = records.filter { it.feature == feature && it.terminal && it.state != "completed" }.reversed()
    if (unsuccessful.isNotEmpty()) Disclosure("Unsuccessful attempts (${unsuccessful.size})") {
        unsuccessful.forEach { Text("${it.state} · ${it.error ?: "No further diagnostic"} · attempt ${it.attemptId}", style = MaterialTheme.typography.bodySmall) }
    }
    if (selected == null) entries.forEach { entry ->
        val attempt = entry.getString("attempt_id")
        val created = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT)
            .format(java.util.Date(entry.optLong("created_ms")))
        DetailCard(entry.getString("state").replace('_', ' ').replaceFirstChar { it.uppercase() }) {
            Text(created, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = { selected = attempt }, modifier = Modifier.semantics {
                contentDescription = "Open ${entry.getString("state")} · $attempt"
            }) { Text("View report →") }
        }
    }
    LaunchedEffect(selected, descriptorIndex, revision) {
        document = null; descriptors = emptyList(); details = ""; loadState = "loading"
        val attempt = selected ?: return@LaunchedEffect
        try {
            val loaded = withContext(Dispatchers.IO) {
                jobs.results.pin(attempt).use {
                    val manifest = jobs.results.load(attempt)
                    val payloads = manifest.getJSONArray("payloads")
                    val list = (0 until payloads.length()).map { ResultDescriptor.from(payloads.getJSONObject(it)) }
                    val descriptor = list.getOrNull(descriptorIndex)
                    var state = descriptor?.let { jobs.results.dispatch(it, supported) } ?: "no_payload"
                    val value = if (descriptor != null && state == "available" && !descriptor.artifact) {
                        try {
                            val runtime = Runtime.getRuntime()
                            val heapFree = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory()
                            if (heapFree < descriptor.bytes * 8 + 16L * 1024 * 1024) throw InputFailure("resource_limit")
                            ExactJson.parse(jobs.results.validate(attempt, descriptor).readBytes(), 64 * 1024 * 1024)
                        } catch (error: Exception) { state = resultFailure(error); null }
                    } else null
                    Triple(list, state, value) to "${manifest.getJSONArray("outcomes")} · saved scope/tracks: ${manifest.optJSONObject("options") ?: "unavailable in older history"}"
                }
            }
            descriptors = loaded.first.first; loadState = loaded.first.second; document = loaded.first.third
            details = "Track outcomes: ${loaded.second}"
        } catch (error: Exception) { loadState = resultFailure(error) }
    }
    selected?.let { attempt ->
        TextButton(onClick = { selected = null }) { Text("‹ All saved reports") }
        if (loadState == "loading") LinearProgressIndicator(Modifier.fillMaxWidth())
        else if (loadState != "available") Text(loadState, color = MaterialTheme.colorScheme.error)
        Disclosure("Report details") {
            Text("Attempt $attempt", style = MaterialTheme.typography.bodySmall)
            Text(details, style = MaterialTheme.typography.bodySmall)
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            descriptors.forEachIndexed { index, descriptor ->
                FilterChip(selected = descriptorIndex == index, onClick = { descriptorIndex = index },
                    label = { Text("${index + 1}: ${descriptor.kind}") })
            }
        }
        descriptors.getOrNull(descriptorIndex)?.let { descriptor ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onExport(attempt, descriptor) }, modifier = Modifier.weight(1f)) { Text("Export original") }
                OutlinedButton(onClick = { onShare(attempt, descriptor) }, modifier = Modifier.weight(1f)) { Text("Share") }
            }
            Text("${descriptor.version} · ${descriptor.bytes} bytes", style = MaterialTheme.typography.bodySmall)
            if (loadState == "available" && descriptor.artifact) artifactPreview(attempt, descriptor)
            if (loadState == "available" && document != null) {
                summary(descriptor, document)
                if (showRawFields) key(attempt, descriptor.path) { Disclosure("Inspect all report fields") { ExactFields(document) } }
            } else if (loadState == "unsupported_version") Text("unsupported_version · original bytes remain exportable")
        }
        TextButton(onClick = { confirmDelete = true }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Delete local result") }
        if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false },
            title = { Text("Delete this report?") }, text = { Text("This removes the saved result from Alfred. Your source audio is unchanged.") },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Keep report") } },
            confirmButton = { TextButton(onClick = {
            confirmDelete = false
            scope.launch {
                try { withContext(Dispatchers.IO) { jobs.results.delete(attempt) }; selected = null; revision++; notice = "Deleted local result" }
                catch (error: Exception) { notice = resultFailure(error) }
            }
        }) { Text("Delete report") } })
    }
}
