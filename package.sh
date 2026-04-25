#!/usr/bin/env bash
# Package the java-clean-code skill into a zip ready for Claude upload.
# Output: dist/java-clean-code.zip with SKILL.md at the zip root.
set -euo pipefail

REPO_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SKILL_SRC="$REPO_DIR/.claude/skills/java-clean-code"
DIST_DIR="$REPO_DIR/dist"
ZIP_PATH="$DIST_DIR/java-clean-code.zip"

if [ ! -d "$SKILL_SRC" ]; then
    echo "Error: skill folder not found at $SKILL_SRC" >&2
    exit 1
fi

if ! command -v zip >/dev/null 2>&1; then
    echo "Error: 'zip' is not installed. Install it and retry." >&2
    exit 1
fi

mkdir -p "$DIST_DIR"
rm -f "$ZIP_PATH"

# Zip from *inside* the skill folder so SKILL.md sits at the zip root.
(cd "$SKILL_SRC" && zip -rq "$ZIP_PATH" . -x "*.DS_Store" "*/.DS_Store")

echo "Packaged: $ZIP_PATH"
echo ""
echo "Contents:"
unzip -l "$ZIP_PATH" | awk 'NR>3 && !/^---/ && !/ files?$/ {print}'
