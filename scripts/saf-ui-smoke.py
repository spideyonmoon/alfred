"""Exercise real DocumentsUI read-grant offers against debug generated inputs."""
import re
import time
import xml.etree.ElementTree as ET
from pathlib import Path
import subprocess
import os
import zipfile


def adb(*args):
    return subprocess.check_output(["adb", *args], text=True, stderr=subprocess.STDOUT)


def run(output: Path):
    api = int(adb("shell", "getprop", "ro.build.version.sdk").strip())
    finger_jar = None
    if api == 30:
        sdk = Path(os.environ["ANDROID_HOME"])
        platform = sdk / "platforms/android-36/android.jar"
        tools = sdk / "build-tools/36.0.0"
        sources = Path(__file__).with_name("saf-finger-tap")
        build = output / "finger-helper"
        classes = build / "classes"
        dex = build / "dex"
        classes.mkdir(parents=True, exist_ok=True)
        dex.mkdir(exist_ok=True)
        subprocess.run(["javac", "--release", "8", "-cp", str(platform), "-d", str(classes),
                        *map(str, sources.glob("*.java"))], check=True)
        stubs = build / "compile-only.jar"
        with zipfile.ZipFile(stubs, "w") as jar:
            for file in (classes / "com").rglob("*.class"):
                jar.write(file, file.relative_to(classes).as_posix())
        subprocess.run([str(tools / "d8"), "--min-api", "30", "--lib", str(platform), "--classpath", str(stubs),
                        "--output", str(dex), str(classes / "dev/alfred/test/SafFingerTap.class")], check=True)
        finger_jar = "/data/local/tmp/alfred-finger.jar"
        packaged = build / "finger.jar"
        with zipfile.ZipFile(packaged, "w") as jar:
            jar.write(dex / "classes.dex", "classes.dex")
        adb("push", str(packaged), finger_jar)
    def nodes():
        adb("shell", "uiautomator", "dump", "/sdcard/alfred-saf.xml")
        xml = adb("shell", "cat", "/sdcard/alfred-saf.xml")
        (output / "saf-current.xml").write_text(xml)
        return list(ET.fromstring(xml).iter("node"))

    def match(label, timeout=30):
        deadline = time.monotonic() + timeout
        while time.monotonic() < deadline:
            for node in nodes():
                if node.attrib.get("text", "").casefold() == label.casefold() or node.attrib.get("content-desc", "").casefold() == label.casefold():
                    return node
            time.sleep(1)
        raise AssertionError(f"SAF UI label missing: {label}")

    def tap(node, long=False):
        left, top, right, bottom = map(int, re.findall(r"\d+", node.attrib["bounds"]))
        x, y = str((left + right) // 2), str((top + bottom) // 2)
        if finger_jar and node.attrib.get("resource-id") == "android:id/title" and node.attrib.get("text") in {"same.flac", "same.wav"}:
            result = adb("shell", "uiautomator", "runtest", finger_jar, "-c", "dev.alfred.test.SafFingerTap#testTap",
                         "-e", "x", x, "-e", "y", y, "-e", "long", str(long).lower())
            assert "OK (1 test)" in result, result
            return
        if long:
            adb("shell", "input", "swipe", x, y, x, y, "1000")
        else:
            adb("shell", "input", "tap", x, y)

    def click(label):
        tap(match(label))

    def panel_match(label):
        # Only the lower actions panel scrolls; a full-screen swipe can move
        # the track table instead. Reacquire bounds after each bounded swipe.
        for _ in range(8):
            current = nodes()
            found = next((n for n in current if n.attrib.get("text") == label), None)
            if found is not None:
                return found
            _, _, width, height = map(int, re.findall(r"\d+", current[0].attrib["bounds"]))
            adb("shell", "input", "swipe", str(width // 2), str(height * 9 // 10), str(width // 2), str(height * 2 // 3), "250")
        raise AssertionError("Workspace panel label missing: " + label)

    def top():
        current = nodes()
        _, _, width, height = map(int, re.findall(r"\d+", current[0].attrib["bounds"]))
        for _ in range(3):
            adb("shell", "input", "swipe", str(width // 2), str(height // 3), str(width // 2), str(height * 4 // 5), "250")

    def root():
        def ready():
            if api == 30:
                # Android 11's grid title area did not activate the document in
                # saved UI evidence. Use its observed List view affordance and
                # wait for the directory layout before reacquiring row bounds.
                view = next((n for n in nodes() if n.attrib.get("content-desc") == "List view"), None)
                if view is not None:
                    tap(view)
                time.sleep(1)
                match("same.flac")
        # Roots update asynchronously on a freshly booted emulator. Verify the
        # destination, and reacquire coordinates if the drawer reordered while
        # the automation was tapping a row.
        for attempt in range(4):
            current = nodes()
            if any(n.attrib.get("text", "") == "same.flac" for n in current):
                ready()
                return
            drawer = next((n for n in current if n.attrib.get("content-desc", "") in {"Show roots", "Open navigation drawer"}), None)
            if drawer is not None:
                tap(drawer)
            time.sleep(2)  # Let roots discovery finish before using row bounds.
            provider = None
            for _ in range(4):
                current = nodes()
                roots = next((n for n in current if n.attrib.get("resource-id", "").endswith("/roots_list")), None)
                if roots is None:
                    break
                provider = next((n for n in roots.iter("node") if n.attrib.get("text") == "Alfred generated inputs"), None)
                if provider is not None:
                    break
                left, top_y, right, bottom = map(int, re.findall(r"\d+", roots.attrib["bounds"]))
                x = str((left + right) // 2)
                adb("shell", "input", "swipe", x, str(bottom - 20), x, str(top_y + 20), "250")
            if provider is None:
                continue
            (output / f"saf-roots-{attempt}.xml").write_bytes((output / "saf-current.xml").read_bytes())
            tap(provider)
            try:
                match("same.flac", timeout=10)
                ready()
                return
            except AssertionError:
                pass
        raise AssertionError("Generated provider root did not open")

    def checked(count):
        deadline = time.monotonic() + 45
        complete = False
        selected = False
        while time.monotonic() < deadline:
            current = nodes()
            texts = [n.attrib.get("text", "") for n in current]
            selected = selected or any(f"of {count} selected documents" in text for text in texts)
            if selected and any("Input checks complete" in text for text in texts):
                complete = True
            if complete and any("persisted" in text for text in texts):
                return
            if selected:
                # Notifications/other status text can push the document row off
                # screen; verify its actual label after a bounded scroll.
                bounds = current[0].attrib["bounds"]
                _, _, width, height = map(int, re.findall(r"\d+", bounds))
                adb("shell", "input", "swipe", str(width // 2), str(height * 4 // 5), str(width // 2), str(height // 3), "250")
            time.sleep(1)
        raise AssertionError(f"SAF acquisition did not finish for {count} inputs")

    adb("shell", "am", "force-stop", "dev.alfred.workspace.debug")
    adb("shell", "am", "start", "-W", "-n", "dev.alfred.workspace.debug/dev.alfred.workspace.MainActivity")
    click("Choose one document")
    root()
    click("same.flac")
    checked(1)
    (output / "saf-single.xml").write_bytes((output / "saf-current.xml").read_bytes())
    click("Home")
    click("Choose documents")
    root()
    tap(match("same.flac"), long=True)
    click("same.wav")
    # DocumentsUI exposes the confirmation affordance as text or description.
    click("Select")
    checked(2)
    (output / "saf-multiple.xml").write_bytes((output / "saf-current.xml").read_bytes())
    click("Home")
    click("Choose folder")
    root()
    click("Use this folder")
    click("Allow")
    checked(3)
    (output / "saf-folder.xml").write_bytes((output / "saf-current.xml").read_bytes())
    adb("shell", "screencap", "-p", "/sdcard/alfred-workspace.png")
    adb("pull", "/sdcard/alfred-workspace.png", str(output / "workspace-display.png"))
    # Exercise the owner scratchpad's independent metadata route with the
    # generated selection; this must not submit a forensic/DSP operation.
    click("Metadata")
    match("Metadata studio")
    tap(panel_match("Inspect metadata · same.flac"))
    panel_match("All report fields")
    (output / "workspace-metadata.xml").write_bytes((output / "saf-current.xml").read_bytes())
    adb("shell", "screencap", "-p", "/sdcard/alfred-metadata.png")
    adb("pull", "/sdcard/alfred-metadata.png", str(output / "metadata-display.png"))
    click("Forensic")
    current = nodes()
    assert any(n.attrib.get("content-desc") == "Track table height" for n in current), "Resizable table control missing"
    # Only one chosen row can enter Spectrogram from a multi-file workspace.
    tap(match("Select all tracks"))
    match("0 of 3 selected documents")
    tap(match("Select same.flac"))
    match("1 of 3 selected documents")
    # The capability notice is rendered below the action button; verify the
    # actual button's enabled ancestor rather than accepting its text alone.
    def enabled_action(label):
        for node in nodes():
            if node.attrib.get("clickable") == "true" and node.attrib.get("enabled") == "true" and any(
                child.attrib.get("text") == label for child in node.iter("node")
            ):
                return True
        return False
    panel_match("Spectrogram")
    assert enabled_action("Spectrogram"), "Single-row Spectrogram action unavailable"
    import json
    (output / "workspace-ui-smoke.json").write_text(json.dumps({"passed": True, "api": api,
        "metadata_without_DSP": True, "select_all_and_subset": True, "single_row_spectrogram": True,
        "resize_control_visible": True}))


if __name__ == "__main__":
    run(Path("emulator-evidence"))
