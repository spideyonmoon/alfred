package dev.alfred.forensics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alfred.shared.ExactFields
import dev.alfred.shared.ResultDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Native Compose presentation; the original parsed object is used only for explicit inspection. */
@Composable
fun ForensicReport(product: Map<*, *>, descriptor: ResultDescriptor) {
    val view by produceState<ReportView?>(null, product) {
        value = withContext(Dispatchers.Default) { ReportProjection(product).project() }
    }
    val report = view
    if (report == null) LinearProgressIndicator(Modifier.fillMaxWidth())
    else key(descriptor.sha256) { ReportBody(report, product, descriptor) }
}

@Composable
private fun ReportBody(report: ReportView, original: Map<*, *>, descriptor: ResultDescriptor) {
    // A single saveable key list preserves nested sections even while their parents collapse.
    var expanded by rememberSaveable { mutableStateOf(emptyList<String>()) }
    fun toggle(id: String) { expanded = if (id in expanded) expanded - id else expanded + id }
    val numeric = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum")
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(report.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-.6).sp))
        if (report.identity.isNotBlank()) Quiet(report.identity)
        if (report.format.isNotBlank()) Quiet(report.format)
        HorizontalDivider()
        Text(report.summary, style = MaterialTheme.typography.titleLarge)
        Quiet(if (report.score != null) "Uncalibrated reference assessment. Original source history remains unverified."
            else "Original source history remains unverified.")
        report.score?.let { score ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(score, style = numeric)
                Quiet("Reference score · not a probability", Modifier.weight(1f))
            }
        }
        Quiet("Native ancestry: ${report.ancestry}")
        report.points?.let { Quiet(it) }
        ReportParts(report.overview, expanded, ::toggle)
        report.sections.forEach { section ->
            DisclosureRow(section.id, section.title, section.subtitle, section.id in expanded, { toggle(section.id) }, section.id) {
                ReportParts(section.parts, expanded, ::toggle)
            }
        }
        HorizontalDivider()
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportIcon("verification")
            Column(Modifier.weight(1f)) { Text("File verification", style = MaterialTheme.typography.titleSmall); Quiet(report.verification) }
        }
        DisclosureRow("technical", "Technical data & original report", "Versions, exact values, frame boundaries & hashes", "technical" in expanded, { toggle("technical") }, "technical") {
            Quiet("Export original and Share use the retained original bytes. Display formatting never rewrites the saved report.")
            Readings(report.technical + listOf("Report SHA-256" to descriptor.sha256))
            Quiet("Channel 1/2 are engine indices 0/1. Left/right routing is not inferred from channel count.")
            DisclosureRow("rule-index", "Rule ID index", "", "rule-index" in expanded, { toggle("rule-index") }) {
                Readings(ReportRules.definitions.map { it.key to it.value.first })
            }
            DisclosureRow("original-fields", "Inspect exact original fields", "", "original-fields" in expanded, { toggle("original-fields") }) {
                ExactFields(original)
            }
        }
    }
}

@Composable
private fun ReportParts(parts: List<ReportPart>, expanded: List<String>, toggle: (String) -> Unit) {
    parts.forEach { part ->
        when (part) {
            is ReportPart.Copy -> if (part.quiet) Quiet(part.text) else Text(part.text, style = MaterialTheme.typography.bodyMedium)
            is ReportPart.Heading -> Text(part.text, Modifier.padding(top = 8.dp), style = MaterialTheme.typography.titleMedium)
            is ReportPart.Metrics -> AdaptiveCards(part.values.size) { index ->
                val (value, label) = part.values[index]
                Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum"))
                        Quiet(label)
                    }
                }
            }
            is ReportPart.Readings -> Readings(part.values)
            is ReportPart.Finding -> Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(10.dp)) {
                Column(Modifier.fillMaxWidth().padding(11.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(part.title, style = MaterialTheme.typography.titleSmall)
                    Text(part.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
            is ReportPart.Scale -> FrequencyScale(part)
            is ReportPart.Channels -> AdaptiveCards(part.cards.size, pair = true) { index ->
                val card = part.cards[index]
                Surface(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                    Column(Modifier.fillMaxWidth().padding(11.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Quiet(card.title)
                        Text(card.value, style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum", fontWeight = FontWeight.SemiBold))
                        Quiet(card.label)
                        card.rows.forEach { (label, value) -> Quiet(label); Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum")) }
                    }
                }
            }
            is ReportPart.Disclosure -> DisclosureRow(part.id, part.title, part.subtitle, part.id in expanded, { toggle(part.id) }) {
                ReportParts(part.parts, expanded, toggle)
            }
        }
    }
}

@Composable
private fun AdaptiveCards(count: Int, pair: Boolean = false, card: @Composable (Int) -> Unit) {
    if (count == 0) return
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val fontScale = LocalDensity.current.fontScale
        val columns = if (fontScale > 1.2f || maxWidth < 270.dp) 1 else if (pair) 2 else minOf(count, 3)
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            (0 until count).chunked(columns).forEach { indices ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    indices.forEach { index -> Box(Modifier.weight(1f)) { card(index) } }
                }
            }
        }
    }
}

@Composable
private fun Readings(rows: List<Pair<String, String>>) {
    val large = LocalDensity.current.fontScale > 1.2f
    rows.forEach { (label, value) ->
        if (large || value.length > 35) Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Quiet(label); Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"))
        } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Quiet(label, Modifier.weight(1f))
            Text(value, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        }
    }
}

