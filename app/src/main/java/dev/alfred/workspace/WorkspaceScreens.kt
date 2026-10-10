package dev.alfred.workspace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alfred.shared.*

@Composable
fun AlfredTheme(appearance: String, content: @Composable () -> Unit) {
    val dark = appearance == "dark" || (appearance == "system" && isSystemInDarkTheme())
    val colors = if (dark) darkColorScheme(
        primary = Color(0xFF9CD9C9), onPrimary = Color(0xFF103B32),
        primaryContainer = Color(0xFF22483F), onPrimaryContainer = Color(0xFFC4EDE1),
        secondary = Color(0xFFB9C7C0), background = Color(0xFF141917),
        surface = Color(0xFF1B211E), onSurface = Color(0xFFE4E9E3),
        onBackground = Color(0xFFE4E9E3), surfaceVariant = Color(0xFF29332D),
        onSurfaceVariant = Color(0xFFAAB9AF), outline = Color(0xFF78867D),
        outlineVariant = Color(0xFF38443C), surfaceContainerLowest = Color(0xFF101512),
        surfaceContainerLow = Color(0xFF1B211E), surfaceContainer = Color(0xFF222B25),
        surfaceContainerHigh = Color(0xFF29332D), surfaceContainerHighest = Color(0xFF313E35))
    else lightColorScheme(
        primary = Color(0xFF245C4D), onPrimary = Color.White,
        primaryContainer = Color(0xFFDCECE4), onPrimaryContainer = Color(0xFF153C30),
        secondary = Color(0xFF53685D), background = Color(0xFFF5F5EF),
        surface = Color(0xFFFFFEF9), onSurface = Color(0xFF202B25),
        onBackground = Color(0xFF202B25), surfaceVariant = Color(0xFFEAEDE5),
        onSurfaceVariant = Color(0xFF56645B), outline = Color(0xFF79867C),
        outlineVariant = Color(0xFFD5DBD1), surfaceContainerLowest = Color(0xFFFFFEF9),
        surfaceContainerLow = Color(0xFFF5F5EF), surfaceContainer = Color(0xFFF0F1EA),
        surfaceContainerHigh = Color(0xFFEAEDE5), surfaceContainerHighest = Color(0xFFE3E8DF))
    MaterialTheme(colorScheme = colors,
        shapes = Shapes(small = RoundedCornerShape(8.dp), medium = RoundedCornerShape(14.dp), large = RoundedCornerShape(20.dp)),
        typography = Typography(
            headlineLarge = androidx.compose.ui.text.TextStyle(fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Medium, letterSpacing = (-1).sp),
            headlineMedium = androidx.compose.ui.text.TextStyle(fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Medium, letterSpacing = (-0.5).sp),
            titleLarge = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Medium),
            bodyLarge = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
            bodyMedium = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
            bodySmall = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 18.sp)
        ), content = content)
}

@Composable
fun HomeScreen(onSingle: () -> Unit, onMultiple: () -> Unit, onFolder: () -> Unit,
               onWorkspace: (() -> Unit)?, onHistory: (FeatureId) -> Unit) {
    var picker by rememberSaveable { mutableStateOf(false) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = maxHeight).padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            val ink = MaterialTheme.colorScheme.primary
            Canvas(Modifier.size(84.dp).padding(12.dp)) {
                listOf(.18f, .38f, .65f, 1f, .72f, .42f, .22f).forEachIndexed { index, amplitude ->
                    val x = size.width * (index + 1) / 8
                    drawLine(ink, Offset(x, size.height * (1 - amplitude) / 2),
                        Offset(x, size.height * (1 + amplitude) / 2), 5.dp.toPx(), StrokeCap.Round)
                }
            }
            Spacer(Modifier.height(26.dp))
            Text("Your music. In detail.", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(10.dp))
            Text("An offline audio workspace", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))
            Button(onClick = { picker = true }, modifier = Modifier.widthIn(min = 220.dp).heightIn(min = 52.dp)) { Text("Select file / folder") }
            if (onWorkspace != null) TextButton(onClick = onWorkspace) { Text("Return to music workspace") }
            TextButton(onClick = { onHistory(FeatureId.FORENSICS) }) { Text("Forensics history") }
            Spacer(Modifier.height(32.dp))
            Text("FLAC  /  WAV  /  ALAC", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Audio stays on this device", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if (picker) AlertDialog(onDismissRequest = { picker = false }, title = { Text("Add your music") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Choose up to 32 tracks. Folders include files at the first level only.")
            Button(onClick = { picker = false; onSingle() }, modifier = Modifier.fillMaxWidth()) { Text("Choose one document") }
            OutlinedButton(onClick = { picker = false; onMultiple() }, modifier = Modifier.fillMaxWidth()) { Text("Choose documents") }
            OutlinedButton(onClick = { picker = false; onFolder() }, modifier = Modifier.fillMaxWidth()) { Text("Choose folder") }
        } }, confirmButton = { TextButton(onClick = { picker = false }) { Text("Cancel") } })
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
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Make it yours", style = MaterialTheme.typography.headlineMedium)
        DetailCard("Appearance") {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("system", "light", "dark").forEach { value ->
                    FilterChip(appearance == value, onClick = { onAppearance(value) }, label = { Text(value.replaceFirstChar { it.uppercase() }) })
                }
            }
        }
        DetailCard("Job notifications") {
            Text(if (notificationsAllowed) "Notifications are on" else "Notifications are off")
            Text("Follow an analysis while Alfred is in the background.", style = MaterialTheme.typography.bodySmall)
            if (!notificationsAllowed) {
                Text("Return to Alfred to inspect progress or cancel.", style = MaterialTheme.typography.bodySmall)
                OutlinedButton(onClick = onNotifications) { Text("Allow job notifications") }
            }
        }
        DetailCard("Spectrogram resolution") {
            Text("Planned feature", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${when (preset) { "standard" -> "1600 × 900"; "large" -> "3840 × 2160"; else -> "2560 × 1440" }} · saved default", style = MaterialTheme.typography.bodySmall)
        }
        Disclosure("Storage & processing limits") {
            Text("Up to 700 MiB per track. One operation runs at a time, with two queued. Saved results: up to 32 jobs / 512 MiB. Export and share from a saved result.")
        }
        Text(nativeStatus, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun FeatureShell(title: String, description: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Planned feature", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
