package dev.alfred.forensics

import java.util.Locale
import kotlin.math.abs

internal typealias Fields = Map<*, *>
internal fun Any?.fields(): Fields = this as? Map<*, *> ?: emptyMap<Any, Any>()
internal fun Fields.obj(key: String): Fields = get(key).fields()
internal fun Fields.items(key: String): List<Fields> = (get(key) as? List<*>)?.map { it.fields() }.orEmpty()
internal fun Fields.number(key: String): Double? = (get(key) as? Number)?.toDouble()?.takeIf { it.isFinite() }
internal fun Fields.text(key: String): String? = get(key) as? String
internal fun Fields.version(key: String, expected: String) = get(key)?.toString() == expected
internal fun display(n: Double?, digits: Int = 2): String = when {
    n == null || !n.isFinite() -> "Unavailable"
    n != 0.0 && abs(n) < .5 * Math.pow(10.0, -digits.toDouble()) -> String.format(Locale.ROOT, "%.2e", n).replace('-', '−')
    else -> String.format(Locale.ROOT, "%.${digits}f", n).replace('-', '−')
}
internal fun measure(n: Double?, unit: String, digits: Int = 2) = if (n == null) "Unavailable" else "${display(n, digits)} $unit"
internal fun clockTime(seconds: Double?): String = if (seconds == null || !seconds.isFinite() || seconds < 0) "Unavailable"
    else String.format(Locale.ROOT, "%d:%06.3f", (seconds / 60).toInt(), seconds % 60)

/** Display models contain stored observations only. No predicate execution or classifier. */
internal sealed interface ReportPart {
    data class Copy(val text: String, val quiet: Boolean = true) : ReportPart
    data class Heading(val text: String) : ReportPart
    data class Metrics(val values: List<Pair<String, String>>) : ReportPart
    data class Readings(val values: List<Pair<String, String>>) : ReportPart
    data class Finding(val title: String, val text: String) : ReportPart
    data class Scale(val value: Double?, val limit: Double?, val label: String) : ReportPart
    data class Channels(val cards: List<ChannelCard>) : ReportPart
    data class Disclosure(val id: String, val title: String, val subtitle: String = "", val parts: List<ReportPart>) : ReportPart
}
internal data class ChannelCard(val title: String, val value: String, val label: String, val rows: List<Pair<String, String>> = emptyList())
internal data class Investigation(val id: String, val title: String, val subtitle: String, val parts: List<ReportPart>)
internal data class ReportView(val title: String, val identity: String, val format: String, val summary: String,
    val score: String?, val ancestry: String, val points: String?, val overview: List<ReportPart>,
    val sections: List<Investigation>, val verification: String, val technical: List<Pair<String, String>>)