@Composable
private fun Quiet(text: String, modifier: Modifier = Modifier) = Text(text, modifier,
    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

@Composable
private fun FrequencyScale(scale: ReportPart.Scale) {
    val n = scale.value; val limit = scale.limit
    if (n == null || limit == null || limit <= 0) { Quiet("${scale.label}: unavailable"); return }
    val ink = MaterialTheme.colorScheme.onSurface; val background = MaterialTheme.colorScheme.outlineVariant
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("${scale.label} · ${measure(n / 1000, "kHz", 3)}", style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"))
        Canvas(Modifier.fillMaxWidth().height(6.dp).semantics {
            contentDescription = "${scale.label}: ${measure(n / 1000, "kHz", 3)} on a frequency scale from zero to ${measure(limit / 1000, "kHz")}."
        }) {
            drawLine(background, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), size.height, StrokeCap.Round)
            drawLine(ink, Offset(0f, size.height / 2), Offset(size.width * (n / limit).coerceIn(0.0, 1.0).toFloat(), size.height / 2), size.height, StrokeCap.Round)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Quiet("0 Hz"); Quiet("${measure(limit / 1000, "kHz")} · Nyquist") }
    }
}

@Composable
private fun DisclosureRow(id: String, title: String, subtitle: String, open: Boolean, toggle: () -> Unit,
                          icon: String? = null, content: @Composable ColumnScope.() -> Unit) {
    key(id) {
        HorizontalDivider()
        Column(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().clickable(onClick = toggle).semantics {
                role = Role.Button; stateDescription = if (open) "Expanded" else "Collapsed"
            }.padding(vertical = 12.dp).heightIn(min = 36.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                if (icon != null) ReportIcon(icon)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(title, style = MaterialTheme.typography.titleSmall)
                    if (subtitle.isNotEmpty()) Quiet(subtitle)
                }
                Text(if (open) "⌃" else "⌄", Modifier.clearAndSetSemantics {}, style = MaterialTheme.typography.titleMedium)
            }
            if (open) Column(Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(9.dp), content = content)
        }
    }
}

/** Small code-native outline icons, no bitmap or external asset dependency. */
@Composable
private fun ReportIcon(kind: String) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(Modifier.size(21.dp).clearAndSetSemantics {}) {
        val unit = size.width / 24f
        fun line(x: Float, y: Float, x2: Float, y2: Float) = drawLine(color, Offset(x * unit, y * unit), Offset(x2 * unit, y2 * unit), 1.8f * unit, StrokeCap.Round)
        when(kind) {
            "spectral" -> listOf(3f to 4f, 7f to 12f, 12f to 18f, 17f to 12f, 21f to 4f).forEach { (x,h) -> line(x, 12-h/2, x, 12+h/2) }
            "dynamics" -> { val p=Path();p.moveTo(2*unit,12*unit);p.lineTo(7*unit,12*unit);p.lineTo(10*unit,4*unit);p.lineTo(14*unit,20*unit);p.lineTo(17*unit,12*unit);p.lineTo(22*unit,12*unit);drawPath(p,color,style=Stroke(1.8f*unit)) }
            "depth" -> { line(5f,3f,5f,10f);line(5f,15f,5f,21f);line(15f,3f,19f,3f);line(15f,3f,15f,10f);line(19f,3f,19f,10f);line(15f,10f,19f,10f);line(17f,15f,17f,21f) }
            "codec" -> { drawCircle(color,8*unit,Offset(12*unit,10*unit),style=Stroke(1.8f*unit));drawCircle(color,4*unit,Offset(12*unit,10*unit),style=Stroke(1.8f*unit));line(12f,10f,12f,21f);line(7f,16f,6f,21f);line(17f,16f,16f,21f) }
            "evidence" -> { drawCircle(color,6*unit,Offset(10*unit,10*unit),style=Stroke(1.8f*unit));line(15f,15f,21f,21f) }
            else -> { drawCircle(color,9*unit,center,style=Stroke(1.8f*unit));line(12f,11f,12f,17f);line(12f,7f,12f,7.2f) }
        }
    }
}
