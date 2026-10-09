"""Prepare a clean pinned validation checkout; never vendor engine/fixture source."""
import json
from pathlib import Path
import subprocess

root = Path(__file__).resolve().parents[1]
pin = json.loads((root / "core-dependency.json").read_text())
checkout = root / ".core"
def git(*args):
    return subprocess.check_output(["git", *args], text=True).strip()
created = not checkout.exists()
if created:
    git("clone", "--filter=blob:none", "--no-checkout", pin["repository"], str(checkout))
if not created and git("-C", str(checkout), "status", "--porcelain", "--untracked-files=no"):
    raise SystemExit("Pinned core checkout has tracked changes; preserve and resolve them before building")
git("-C", str(checkout), "checkout", "--detach", pin["revision"])
assert git("-C", str(checkout), "rev-parse", "HEAD") == pin["revision"]
manifest = (root / "native/adapter/Cargo.toml").read_text()
assert f'rev = "{pin["revision"]}"' in manifest
assert f'git = "{pin["repository"]}"' in manifest
print("Pinned core checkout:", pin["revision"])
