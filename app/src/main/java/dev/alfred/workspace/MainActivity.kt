package dev.alfred.workspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alfred.forensics.ForensicsScreen
import dev.alfred.forensics.forensicsOperation
import dev.alfred.shared.FeatureId
import dev.alfred.shared.NativeBootstrap
import dev.alfred.shared.SafPicker
import dev.alfred.shared.WorkspaceInput
import dev.alfred.shared.WorkspaceState
import dev.alfred.shared.withSelectedItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import dev.alfred.shared.SharedJobs
import dev.alfred.shared.JobRecord
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import dev.alfred.shared.ResultTransfer
import dev.alfred.shared.ResultDescriptor
import dev.alfred.shared.ResultDestination
import dev.alfred.shared.ResultAction
import dev.alfred.shared.resultFailure
import kotlinx.coroutines.launch

class WorkspaceModel(application: android.app.Application) : AndroidViewModel(application) {
    val workspace = mutableStateOf(WorkspaceState())
    val jobs = SharedJobs.get(application)
    val transfers = ResultTransfer(application, jobs.results)
    val inputs = WorkspaceInput(application) { workspace.value = it }
    override fun onCleared() { inputs.close() }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val model = ViewModelProvider(this)[WorkspaceModel::class.java]
        val preferences = getSharedPreferences("workspace-ui", MODE_PRIVATE)
        setContent {
            var appearance by rememberSaveable { mutableStateOf(preferences.getString("appearance", "system") ?: "system") }
            var preset by rememberSaveable { mutableStateOf(preferences.getString("preset", "publication") ?: "publication") }
            AlfredTheme(appearance) {
                val workspace by model.workspace
                var checkedItems by rememberSaveable(workspace.selection?.id) {
                    mutableStateOf(if (workspace.selection?.incomplete == true) emptyList<String>() else workspace.selection?.items.orEmpty().map { it.id })
                }
                val checked = checkedItems.toSet()
                var route by rememberSaveable { mutableStateOf<String?>(null) }
                var transferNotice by remember { mutableStateOf("") }
                var exportAttempt by rememberSaveable { mutableStateOf<String?>(null) }
                var exportDescriptor by rememberSaveable { mutableStateOf<String?>(null) }
                val coroutine = rememberCoroutineScope()
                val destination = rememberLauncherForActivityResult(ResultDestination()) { uri ->
                    val attempt = exportAttempt
                    val descriptor = exportDescriptor
                    exportAttempt = null; exportDescriptor = null
                    if (uri != null && attempt != null && descriptor != null) coroutine.launch {
                        try { withContext(Dispatchers.IO) {
                            model.transfers.export(attempt, ResultDescriptor.from(org.json.JSONObject(descriptor)), uri).get()
                        }; transferNotice = "Export completed" }
                        catch (error: Exception) { transferNotice = "Export failed: ${resultFailure(error)} · local result retained" }
                    }
                }
                val export: ResultAction = { attempt, descriptor ->
                    exportAttempt = attempt; exportDescriptor = descriptor.json().toString()
                    destination.launch("alfred-${descriptor.kind}-${descriptor.path.substringBefore('.')}.${if (descriptor.kind == "png") "png" else "json"}")
                }
                val share: ResultAction = { attempt, descriptor -> coroutine.launch {
                    try { val intent = withContext(Dispatchers.IO) { model.transfers.share(attempt, descriptor).get() }; startActivity(android.content.Intent.createChooser(intent, "Share Alfred result")); transferNotice = "Share copy prepared" }
                    catch (error: Exception) { transferNotice = "Share failed: ${resultFailure(error)} · local result retained" }
                } }
                var nativeStatus by remember { mutableStateOf("Loading native host…") }
                val inputs = model.inputs
                var jobs by remember { mutableStateOf<List<JobRecord>>(emptyList()) }
                var progress by remember { mutableStateOf<dev.alfred.shared.JobProgress?>(null) }
                var notificationAllowed by remember { mutableStateOf(Build.VERSION.SDK_INT < 33 || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) }
                val notifications = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { notificationAllowed = it }
                LaunchedEffect(model.jobs) {
                    while (true) {
                        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                            jobs = model.jobs.snapshot(); progress = model.jobs.progressSnapshot()
                            notificationAllowed = Build.VERSION.SDK_INT < 33 || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                        }
                        delay(250)
                    }
                }
                LaunchedEffect(Unit) { nativeStatus = withContext(Dispatchers.IO) { NativeBootstrap.load() } }
                val files = rememberLauncherForActivityResult(SafPicker()) { result ->
                    if (result != null) { route = "music"; inputs.select(result) }
                }
                val single = rememberLauncherForActivityResult(SafPicker(multiple = false)) { result ->
                    if (result != null) { route = "music"; inputs.select(result) }
                }
                val folder = rememberLauncherForActivityResult(SafPicker(tree = true)) { result ->
                    if (result != null) { route = "music"; inputs.select(result) }
                }
                val selectedInputs = if (checked.isEmpty()) null else inputs.featureInputs()?.withSelectedItems(checked)
                BackHandler(route != null) {
                    route = if (route in FeatureId.entries.map { it.name }) "music" else null
                }
                Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
                    Column {
                        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Alfred", style = MaterialTheme.typography.titleLarge)
                            Row {
                                TextButton(onClick = { route = null }) { Text("Home") }
                                TextButton(onClick = { route = "settings" }) { Text("Settings") }
                            }
                        }
                        if (transferNotice.isNotEmpty()) Text(transferNotice, Modifier.padding(horizontal = 16.dp))
                        if (model.jobs.releaseUnconfirmed()) Text("Native release could not be confirmed. Work is blocked to protect its files; force-stop Alfred before reopening it.", Modifier.padding(16.dp))
                        Column(Modifier.heightIn(max = 180.dp).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
                            jobs.filter { !it.terminal }.forEach { job ->
                                Text("${job.feature} · ${job.state}${if (job.cancelRequested) " · cancellation requested" else ""}")
                                if (progress?.attemptId == job.attemptId) Text("${progress?.phase} · pass ${progress?.pass ?: "unknown"} · ${progress?.frames ?: "unknown"} frames / ${progress?.expectedFrames ?: "unknown"}")
                                TextButton(onClick = { model.jobs.cancel(job.attemptId) }) { Text("Cancel operation") }
                            }
                        }
                    }
                }) { insets ->
                    Column(Modifier.fillMaxSize().padding(insets)) {
                        when (route) {
                            "settings" -> SettingsScreen(appearance, { value -> appearance = value; preferences.edit().putString("appearance", value).apply() },
                                preset, nativeStatus, notificationAllowed,
                                { if (Build.VERSION.SDK_INT >= 33) notifications.launch(Manifest.permission.POST_NOTIFICATIONS) })
                            "music" -> MusicWorkspace(workspace, checked, { checkedItems = it.toList() }, inputs,
                                forensicsOperation, { route = it.name }, { files.launch(Unit) }, { folder.launch(Unit) })
                            in FeatureId.entries.map { it.name } -> {
                                TextButton(onClick = { route = "music" }) { Text("Back to workspace") }
                                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    when (route) {
                                        FeatureId.FORENSICS.name -> ForensicsScreen(selectedInputs, model.jobs, jobs, export, share) { feature -> route = feature.name }
                                        FeatureId.SPECTROGRAM.name -> FeatureShell("Spectrogram", "A place for visualizing the selected track's spectrum. Rendering and export are planned.")
                                        FeatureId.COMPARE.name -> FeatureShell("Compare", "A place for comparing 2–32 declared variants of the same track. Comparison is planned.")
                                    }
                                }
                            }
                            else -> HomeScreen({ single.launch(Unit) }, { files.launch(Unit) }, { folder.launch(Unit) },
                                if (workspace.selection != null || workspace.busy) ({ route = "music" }) else null, { route = it.name })
                        }
                    }
                }
            }
        }
    }
}
