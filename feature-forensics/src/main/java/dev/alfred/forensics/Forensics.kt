package dev.alfred.forensics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.alfred.shared.*
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

val forensicsOperation = Operation(FeatureId.FORENSICS, "Audio Forensics", 1, 32)

@Composable
fun ForensicsScreen(inputs: FeatureInputs?, jobs: SharedJobs, records: List<JobRecord>,
                    onExport: ResultAction, onShare: ResultAction, showHistory: Boolean = true, focusedItem: String? = null) {
    val app = LocalContext.current.applicationContext
    val coroutine = rememberCoroutineScope()
    var notice by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    val ids = inputs?.selection?.items?.map { it.id }.orEmpty()
    var launched by rememberSaveable(inputs?.selection?.id) { mutableStateOf<String?>(null) }
    val latest = records.filter { it.feature == "forensics" && (if (focusedItem != null) focusedItem in it.items else it.attemptId == launched || ids.isNotEmpty() && it.items == ids) }.maxByOrNull { it.createdMs }
    if (showHistory) {
        Text("Forensics history", style = MaterialTheme.typography.headlineMedium)
        ResultBrowser("forensics", jobs, records, setOf("product" to "audio-forensic-product-v1", "alfred-result" to "1"),
            onExport, onShare, showRawFields = false) { descriptor, value ->
            if (descriptor.kind == "product" && value is Map<*, *>) ForensicReport(value, descriptor)
            else Disclosure("Technical data") { ExactFields(value) }
        }
        return
    }
    if (notice.isNotEmpty()) Text(notice, style = MaterialTheme.typography.bodySmall)
    records.filter { it.feature == "forensics" && !it.terminal }.forEach { job ->
        Text(if (job.cancelRequested) "Cancellation requested" else "Analysis ${job.state.replace('_', ' ')}", style = MaterialTheme.typography.titleSmall)
        val progress = jobs.progressSnapshot()
        if (progress?.attemptId == job.attemptId) Text(progress.phase.replace('_', ' '), style = MaterialTheme.typography.bodySmall)
        LinearProgressIndicator(Modifier.fillMaxWidth())
        TextButton(enabled = !job.cancelRequested, onClick = { jobs.cancel(job.attemptId) }) { Text("Cancel analysis") }
    }
    if (inputs != null) {
        Button(enabled = !submitting && ids.size in 1..32 && inputs.probes.values.any { it.status == "available" }, onClick = {
            submitting = true
            coroutine.launch {
                try {
                    val record = withContext(Dispatchers.IO) { ForensicsWork.submit(app, inputs, JSONObject().put("kind", "full")).get() }
                    launched = record.attemptId
                    notice = ""
                } catch (cancelled: CancellationException) { throw cancelled }
                catch (failure: Exception) { notice = "Analysis could not start · ${resultFailure(failure)}" }
                finally { submitting = false }
            }
        }) { Text(if (latest?.terminal == true) "Scan again · ${ids.size} selected" else "Scan · ${ids.size} selected") }
    } else if (latest == null) Text("Select tracks to analyze.", style = MaterialTheme.typography.bodyMedium)
    latest?.let { result ->
        if (result.terminal) {
            if (result.state != "completed") Text("Analysis ${result.state} · ${result.error ?: "No completed finding"}", style = MaterialTheme.typography.bodyMedium)
            InlineReport(result.attemptId, jobs, onExport, onShare, focusedItem)
        }
    }
}