/** All version gates are independent; missing/unsupported components never become zero. */
internal class ReportProjection(private val product: Fields) {
    private val known = product.version("product_schema_version", "audio-forensic-product-v1") && product.version("contract_version", "1")
    private val native = product.obj("measurement_report").takeIf { known && it.version("schema_version", "0.18.0") && it.version("policy_version", "observations-only-v18") }.orEmpty()
    private val reference = product.obj("reference_assessment").takeIf { known && it.version("assessment_version", "1") && it.version("contract_version", "1") && it.version("method_id", "python-reference-c6ecce2-v1") }.orEmpty()
    private val metadata = product.obj("metadata").takeIf { known && it.version("metadata_version", "1") && it.version("contract_version", "1") }.orEmpty()
    private val statistics = product.obj("tool_statistics").takeIf { known && it.version("statistics_version", "1") && it.version("contract_version", "1") && it.version("method_id", "ffmpeg-7.1.1-sox-14.4.2-v1") }.orEmpty()
    private val referenceInputs = product.obj("reference_inputs").takeIf { known && it.version("version", "2") && it.version("method", "python-c6ecce2-p04abc-f32-v2") }.orEmpty()
    private val features = reference.obj("features")
    private val technical = metadata.obj("technical")
    private val coverage = native.obj("coverage")
    private val channels = native.items("channels")
    private val peaks = native.items("true_peak")
    private val floor = native.items("noise_floor")
    private val loudness = native.obj("loudness")
    private val dr = statistics.obj("measurements").obj("dr")
    private val rules = reference.items("rules")
    private val rate = native.obj("stream").number("sample_rate") ?: technical.number("declared_sample_rate_hz")
    private val nyquist = rate?.div(2)
    private val measuredScope = if (coverage["reached_end"] == true) "Entire track" else "Decoded interval"
    private fun feature(key: String) = features.obj(key).takeIf { it.version("version", "2") && it.text("status") == "available" }.orEmpty()
    private fun f(key: String) = feature(key).number("value")
    private fun scoped(interval: Fields, sampleRate: Double? = rate): String {
        if (sampleRate == null || sampleRate <= 0) return "Scope unavailable"
        return if (interval.number("start_frame") == null || interval.number("end_frame") == null) "Scope unavailable"
        else "${clockTime(interval.number("start_frame")!! / sampleRate)}–${clockTime(interval.number("end_frame")!! / sampleRate)}"
    }
    private fun scope(key: String): String {
        val x = feature(key)
        return x.items("intervals").joinToString(", ") { scoped(it, x.number("sample_rate")) }.ifBlank { "Scope unavailable" }
    }
    private fun fv(key: String): String {
        val x = feature(key); val n = x.number("value") ?: return "Unavailable"
        return when {
            x.text("unit") == "boolean" -> if (n == 1.0) "Observed" else "Not observed"
            key == "resample" && n == 0.0 -> "No source-rate hit"
            x.text("unit") == "Hz" -> measure(n / 1000, "kHz", 3)
            else -> measure(n, x.text("unit").orEmpty(), when(key) { "aac", "vorbis" -> 4; "hf", "silence_ratio", "sparsity" -> 5; else -> 3 })
        }
    }
    private fun readings(vararg keys: String) = ReportPart.Readings(keys.map { (ReportRules.featureNames[it] ?: it) to fv(it) })
    private fun tag(key: String): String? {
        val named = metadata.obj("named_tags")[key]
        val index = ((named as? List<*>)?.firstOrNull() as? Number)?.toInt() ?: return null
        return metadata.items("entries").getOrNull(index)?.text("value")
    }
    private fun numeric(x: Fields) = x.takeIf { it.text("availability") == "available" }?.number("value")
    private fun rule(id: String) = rules.find { it.text("id") == id }.orEmpty()
    private fun state(rule: Fields) = ReportRules.states[rule.text("state")] ?: "Unrecognized state"
    private fun effect(rule: Fields): String {
        when(rule.text("state")) {
            "excluded_by_contract" -> return "Excluded; no score contribution."
            "unavailable" -> return "Required evidence unavailable; no assessment from missing values."
            "vetoed" -> return "Suppressed by ${ReportRules.definitions[rule.text("causal_veto_rule")]?.first ?: "another recorded rule"}."
        }
        val before = rule.obj("before"); val after = rule.obj("after")
        val changes = listOf("lossy" to "Lossy points", "natural" to "Natural points", "net" to "Net points", "heuristic" to "Initial heuristic", "main" to "Main score").mapNotNull { (key, label) ->
            val start = before.number(key); val end = after.number(key)
            if (start != null && end != null && start != end) "$label: ${display(start, 0)} → ${display(end, 0)}" else null
        }
        return changes.joinToString(" · ").ifBlank { if (rule.text("id") in listOf("R33", "R34")) "Descriptive interpretation; no ancestry points." else "No score change." }
    }
    private fun ruleDetail(id: String, prefix: String): ReportPart.Disclosure? {
        val r = rule(id); val definition = ReportRules.definitions[id] ?: return null
        if (r.isEmpty()) return null
        val keys = ((r["input_feature_ids"] as? List<*>)?.filterIsInstance<String>().orEmpty() + ReportRules.supplements[id].orEmpty())
            .distinct().filter { it in ReportRules.featureNames }
        val parts = mutableListOf<ReportPart>(readings(*keys.toTypedArray()), ReportPart.Copy(definition.second, false), ReportPart.Finding(state(r), effect(r)))
        if (keys.isNotEmpty()) parts += ReportPart.Disclosure("$prefix-$id-scope", "Measurement scopes", parts = keys.groupBy { scope(it) }.map { (time, names) ->
            ReportPart.Copy("${names.joinToString { ReportRules.featureNames[it].orEmpty() }}: $time")
        })
        if (id in listOf("R18", "R19", "R20")) parts += ReportPart.Copy("Individual excerpts and eligibility are in Spectral investigation. Excluded excerpts are not negative votes.")
        parts += ReportPart.Copy("Technical record: $id. Exact operands, predicates and causal links remain in Technical data.")
        return ReportPart.Disclosure("$prefix-$id", definition.first, state(r), parts)
    }
    private fun paired(value: (Fields) -> String, label: String, rows: (Fields) -> List<Pair<String, String>> = { emptyList() }) = ReportPart.Channels(channels.map {
        ChannelCard("Channel ${display(it.number("channel_index")?.plus(1), 0)}", value(it), label, rows(it))
    })
    private fun channelAt(list: List<Fields>, channel: Fields) = list.find { it["channel_index"] == channel["channel_index"] }.orEmpty()
    private fun signed(n: Double?) = if (n == null) "Unavailable" else (if (n > 0) "+" else "") + display(n, 3)
    private fun priority(): List<Pair<String, ReportPart.Finding>> {
        val result = mutableListOf<Pair<String, ReportPart.Finding>>()
        reference.items("source_candidates").filter { it["matched"] == true }.forEach {
            result += (if (it.text("id") == "resampling") "codec" else "spectral") to ReportPart.Finding(ReportRules.candidates[it.text("id")] ?: "Source candidate", it.text("display") ?: "Inspect the recorded candidate evidence.")
        }
        for ((id, title) in listOf("R29" to "AAC rule contributed", "R30" to "Vorbis rule contributed")) {
            val r = rule(id); val before = r.obj("before").number("main"); val after = r.obj("after").number("main")
            if (r.text("state") == "applied" && before != null && after != null && after > before) result += "codec" to ReportPart.Finding(title, effect(r))
        }
        if ((native.obj("mqa").number("sync_matches") ?: 0.0) > 0) result += "codec" to ReportPart.Finding("MQA sync candidates", "Signalling observations in the searched interval, not authentication or a decoded payload.")
        reference.items("depth_candidates").filter { it["matched"] == true }.forEach { result += "depth" to ReportPart.Finding("Depth candidate", it.text("display") ?: "Inspect depth evidence.") }
        val declared = technical.number("declared_precision_bits")
        if (declared != null && channels.any { it.number("exact_used_bits")?.let { bits -> bits < declared } == true }) result += "depth" to ReportPart.Finding("Fewer bits exercised than declared", "Sample activity differs from container precision. This alone does not establish original source depth.")
        return result
    }

