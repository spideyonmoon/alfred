package dev.alfred.workspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.alfred.shared.*

@Composable
fun AlfredTheme(appearance: String, content: @Composable () -> Unit) {
    val dark = appearance == "dark" || (appearance == "system" && isSystemInDarkTheme())
    val colors = if (dark) darkColorScheme(primary = Color(0xFFADC9FF), secondary = Color(0xFFB7C9DC),
        background = Color(0xFF111820), surface = Color(0xFF18212C))
    else lightColorScheme(primary = Color(0xFF285897), secondary = Color(0xFF516578),
        background = Color(0xFFF5F7FA), surface = Color(0xFFFCFDFE), surfaceVariant = Color(0xFFE5EBF2))
    MaterialTheme(colorScheme = colors, content = content)
}

@Composable
fun HomeScreen(onSingle: () -> Unit, onMultiple: () -> Unit, onFolder: () -> Unit,
               onWorkspace: (() -> Unit)?, onHistory: (FeatureId) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Spacer(Modifier.height(24.dp))
        Text("Your audio workspace", style = MaterialTheme.typography.headlineLarge)
        Text("Select your music and run offline Audio Forensics.", style = MaterialTheme.typography.bodyLarge)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select file / folder", style = MaterialTheme.typography.titleLarge)
                Text("FLAC, WAV and ALAC/M4A · up to 32 tracks")
                Button(onClick = onSingle, modifier = Modifier.fillMaxWidth()) { Text("Choose one document") }
                OutlinedButton(onClick = onMultiple, modifier = Modifier.fillMaxWidth()) { Text("Choose documents") }
                OutlinedButton(onClick = onFolder, modifier = Modifier.fillMaxWidth()) { Text("Choose folder") }
                if (onWorkspace != null) TextButton(onClick = onWorkspace) { Text("Return to music workspace") }
            }
        }
        Text("Saved results", style = MaterialTheme.typography.titleLarge)
        OutlinedButton(onClick = { onHistory(FeatureId.FORENSICS) }, modifier = Modifier.fillMaxWidth()) { Text("Forensics history") }
        Text("Spectrogram, Compare and Metadata studio are planned features.", style = MaterialTheme.typography.bodySmall)
        Text("Audio stays on this device. Folder selection reads the first level only.", style = MaterialTheme.typography.bodySmall)
    }
}

fun featureTitle(feature: FeatureId): String = when (feature) {
    FeatureId.FORENSICS -> "Forensics"
    FeatureId.SPECTROGRAM -> "Spectrogram"
    FeatureId.COMPARE -> "Compare"
}

