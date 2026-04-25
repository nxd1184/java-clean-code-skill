#!/usr/bin/env bash
set -euo pipefail
SKILL_DEST="$HOME/.claude/skills/java-clean-code"

if [ -L "$SKILL_DEST" ]; then
    rm "$SKILL_DEST"
    echo "Removed: $SKILL_DEST"
elif [ -e "$SKILL_DEST" ]; then
    echo "Error: $SKILL_DEST exists but is not a symlink. Remove manually." >&2
    exit 1
else
    echo "Nothing to remove at $SKILL_DEST"
fi
