package dev.alfred.workspace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alfred.shared.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val workspaceColumns = listOf("Track", "Artist", "Album", "Format", "Rate", "Bit depth", "Channels", "Size")
internal data class ReportWorkspaceRow(val id: String, val ordinal: Int, val values: List<String?>, val numbers: Map<Int, Long?>)
internal fun sortedWorkspaceRows(rows: List<ReportWorkspaceRow>, column: Int, direction: Int): List<ReportWorkspaceRow> {
    if (direction == 0 || column !in workspaceColumns.indices) return rows.sortedBy { it.ordinal }
    return rows.sortedWith { a, b ->
        val av = if (column >= 4) a.numbers[column] else a.values.getOrNull(column)
        val bv = if (column >= 4) b.numbers[column] else b.values.getOrNull(column)
        when { av == null && bv == null -> a.ordinal.compareTo(b.ordinal); av == null -> 1; bv == null -> -1
            else -> { val cmp = if (av is Long && bv is Long) av.compareTo(bv) else av.toString().compareTo(bv.toString(), ignoreCase = true)
                if (cmp == 0) a.ordinal.compareTo(b.ordinal) else cmp * direction } }
    }
}

@Composable
fun MusicWorkspace(state: WorkspaceState, selected: Set<String>, onSelected: (Set<String>) -> Unit,
                   inputs: WorkspaceInput, forensicOperation: Operation, onFeature: (FeatureId) -> Unit,
                   onPick: () -> Unit, onFolder: () -> Unit, panel: String?, onPanel: (String?) -> Unit,
                   onHome: () -> Unit, onSettings: () -> Unit, onClear: () -> Unit,
                   forensics: @Composable (String?) -> Unit) {
    var sheetRatio by rememberSaveable { mutableFloatStateOf(.56f) }
    var quick by rememberSaveable { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    var importMenu by remember { mutableStateOf(false) }
    var columnMenu by remember { mutableStateOf(false) }
    var visible by rememberSaveable { mutableStateOf(listOf(0, 1, 2, 3, 4, 5)) }
    var sortColumn by rememberSaveable { mutableIntStateOf(0) }
    var direction by rememberSaveable { mutableIntStateOf(0) }
    var focus by rememberSaveable(state.selection?.id) { mutableStateOf<String?>(null) }
    val states = rememberSaveableStateHolder()
    val items = state.selection?.items.orEmpty()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("workspace-columns", android.content.Context.MODE_PRIVATE) }
    var widths by rememberSaveable { mutableStateOf(workspaceColumns.indices.map { prefs.getInt("width-$it", if (it == 0) 180 else if (it < 3) 140 else 96).coerceIn(72, 800) }) }
    val rows by produceState<List<ReportWorkspaceRow>>(emptyList(), state.selection?.id, state.probes) {
        value = withContext(Dispatchers.IO) {
            items.mapIndexed { index, item ->
                val probe = state.probes[item.id]
                val metadata = if (probe?.metadataAvailable == true) runCatching { inputs.readMetadata(item.id) as? Map<*, *> }.getOrNull() else null
                fun tag(key: String): String? {
                    val indices = (metadata?.get("named_tags") as? Map<*, *>)?.get(key) as? List<*>
                    val n = (indices?.firstOrNull() as? Number)?.toInt() ?: return null
                    return (((metadata["entries"] as? List<*>)?.getOrNull(n)) as? Map<*, *>)?.get("value") as? String
                }
                ReportWorkspaceRow(item.id, index, listOf(tag("title") ?: item.name, tag("artist"), tag("album"), probe?.codec?.uppercase(),
                    probe?.rate?.let { "$it Hz" }, probe?.precision?.let { "$it bit" }, probe?.channels, item.declaredBytes?.let { "$it bytes" }),
                    mapOf(4 to probe?.rate, 5 to probe?.precision?.toLongOrNull(), 6 to probe?.channels?.toLongOrNull(), 7 to item.declaredBytes))
            }
        }
    }
    val sorted = remember(rows, sortColumn, direction) { sortedWorkspaceRows(rows, sortColumn, direction) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().heightIn(min = 46.dp).padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onHome, modifier = Modifier.semantics { contentDescription = "Back to Home" }, contentPadding = PaddingValues(0.dp)) { Text("←") }
            Text("${selected.size} selected", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { quick = !quick }, modifier = Modifier.semantics { contentDescription = "Quick controls"; stateDescription = if (quick) "Expanded" else "Collapsed" }, contentPadding = PaddingValues(0.dp)) { Text(if (quick) "⌃" else "⌄") }
            Box {
                val ink = MaterialTheme.colorScheme.onSurface
                IconButton(onClick = { menu = true }, modifier = Modifier.semantics { contentDescription = "Workspace actions" }) {
                    Canvas(Modifier.size(24.dp)) { listOf(3f to 6f,8f to 12f,13f to 18f).forEach { (x,y) -> drawLine(ink, Offset(size.width*x/24,size.height*y/24),Offset(size.width*21/24,size.height*y/24),1.8.dp.toPx(),StrokeCap.Round) } }
                }
                DropdownMenu(menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Columns") }, onClick = { menu = false; columnMenu = true })
                    DropdownMenuItem(text = { Text("Clear") }, enabled = !state.busy, onClick = { menu = false; onClear() })
                    DropdownMenuItem(text = { Text("Import") }, onClick = { menu = false; importMenu = true })
                    DropdownMenuItem(text = { Text("Saved reports") }, onClick = { menu = false; onFeature(FeatureId.FORENSICS) })
                    DropdownMenuItem(text = { Text("Settings") }, onClick = { menu = false; onSettings() })
                }
            }
        }
        if (quick) Surface(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), shape = RoundedCornerShape(10.dp), tonalElevation = 2.dp) {
            // Only implemented actions appear here; the HTML's future converter specimen is not a control.
            Row(Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onFeature(FeatureId.FORENSICS) }) { Text("Saved reports") }
                TextButton(onClick = onSettings) { Text("Settings") }
            }
        }
        if (state.busy) Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Checking inputs…", Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = inputs::cancel) { Text("Cancel") }
        }
        if (state.notice.isNotBlank() && state.notice != "Input checks complete. Header support does not verify decoded PCM.") Text(state.notice, Modifier.padding(horizontal = 10.dp), style = MaterialTheme.typography.bodySmall)
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
            val density = LocalDensity.current
            val height = maxHeight
            val pixels = with(density) { height.toPx() }.coerceAtLeast(1f)
            val minimum = (132.dp * density.fontScale).coerceAtMost(height)
            val sheetHeight = (height * sheetRatio).coerceIn(minimum, height)
            val tableHorizontal = rememberScrollState()
            val tableVertical = rememberScrollState()
            val rowHeight = 48.dp * density.fontScale
            val headerHeight = 48.dp * density.fontScale
            Column(Modifier.fillMaxSize().horizontalScroll(tableHorizontal)) {
                Row(Modifier.width((48 + visible.sumOf { widths[it] }).dp).background(MaterialTheme.colorScheme.surfaceContainerHigh), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(48.dp).height(headerHeight))
                    visible.forEach { index ->
                        Box(Modifier.width(widths[index].dp).height(headerHeight)) {
                            TextButton(onClick = { if (sortColumn != index) { sortColumn = index; direction = 1 } else direction = when(direction) { 0 -> 1; 1 -> -1; else -> 0 } }, modifier = Modifier.fillMaxWidth().padding(end = 12.dp), contentPadding = PaddingValues(horizontal = 6.dp)) {
                                Text(workspaceColumns[index] + if (sortColumn == index && direction != 0) if (direction > 0) " ↑" else " ↓" else "", maxLines = 1)
                            }
                            Box(Modifier.align(Alignment.CenterEnd).width(12.dp).height(44.dp).draggable(rememberDraggableState { delta ->
                                val next = widths.toMutableList(); next[index] = (widths[index] + with(density) { delta.toDp().value }.toInt()).coerceIn(72,800); widths = next
                            }, Orientation.Horizontal, onDragStopped = { prefs.edit().putInt("width-$index", widths[index]).apply() }).semantics {
                                contentDescription = "Resize ${workspaceColumns[index]} column"
                                customActions = listOf(CustomAccessibilityAction("Widen") { val next=widths.toMutableList();next[index]=(next[index]+24).coerceAtMost(800);widths=next;prefs.edit().putInt("width-$index",next[index]).apply();true }, CustomAccessibilityAction("Narrow") { val next=widths.toMutableList();next[index]=(next[index]-24).coerceAtLeast(72);widths=next;prefs.edit().putInt("width-$index",next[index]).apply();true })
                            }, contentAlignment = Alignment.Center) { VerticalDivider(Modifier.height(20.dp)) }
                        }
                    }
                }
                Column(Modifier.width((48 + visible.sumOf { widths[it] }).dp).weight(1f).verticalScroll(tableVertical).padding(bottom = sheetHeight)) {
                    sorted.forEach { row ->
                        Row(Modifier.fillMaxWidth().height(rowHeight).background(if (row.id == focus) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surface), verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(48.dp).height(rowHeight))
                            visible.forEach { column -> Text(row.values[column] ?: "—", Modifier.width(widths[column].dp).clickable { focus = row.id; onPanel("analysis") }.padding(horizontal = 8.dp, vertical = 12.dp), style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                        HorizontalDivider()
                    }
                }
            }
            // The shared vertical state keeps the frozen check column aligned with rows.
            Column(Modifier.width(48.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surface)) {
                    TriStateCheckbox(state = when { selected.isEmpty() -> ToggleableState.Off; items.isNotEmpty() && items.all { it.id in selected } -> ToggleableState.On; else -> ToggleableState.Indeterminate },
                        onClick = { onSelected(if (items.isNotEmpty() && items.all { it.id in selected }) emptySet() else items.map { it.id }.toSet()) }, modifier = Modifier.width(48.dp).height(headerHeight).semantics { contentDescription = "Select all tracks" })
                Column(Modifier.weight(1f).verticalScroll(tableVertical).padding(bottom = sheetHeight)) {
                    sorted.forEach { row ->
                            Checkbox(row.id in selected, onCheckedChange = { onSelected(if (it) selected + row.id else selected - row.id) }, modifier = Modifier.width(48.dp).height(rowHeight).semantics { contentDescription = "Select ${row.values[0]}" })
                        HorizontalDivider()
                    }
                }
            }
            Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(sheetHeight), shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp), shadowElevation = 3.dp) {
                Column {
                    Box(Modifier.fillMaxWidth().height(24.dp).draggable(rememberDraggableState { delta -> sheetRatio = (sheetRatio - delta / pixels).coerceIn(minimum.value / height.value.coerceAtLeast(1f), 1f) }, Orientation.Vertical).semantics {
                        contentDescription = "Bottom sheet height"; progressBarRangeInfo = ProgressBarRangeInfo(sheetRatio, 0f..1f)
                        setProgress { sheetRatio = it.coerceIn(0f,1f); true }
                        customActions = listOf(CustomAccessibilityAction("Fully expand") { sheetRatio=1f;true }, CustomAccessibilityAction("Normal height") { sheetRatio=.56f;true })
                    }, contentAlignment = Alignment.Center) { Box(Modifier.width(30.dp).height(3.dp).background(MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(3.dp))) }
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        listOf("Forensic", "Metadata", "Log", "Convert").forEach { title -> Text(title, Modifier.padding(horizontal = 12.dp, vertical = 8.dp).semantics { if(title != "Forensic") disabled() }, style = MaterialTheme.typography.labelLarge, color = if(title == "Forensic") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        listOf("analysis" to "Audio Forensics", "spectrogram" to "Spectrogram", "compare" to "Compare").forEach { (id,title) ->
                            TextButton(onClick = { onPanel(id) }, modifier = Modifier.semantics { this.selected = (panel ?: "analysis") == id }, contentPadding = PaddingValues(horizontal = 8.dp)) { Text(title, style = MaterialTheme.typography.labelMedium) }
                        }
                    }
                    states.SaveableStateProvider("${panel ?: "analysis"}|${focus.orEmpty()}") {
                        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (state.selection?.incomplete == true) {
                                Text("Folder list incomplete. Select fewer than 32 tracks to continue.")
                                Button(enabled = selected.size in 1..31, onClick = { inputs.confirmFolder(selected) }) { Text("Use selected documents") }
                            } else when(panel ?: "analysis") {
                                "analysis" -> { val capability = operationCapability(forensicOperation, state.withSelectedItems(selected));if (capability.state != "available" && items.isNotEmpty()) Text(capability.reason, style = MaterialTheme.typography.bodySmall);forensics(focus) }
                                "spectrogram" -> Text("No preview loaded", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                "compare" -> Text("No comparison loaded", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
    if (importMenu) AlertDialog(onDismissRequest = { importMenu = false }, title = { Text("Import") }, text = {
        Column { TextButton(onClick = { importMenu = false; onPick() }) { Text("Files") };TextButton(onClick = { importMenu = false; onFolder() }) { Text("Folder") } }
    }, confirmButton = { TextButton(onClick = { importMenu = false }) { Text("Close") } })
    if (columnMenu) AlertDialog(onDismissRequest = { columnMenu = false }, title = { Text("Columns") }, text = {
        Column(Modifier.verticalScroll(rememberScrollState())) { workspaceColumns.forEachIndexed { index,title -> Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(index in visible, enabled = index != 0, onCheckedChange = { visible = if(it) (visible + index).sorted() else visible - index });Text(title)
        } } }
    }, confirmButton = { TextButton(onClick = { columnMenu = false }) { Text("Done") } })
}