@Composable
fun SettingsScreen(appearance: String, onAppearance: (String) -> Unit, preset: String,
                   nativeStatus: String, notificationsAllowed: Boolean, onNotifications: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Appearance", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("system", "light", "dark").forEach { value ->
                FilterChip(appearance == value, onClick = { onAppearance(value) }, label = { Text(value.replaceFirstChar { it.uppercase() }) })
            }
        }
        Text("Spectrogram resolution · planned", style = MaterialTheme.typography.titleMedium)
        listOf("standard" to "1600 × 900", "publication" to "2560 × 1440", "large" to "3840 × 2160").forEach { (value, size) ->
            FilterChip(preset == value, onClick = {}, enabled = false, label = { Text("$size · $value") })
        }
        Text("Resolution presets will be available when Spectrogram is ready.")
        HorizontalDivider()
        Text("Job notifications", style = MaterialTheme.typography.titleMedium)
        Text(if (notificationsAllowed) "Allowed" else "Denied · operations can continue; return to Alfred to cancel or inspect progress.")
        if (!notificationsAllowed) OutlinedButton(onClick = onNotifications) { Text("Allow job notifications") }
        Text("Offline storage", style = MaterialTheme.typography.titleMedium)
        Text("Imports: up to 700 MiB per track. One operation runs at a time, with two queued. Saved results: up to 32 jobs / 512 MiB. Export and share are explicit actions from each result.")
        Text(nativeStatus, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun MusicWorkspace(state: WorkspaceState, selected: Set<String>, onSelected: (Set<String>) -> Unit,
                   inputs: WorkspaceInput, forensicOperation: Operation, onFeature: (FeatureId) -> Unit,
                   onPick: () -> Unit, onFolder: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf(0) }
    var tableFraction by rememberSaveable { mutableFloatStateOf(0.44f) }
    val panelScroll = rememberScrollState()
    LaunchedEffect(tab) { panelScroll.scrollTo(0) }
    val items = state.selection?.items.orEmpty()
    val chosen = state.withSelectedItems(selected)
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Music", style = MaterialTheme.typography.headlineSmall)
            Row {
                TextButton(onClick = onPick) { Text("Files") }
                TextButton(onClick = onFolder) { Text("Folder") }
                TextButton(onClick = {}, enabled = false) { Text("Resolution") }
            }
        }
        Text("${selected.size} of ${items.size} selected documents", style = MaterialTheme.typography.labelLarge)
        if (state.notice.isNotEmpty()) Text(state.notice, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 6.dp))
        if (state.busy) TextButton(onClick = { inputs.cancel() }) { Text("Cancel input checks") }
        BoxWithConstraints(Modifier.weight(1f)) {
            val totalPx = with(LocalDensity.current) { maxHeight.toPx() }.coerceAtLeast(1f)
            Column(Modifier.fillMaxSize()) {
                Surface(Modifier.fillMaxWidth().weight(tableFraction), shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                    TrackTable(state, selected, onSelected)
                }
                Column(Modifier.fillMaxWidth().draggable(rememberDraggableState { delta ->
                    tableFraction = (tableFraction + delta / totalPx).coerceIn(0.2f, 0.7f)
                }, Orientation.Vertical).padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Resize track table", style = MaterialTheme.typography.labelSmall)
                    Slider(tableFraction, onValueChange = { tableFraction = it }, valueRange = 0.2f..0.7f,
                        modifier = Modifier.height(24.dp).semantics { contentDescription = "Track table height" })
                }
                Column(Modifier.weight(1f - tableFraction)) {
                    TabRow(tab) {
                        Tab(tab == 0, onClick = { tab = 0 }, text = { Text("Forensic") })
                        Tab(tab == 1, onClick = { tab = 1 }, text = { Text("Metadata") })
                    }
                    Column(Modifier.fillMaxSize().verticalScroll(panelScroll).padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (state.selection?.incomplete == true) {
                            Text("This folder list is incomplete. Confirm a smaller set before checks or analysis.")
                            Button(enabled = selected.size in 1..31, onClick = { inputs.confirmFolder(selected) }) { Text("Use selected documents") }
                        } else if (tab == 0) {
                            Text("Actions panel", style = MaterialTheme.typography.titleMedium)
                            val capability = operationCapability(forensicOperation, chosen)
                            Button(enabled = capability.state == "available" && !state.busy,
                                onClick = { onFeature(FeatureId.FORENSICS) }, modifier = Modifier.fillMaxWidth()) { Text(forensicOperation.title) }
                            Text(capability.reason, style = MaterialTheme.typography.bodySmall)
                            Text("Choose full or partial analysis in Audio Forensics. Reference scores are uncalibrated; they do not measure sound quality.", style = MaterialTheme.typography.bodySmall)
                            Text("Planned tools", style = MaterialTheme.typography.titleSmall)
                            listOf(FeatureId.SPECTROGRAM, FeatureId.COMPARE).forEach { feature ->
                                OutlinedButton(onClick = { onFeature(feature) }, modifier = Modifier.fillMaxWidth()) { Text("${featureTitle(feature)} · planned") }
                            }
                        } else FeatureShell("Metadata studio", "A place for inspecting and editing tags. Metadata in completed Audio Forensics reports remains available.")
                        HorizontalDivider()
                        Text("Saved results", style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = { onFeature(FeatureId.FORENSICS) }) { Text("Forensics history") }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackTable(state: WorkspaceState, selected: Set<String>, onSelected: (Set<String>) -> Unit) {
    val items = state.selection?.items.orEmpty()
    val all = items.isNotEmpty() && items.all { it.id in selected }
    Column(Modifier.horizontalScroll(rememberScrollState()).width(900.dp)) {
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(all, onCheckedChange = { onSelected(if (it) items.map { item -> item.id }.toSet() else emptySet()) },
                modifier = Modifier.semantics { contentDescription = "Select all tracks" })
            TableCell("Song info", 290, true)
            TableCell("Resolution", 190, true)
            TableCell("Input status", 220, true)
            TableCell("Size", 145, true)
        }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            items.forEach { item ->
                val probe = state.probes[item.id]
                Row(Modifier.fillMaxWidth().background(if (item.id in selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surface), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(item.id in selected, onCheckedChange = { onSelected(if (it) selected + item.id else selected - item.id) },
                        modifier = Modifier.semantics { contentDescription = "Select ${item.name}" })
                    TableCell("${item.name}\n${probe?.codec?.uppercase() ?: "Checking header"} · ${item.grantState}", 290)
                    TableCell(probe?.let { "${it.rate?.let { rate -> "$rate Hz" } ?: "Rate unavailable"}\n${it.precision?.let { bits -> "$bits bit" } ?: "Precision unavailable"} · ${it.channels ?: "?"} ch" } ?: "Pending", 190)
                    TableCell(probe?.reason ?: "needs_input", 220)
                    TableCell(item.declaredBytes?.let { "$it bytes" } ?: "Unknown length", 145)
                }
                HorizontalDivider()
            }
            if (items.isEmpty()) Text("Select audio files or a folder to populate the table.", Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun TableCell(value: String, width: Int, heading: Boolean = false) {
    Text(value, Modifier.width(width.dp).padding(10.dp), style = if (heading) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall)
}

@Composable
fun FeatureShell(title: String, description: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text("Planned feature", style = MaterialTheme.typography.labelLarge)
            Text(description)
            Text("Audio Forensics is the available feature.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