    fun project(): ReportView {
        val nativeStatus = native.text("status")
        if (!known || nativeStatus in listOf("failed", "unsupported", "cancelled", "timed_out")) {
            return ReportView(native.text("source")?.substringAfterLast('/')?.substringAfterLast('\\') ?: "Saved audio report", "", "",
                if (!known) "Unsupported report version" else "Analysis ${nativeStatus?.replace('_', ' ')}", null,
                "Unavailable", null, listOf(ReportPart.Copy("No completed forensic conclusion is available. Original bytes and diagnostics remain accessible in Technical data.")),
                emptyList(), "No successful full-track verification claimed.",
                listOf("Engine" to (product.text("engine_version") ?: "Unavailable"), "Product" to (product.text("product_schema_version") ?: "Unavailable")))
        }
        val overview = mutableListOf<ReportPart>()
        val important = priority(); overview += important.map { it.second }
        if (reference.isNotEmpty() && reference.text("status") != "available") {
            val missing = (reference["missing_inputs"] as? List<*>)?.filterIsInstance<String>().orEmpty()
            overview += ReportPart.Finding("Reference assessment ${reference.text("status") ?: "unavailable"}",
                if (missing.isEmpty()) "Some interpretation is unavailable; supported measurements remain below."
                else "Required evidence: ${missing.joinToString { ReportRules.featureNames[it] ?: it.replace('_', ' ') }}.")
        }
        overview += ReportPart.Metrics(listOf((dr.text("overall_integer_label") ?: "Unavailable") to "Dynamic range · drmeter",
            display(loudness.number("integrated_lufs")) to "LUFS integrated", display(loudness.obj("range").number("range_lu")) to "Loudness range · LU"))
        if (reference.isNotEmpty()) {
            overview += ReportPart.Heading("Frequency analysis")
            overview += ReportPart.Metrics(listOf(measure(f("cutoff")?.div(1000), "kHz", 3) to "Reference cutoff · mid-channel estimate"))
            overview += ReportPart.Scale(f("cutoff"), nyquist, "Reference cutoff")
            overview += ReportPart.Copy("Cliff ${fv("cliff")}. A cutoff position, not a plotted spectrum.")
            if (rule("R01").text("state") == "not_triggered" && rule("R02").text("state") == "not_triggered") overview += ReportPart.Copy("The low-bandwidth and abrupt-edge rules did not trigger. Bandwidth alone cannot establish encoding history.")
        }
        if (peaks.isNotEmpty()) {
            overview += ReportPart.Heading("True peak · channel comparison")
            overview += ReportPart.Channels(peaks.map { ChannelCard("Channel ${display(it.number("channel_index")?.plus(1), 0)}", signed(it.number("estimated_peak_dbtp")), "dBTP · estimated") })
            overview += ReportPart.Copy("4× FIR interpolation estimates. Peaks above zero do not establish clipping or audible distortion.")
        }
        val mqa = native.obj("mqa")
        if (reference.isNotEmpty() || mqa.isNotEmpty()) {
            overview += ReportPart.Heading("Codec & source fingerprints")
            if (reference.isNotEmpty()) overview += ReportPart.Readings(listOf("AAC · ${scope("aac")}" to display(f("aac"), 4), "Vorbis · ${scope("vorbis")}" to display(f("vorbis"), 4), "Resampling candidate" to candidateState(reference.items("source_candidates").find { it.text("id") == "resampling" }.orEmpty())))
            if (mqa.isNotEmpty()) overview += ReportPart.Readings(listOf("MQA · first ${display(mqa.number("scanned_frames")?.let { n -> rate?.takeIf { it > 0 }?.let { n / it } }, 3)} seconds" to when(mqa.text("status")) { "not_detected" -> "Not detected"; else -> if ((mqa.number("sync_matches") ?: 0.0) > 0) "Candidates observed" else "Unavailable" }))
        }
        if (reference.isNotEmpty()) {
            overview += ReportPart.Heading("Declared & exercised precision")
            overview += ReportPart.Metrics(listOf(display(f("depth_claimed"), 0) to "Declared bits") + channels.map { display(f("effective_bits_channel_${display(it.number("channel_index"), 0)}"), 0) to "Channel ${display(it.number("channel_index")?.plus(1), 0)} · bits" })
            overview += ReportPart.Copy("Reference activity · ${scope("effective_bits_channel_0")}. Exercised bits do not establish original recording precision.")
        }
        val sections = listOf(spectral(), dynamics(), codec(), depth(), evidence())
        val order = (important.map { it.first } + sections.map { it.id }).distinct()
        val summary = reference.text("display_summary")?.let { if (it == "Uncalibrated reference method: no strong lossy indicators; source history is unverified.") "No strong lossy indicators" else it }
            ?: "Reference interpretation unavailable"
        val filename = native.text("source")?.substringAfterLast('/')?.substringAfterLast('\\') ?: "Saved audio report"
        return ReportView(tag("title") ?: filename, listOfNotNull(tag("artist"), tag("album")).joinToString(" · "),
            listOfNotNull(technical.text("container"), rate?.let { measure(it / 1000, "kHz", 1) }, technical.number("declared_precision_bits")?.let { measure(it, "bit", 0) }, technical.number("declared_channels")?.let { if (it == 2.0) "Stereo" else measure(it, "channels", 0) }, coverage.number("end_seconds")?.let { clockTime(it) }).joinToString(" · "),
            summary, reference.takeIf { it.isNotEmpty() }?.let { display(it.obj("scores").number("main"), 0) }, native.text("ancestry_verdict") ?: "Unavailable",
            reference.takeIf { it.isNotEmpty() }?.let { "${display(it.obj("scores").number("lossy"), 0)} lossy / ${display(it.obj("scores").number("natural"), 0)} natural points" },
            overview, order.mapNotNull { id -> sections.find { it.id == id } },
            if (coverage.isEmpty()) "Decode verification unavailable." else "$measuredScope decoded · ${clockTime(coverage.number("end_seconds"))}. Header length ${if (coverage["length_matches_header"] == true) "matches" else "unverified"}. Decoder verification ${if (coverage["decoder_verification"] == true) "passed" else "unconfirmed"}.",
            listOf("Engine" to (product.text("engine_version") ?: "Unavailable"), "Product" to (product.text("product_schema_version") ?: "Unavailable"), "Reference method" to (product.obj("reference_assessment").text("method_id") ?: "Unavailable"), "PCM SHA-256" to (coverage.text("decoded_pcm_sha256") ?: "Unavailable"), "Measurement schema" to (product.obj("measurement_report").text("schema_version") ?: "Unavailable")))
    }
    private fun candidateState(c: Fields) = when(c["matched"]) { true -> "Candidate recorded"; false -> "Not indicated by these rules"; else -> "Unavailable" }
    private fun spectral(): Investigation {
        val parts = mutableListOf<ReportPart>()
        if (channels.isNotEmpty()) {
            parts += paired({ measure(it.obj("spectral").number("cutoff_p95_hz")?.div(1000), "kHz", 3) }, "Native 95th-percentile cutoff", { listOf("Cliff" to measure(it.obj("spectral").number("cliff_depth_db"), "dB"), "Sharpness" to measure(it.obj("spectral").number("sharpness_db_per_bin"), "dB/bin", 3)) })
            parts += ReportPart.Copy("Native channel statistics and the reference mid-channel cutoff use different estimators. Native spectral windows lie within the decoded interval.")
        }
        if (reference.isNotEmpty()) {
            parts += readings("cutoff", "cliff", "sharpness", "hf", "entropy", "envelope", "banding", "sparsity", "bound", "variance")
            parts += ReportPart.Copy("HF is a magnitude ratio. Envelope is signed Pearson correlation. Banding and sparsity are different structural measurements, not confidence.")
            parts += listOfNotNull(ruleDetail("R01", "spectral"), ruleDetail("R02", "spectral"), ruleDetail("R28", "spectral"))
        }
        val segments = referenceInputs.obj("segments"); val probes = segments.items("probes"); val vote = segments.obj("classic_vote")
        if (probes.isNotEmpty()) {
            parts += ReportPart.Heading("${probes.size} sampled excerpts")
            parts += ReportPart.Finding("${display(vote.number("eligible_walled_probes"), 0)} wall votes / ${display(vote.number("eligible_probes"), 0)} eligible", "Discrete excerpt observations; excluded excerpts are not negative votes. Gaps between excerpts were not measured by this probe set.")
            parts += ReportPart.Readings(listOf("Adaptive wall" to measure(segments.obj("adaptive_wall_hz").number("value")?.div(1000), "kHz", 3), "Majority wall" to when(vote["majority_on_eligible_probes"]) { true -> "Observed"; false -> "Not observed"; else -> "Unavailable" }))
            probes.forEachIndexed { index, p ->
                parts += ReportPart.Heading(scoped(p.obj("interval")))
                parts += ReportPart.Scale(p.number("cutoff_hz"), nyquist, "Excerpt cutoff")
                parts += ReportPart.Copy("Cliff ${measure(p.number("cliff_db"), "dB")} · ${when(p["eligible"]) { true -> "Eligible"; false -> "Excluded from vote"; else -> "Eligibility unavailable" }} · ${when(p["wall_observed"]) { true -> "Wall observed"; false -> "No wall observed"; else -> "Wall observation unavailable" }}")
                parts += ReportPart.Disclosure("probe-$index", "Excerpt measurements", parts = listOf(ReportPart.Readings(listOf("Peak" to measure(p.number("peak_dbfs"), "dBFS"), "High band" to measure(p.number("high_band_relative_db"), "dB relative"), "Above cutoff" to measure(p.number("above_cutoff_relative_db"), "dB relative"), "Occupied band" to measure(p.number("occupied_band_fraction"), "fraction", 4)))))
            }
        } else parts += ReportPart.Copy("Spectral excerpts unavailable for this report component.")
        ruleDetail("R18", "spectral")?.let { parts += it }
        return Investigation("spectral", "Spectral investigation", "Bandwidth, spectral walls & high-frequency evidence", parts)
    }
    private fun dynamics(): Investigation {
        if (native.isEmpty() && statistics.isEmpty()) return Investigation("dynamics", "Dynamics laboratory", "DR, loudness, peaks & channel differences",
            listOf(ReportPart.Copy("Native dynamics and tool statistics are missing or use unsupported versions. Original fields remain accessible.")))
        val parts = mutableListOf<ReportPart>(ReportPart.Metrics(listOf((dr.text("overall_integer_label") ?: "Unavailable") to "Stored DR label", measure(numeric(dr.obj("overall")), "dB", 3) to "Overall · drmeter")))
        parts += paired({ display(numeric(channelAt(dr.items("channels"), it).obj("numeric_dr")), 3) }, "Channel DR · dB", { c -> listOf("Crest factor" to measure(c.number("crest_factor_db"), "dB", 3), "RMS amplitude" to measure(c.number("rms"), "linear", 6), "Sample peak" to measure(c.number("peak"), "linear", 6), "Estimated peak" to channelAt(peaks, c).number("estimated_peak_dbtp").let { if (it == null) "Unavailable" else "${signed(it)} dBTP" }) })
        parts += ReportPart.Heading("Programme loudness")
        parts += ReportPart.Readings(listOf("Integrated" to measure(loudness.number("integrated_lufs"), "LUFS", 3), "Momentary maximum" to measure(loudness.number("momentary_max_lufs"), "LUFS", 3), "Short-term maximum" to measure(loudness.number("short_term_max_lufs"), "LUFS", 3), "Loudness range" to measure(loudness.obj("range").number("range_lu"), "LU"), "Histogram bounds" to "${display(loudness.obj("range").number("range_lower_lu"))}–${display(loudness.obj("range").number("range_upper_lu"))} LU"))
        parts += ReportPart.Disclosure("dynamics-method", "How these measurements differ", parts = listOf(
            ReportPart.Copy("DR compares selected loud blocks with peaks using three-second RMS/peak histograms. Crest compares sample peak with whole-interval RMS. Neither is a sound-quality grade; linear amplitude 1 is full scale."),
            ReportPart.Copy("Integrated loudness uses gated K-weighted channel powers. Momentary (400 ms) and short-term (3 s) maxima are sampled every 100 ms, not continuous maxima. LRA describes gated three-second level spread; its bounds concern histogram quantization, not perceptual uncertainty."),
            ReportPart.Copy("Complete loudness windows: ${scoped(loudness.obj("interval"))}. Trailing interval outside complete windows: ${measure(loudness.number("trailing_frames")?.let { n -> rate?.takeIf { it > 0 }?.let { n / it * 1000 } }, "ms", 0)}. No artificial silence is appended."),
            ReportPart.Copy("4× FIR peak estimates include zero-padded boundaries and the filter tail; ringing can affect the result. Certified EBU/true-peak meter conformance is unverified. No loudness history is inferred from integrated statistics.")))
        return Investigation("dynamics", "Dynamics laboratory", "DR, loudness, peaks & channel differences", parts)
    }
    private fun codec(): Investigation {
        val parts = mutableListOf<ReportPart>()
        for ((id, key) in listOf("R29" to "aac", "R30" to "vorbis")) if (reference.isNotEmpty()) {
            parts += ReportPart.Metrics(listOf(display(f(key), 4) to ReportRules.featureNames.getValue(key)))
            parts += ReportPart.Copy("Search ${scope(key)}${if (key == "aac") " · sparse eligible probes, not every instant" else " · reference reconstruction"}.")
            ruleDetail(id, "codec")?.let { parts += it }
        }
        val m = native.obj("mqa")
        if (m.isNotEmpty()) {
            parts += ReportPart.Finding("MQA signalling", if (m.text("status") == "not_detected") "No sync candidates in the searched interval." else if ((m.number("sync_matches") ?: 0.0) > 0) "Sync candidates observed; not authentication." else "Signalling observation unavailable.")
            parts += ReportPart.Readings(listOf("Search from track start" to measure(m.number("scanned_frames")?.let { n -> rate?.takeIf { it > 0 }?.let { n / it } }, "seconds", 3), "Sync matches" to display(m.number("sync_matches"), 0), "Retained candidates" to ((m["candidates"] as? List<*>)?.size?.toString() ?: "Unavailable"), "Metadata" to when(m["metadata_evaluated"]) { true -> "Evaluated"; false -> "Not evaluated"; else -> "Unavailable" }))
            parts += ReportPart.Copy("Only the stated interval was searched. No MQA decode or unfolding is implied. No match here does not exclude signalling elsewhere.")
        }
        if (reference.isNotEmpty()) {
            parts += ReportPart.Heading("Sample-rate conversion"); parts += readings("resample", "resample_wall")
            parts += listOfNotNull(ruleDetail("R10", "codec"), ruleDetail("R12", "codec"))
            parts += ReportPart.Copy("The preceding-energy scalar alone is not a pre-echo finding: its cutoff/MP3 applicability gate must also be met. AAC uses recorded source vetoes; Vorbis has a separate score-floor rule. Untriggered rules do not exclude every previous encode or resample.")
        }
        if (parts.isEmpty()) parts += ReportPart.Copy("Codec evidence is missing or its report components use unsupported versions. Original fields remain accessible.")
        return Investigation("codec", "Codec fingerprints", "AAC, Vorbis, MQA & resampling evidence", parts)
    }
    private fun depth(): Investigation {
        if (native.isEmpty() && reference.isEmpty()) return Investigation("depth", "Bit-depth investigation", "Declared precision, sample activity & noise floor",
            listOf(ReportPart.Copy("Depth measurements are missing or their report components use unsupported versions. Original fields remain accessible.")))
        val parts = mutableListOf<ReportPart>()
        parts += paired({ display(it.number("exact_used_bits"), 0) }, "Bits exercised · ${measuredScope.lowercase()}", { c ->
            val fl = channelAt(floor, c); val key = "effective_bits_channel_${display(c.number("channel_index"), 0)}"
            listOf("Declared" to measure(technical.number("declared_precision_bits"), "bits", 0), "Reference · ${scope(key)}" to fv(key), "Floor · ${scoped(fl.obj("interval"))}" to measure(fl.takeIf { it.text("status") == "measured" }?.number("nonzero_rms_p015_dbfs"), "dBFS"), "Floor color · high minus low" to measure(fl.takeIf { it.text("color_status") == "measured" }?.number("high_minus_low_db"), "dB"))
        })
        parts += ReportPart.Copy("The native floor is a low-percentile nonzero block RMS observation, capped at the first 30 seconds. Music can mask recording noise; sample activity does not establish original recording depth.")
        if (reference.isNotEmpty()) {
            parts += readings("depth_floor", "depth_color")
            val masked = f("depth_floor")?.let { it > -86 } == true
            parts += ReportPart.Finding(if (masked) "Measured floor, masked inference" else "Reference depth interpretation", reference.items("depth_candidates").mapNotNull { it.text("display") }.distinct().joinToString(" ").ifBlank { "Depth interpretation unavailable." })
            if (masked) parts += ReportPart.Copy("The measured reference floor is above −86 dBFS, where this method treats depth inference as masked. A measurement exists; its suitability for this inference is limited.")
            ruleDetail("R33", "depth")?.let { parts += it }
        }
        return Investigation("depth", "Bit-depth investigation", "Declared precision, sample activity & noise floor", parts)
    }
    private fun evidence(): Investigation {
        val parts = mutableListOf<ReportPart>()
        if (reference.isEmpty()) parts += ReportPart.Copy("Rule translations require a supported reference component. Original technical data remains accessible.")
        else {
            parts += ReportPart.Heading("How the stored score was reached")
            for (id in listOf("R04", "R07", "R08", "R09", "R23", "R31")) {
                val r = rule(id)
                if (r.isNotEmpty()) parts += ReportPart.Finding(ReportRules.definitions.getValue(id).first, if (id == "R09") "Stored net ${display(r.obj("after").number("net"), 0)} → initial heuristic ${display(r.obj("after").number("heuristic"), 0)}. Lossy minus natural is floored at zero." else effect(r))
            }
            parts += ReportPart.Copy("Lossy/natural are point totals, not detector counts. The initial heuristic and later main-score adjustments are separate steps. A final zero is not proof of source authenticity.")
            parts += ReportPart.Heading("Source hypotheses")
            reference.items("source_candidates").forEach { c ->
                val id = c.text("id").orEmpty()
                parts += ReportPart.Disclosure("candidate-$id", ReportRules.candidates[id] ?: "Source candidate", candidateState(c), listOf(ReportPart.Copy(c.text("display") ?: "Inspect the contributing criteria.")) + (c["rule_ids"] as? List<*>)?.filterIsInstance<String>().orEmpty().mapNotNull { ruleDetail(it, "candidate-$id") })
            }
            for ((status, label) in ReportRules.states) {
                val group = rules.filter { it.text("state") == status }
                if (group.isNotEmpty()) parts += ReportPart.Disclosure("rules-$status", label, "${group.size} recorded steps", group.mapNotNull { ruleDetail(it.text("id").orEmpty(), "rule") })
            }
            parts += ReportPart.Copy("Excluded means outside the reference contract, not a negative measurement. Unavailable means required evidence is missing; vetoed means another recorded rule suppressed it. Unflagged candidates do not exclude source history.")
        }
        return Investigation("evidence", "Evidence explorer", "Measurements → criteria → outcomes → effects", parts)
    }
}
