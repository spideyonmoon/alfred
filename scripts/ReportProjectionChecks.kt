package dev.alfred.forensics

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.io.File

/** Host-only presentation tests; no DSP, no invented audio specimens in the app. */
private fun partsText(parts: List<ReportPart>): String = parts.joinToString("\n") { p -> when(p) {
    is ReportPart.Copy -> p.text
    is ReportPart.Heading -> p.text
    is ReportPart.Metrics -> p.values.toString()
    is ReportPart.Readings -> p.values.toString()
    is ReportPart.Finding -> p.title + " " + p.text
    is ReportPart.Scale -> "${p.label}: ${p.value} / ${p.limit}"
    is ReportPart.Channels -> p.cards.toString()
    is ReportPart.Disclosure -> p.title + " " + p.subtitle + " " + partsText(p.parts)
} }
private fun allParts(parts: List<ReportPart>): List<ReportPart> = parts.flatMap { listOf(it) + if (it is ReportPart.Disclosure) allParts(it.parts) else emptyList() }
private fun ReportView.allText() = title + identity + summary + overview.let(::partsText) + sections.joinToString { partsText(it.parts) }
@Suppress("UNCHECKED_CAST") private fun Fields.mutable() = this as MutableMap<String, Any?>

fun main(args: Array<String>) {
    val bytes = File(args.single()).readBytes()
    fun source(): Fields = (JsonSlurper().parseText(bytes.toString(Charsets.UTF_8)) as List<*>).first().fields()
    val product = source(); val before = JsonOutput.toJson(product)
    val view = ReportProjection(product).project(); val text = view.allText()
    check(view.title == "Money, Money, Money")
    check(view.identity == "ABBA · Gold (Greatest Hits)")
    check(view.summary == "No strong lossy indicators")
    check(view.score == "0" && view.ancestry == "INCONCLUSIVE" && view.points == "1 lossy / 5 natural points")
    for (value in listOf("DR8", "−10.63", "8.54", "+0.319", "+0.025", "0.0153", "0.0000", "−47.76", "−44.15", "21.749", "8.475")) check(value in text) { value }
    check(view.sections.map { it.id } == listOf("spectral", "dynamics", "codec", "depth", "evidence"))
    val parts = allParts(view.overview + view.sections.flatMap { it.parts })
    val ids = parts.filterIsInstance<ReportPart.Disclosure>().map { it.id }
    check(ids.size == ids.toSet().size)
    check((1..34).all { "rule-R${it.toString().padStart(2,'0')}" in ids })
    check(parts.filterIsInstance<ReportPart.Scale>().count { it.label == "Excerpt cutoff" } == 12)
    check("0 wall votes / 10 eligible" in text)
    check("Main score: 0 → −30" in text && "Main score: −30 → 0" in text)
    check("Excluded by reference contract" in text && "Measured floor, masked inference" in text)
    check("0:00.000–3:00.000" in text && "0:00.000–0:30.000" in text)
    check("132300" !in text && "8237307" !in text && "cutoff <" !in text)
    check(JsonOutput.toJson(product) == before) { "Projection mutated input" }

    val missing = source(); missing.obj("reference_assessment").obj("features").obj("aac").mutable()["value"] = null
    val missingView = ReportProjection(missing).project()
    check("0.0153" !in missingView.allText() && "Unavailable" in missingView.allText())
    val future = source(); future.obj("reference_assessment").mutable()["method_id"] = "future"
    val partial = ReportProjection(future).project()
    check(partial.summary == "Reference interpretation unavailable" && "−10.632" in partial.allText() && partial.score == null)
    val gates = listOf("measurement_report" to "schema_version", "metadata" to "metadata_version", "tool_statistics" to "statistics_version", "reference_inputs" to "version")
    for ((component, version) in gates) {
        val unknown = source(); unknown.obj(component).mutable()[version] = "future"
        val result = ReportProjection(unknown).project()
        check("NaN" !in result.allText() && "Infinity" !in result.allText())
        if (component == "tool_statistics") check("DR8" !in result.allText())
        if (component == "metadata") check(result.identity.isEmpty())
        if (component == "reference_inputs") check(allParts(result.sections.flatMap { it.parts }).filterIsInstance<ReportPart.Scale>().none { it.label == "Excerpt cutoff" })
    }
    val unknownFeature = source(); unknownFeature.obj("reference_assessment").obj("features").obj("aac").mutable()["version"] = 99
    check("0.0153" !in ReportProjection(unknownFeature).project().allText())
    val positive = source(); positive.obj("reference_assessment").items("source_candidates").first { it.text("id") == "resampling" }.mutable()["matched"] = true
    check(ReportProjection(positive).project().sections.first().id == "codec")
    val mqa = source(); mqa.obj("measurement_report").obj("mqa").mutable()["sync_matches"] = 1
    check(ReportProjection(mqa).project().sections.first().id == "codec")
    val depth = source(); depth.obj("measurement_report").items("channels").first().mutable()["exact_used_bits"] = 12
    check(ReportProjection(depth).project().sections.first().id == "depth")
    val veto = source(); val rules = veto.obj("reference_assessment").items("rules")
    rules.first { it.text("id") == "R29" }.mutable().putAll(mapOf("state" to "vetoed", "causal_veto_rule" to "R16"))
    rules.first { it.text("id") == "R30" }.mutable()["state"] = "unavailable"
    val vetoText = ReportProjection(veto).project().allText()
    check("Suppressed by Cassette candidate effect" in vetoText && "Required evidence unavailable" in vetoText)
    val failed = source(); failed.obj("measurement_report").mutable()["status"] = "failed"
    check(ReportProjection(failed).project().sections.isEmpty())
    val unknown = source(); unknown.mutable()["product_schema_version"] = "future"
    check(ReportProjection(unknown).project().summary == "Unsupported report version")
    check(display(null) == "Unavailable" && display(0.0,4) == "0.0000" && display(0.00000001) != "0.00")
    println("PASS: actual ABBA values/scopes, 34 rules, 12 probes, score trail, immutable input, null/zero, component/feature versions, adaptive ordering, vetoes, failed/unknown products.")
}
