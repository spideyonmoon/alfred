"""Generated-only saved Forensics UI, using the actual Compose navigation."""
import json
import re
import subprocess
import time
import xml.etree.ElementTree as ET


def adb(*args):
    return subprocess.check_output(["adb", *args], text=True, stderr=subprocess.STDOUT)


def run(output, receipt):
    package = "dev.alfred.workspace.debug"
    adb("shell", "am", "force-stop", package)
    adb("shell", "am", "start", "-W", "-n", package + "/dev.alfred.workspace.MainActivity")

    def nodes():
        adb("shell", "uiautomator", "dump", "/sdcard/alfred-feature.xml")
        xml = adb("shell", "cat", "/sdcard/alfred-feature.xml")
        (output / "feature-current.xml").write_text(xml)
        return list(ET.fromstring(xml).iter("node"))

    def swipe(up):
        current = nodes()
        _, _, width, height = map(int, re.findall(r"\d+", current[0].attrib["bounds"]))
        panels = [n for n in current if n.attrib.get("scrollable") == "true" and
                  int(re.findall(r"\d+", n.attrib["bounds"])[1]) >= height // 2]
        if panels:
            left, top, right, bottom = map(int, re.findall(r"\d+", panels[0].attrib["bounds"]))
            distance = max(24, (bottom - top) // 3)
            a, b = (bottom - 16, bottom - 16 - distance) if up else (top + 16, top + 16 + distance)
            adb("shell", "input", "swipe", str((left + right) // 2), str(a), str(b), "250")
            return
        a, b = (height * 4 // 5, height // 3) if up else (height // 3, height * 4 // 5)
        adb("shell", "input", "swipe", str(width // 2), str(a), str(width // 2), str(b), "250")

    def find(text, scroll=True, up=True, actionable=False):
        for _ in range(20):
            current = nodes()
            parents = {child: parent for parent in current for child in parent}
            height = int(re.findall(r"\d+", current[0].attrib["bounds"])[-1])
            for node in current:
                if text not in node.attrib.get("text", "") and text not in node.attrib.get("content-desc", ""):
                    continue
                target = node
                if actionable:
                    while target is not None and target.attrib.get("clickable") != "true":
                        target = parents.get(target)
                    if target is None or target.attrib.get("enabled") != "true":
                        continue
                left, top, right, bottom = map(int, re.findall(r"\d+", target.attrib["bounds"]))
                if right > left and bottom - top >= (44 if actionable else 18) and bottom <= height - 8:
                    return target
            if scroll:
                swipe(up)
            time.sleep(.5)
        raise AssertionError("Feature UI missing: " + text)

    def click(text, up=True):
        node = find(text, up=up, actionable=True)
        left, top, right, bottom = map(int, re.findall(r"\d+", node.attrib["bounds"]))
        adb("shell", "input", "tap", str((left + right) // 2), str((top + bottom) // 2))
        time.sleep(.5)

    click("Forensics history")
    click("Open completed · " + receipt["actual_attempt"])
    find("Native ancestry:")
    for section, evidence in (
        ("Spectral investigation", "Native 95th-percentile cutoff"),
        ("Dynamics laboratory", "Programme loudness"),
        ("Codec fingerprints", "Sample-rate conversion"),
        ("Bit-depth investigation", "Bits exercised"),
        ("Evidence explorer", "How the stored score was reached"),
    ):
        click(section)
        find(evidence)
        click(section, up=False)
    click("All saved reports", up=False)
    click("Open completed · " + receipt["generated_attempt"])
    find("Analysis failed")
    click("Technical data & original report")
    click("Inspect exact original fields")
    click("unknown_future_integer:")
    find("18446744073709551615")
    # Navigation back up to the object precedes choosing a sibling field.
    click("Up one field", up=False)
    click("unknown_null:")
    find("null · unavailable (not zero)")
    (output / "feature-ui-smoke.json").write_text(json.dumps({"passed": True, "checks": 10,
        "all_five_investigations": True, "history_after_restart": True,
        "api": int(adb("shell", "getprop", "ro.build.version.sdk"))}))
    # Planned tools are exercised by saf-ui-smoke.py in the workspace panel.
    # History is intentionally a report-only destination.
