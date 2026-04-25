#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SKILL_SRC="$REPO_DIR/.claude/skills/java-clean-code"
SKILL_DEST="$HOME/.claude/skills/java-clean-code"

mkdir -p "$HOME/.claude/skills"

if [ -e "$SKILL_DEST" ] && [ ! -L "$SKILL_DEST" ]; then
    echo "Error: $SKILL_DEST exists and is not a symlink. Remove it first." >&2
    exit 1
fi

ln -sfn "$SKILL_SRC" "$SKILL_DEST"
echo "Installed: $SKILL_DEST -> $SKILL_SRC"
echo "Invoke in Claude Code via: /skills java-clean-code (or implicit load on explicit request)."
