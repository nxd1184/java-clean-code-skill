# java-clean-code — a Claude Code skill

Clean code principles for Java projects, packaged as an installable Claude Code skill. Covers naming, functions, SOLID, TDD, concurrency, clean architecture, and craftsmanship. Examples target Spring Boot 3.x on Java 17 LTS, with Java 21 idioms called out as optional.

## What this gives you

When you invoke this skill, Claude applies a top-20 rule checklist to whatever Java code it's writing or reviewing, and opens topic-specific reference files for depth when needed.

- 20 high-ROI rules, each with a quick "how to check"
- 12 topic references (naming, functions, SOLID, testing, architecture, concurrency, …)
- Before/after examples in `examples/`

## Install

### Option 1 — Claude Code plugin marketplace

```
/plugin marketplace add nxd1184/java-clean-code-skill
/plugin install java-clean-code@java-clean-code-skill
```

### Option 2 — Local symlink

```bash
git clone https://github.com/nxd1184/java-clean-code-skill.git
cd java-clean-code-skill
```

**macOS / Linux:**

```bash
./install.sh       # install
./uninstall.sh     # uninstall
```

**Windows (PowerShell):**

```powershell
.\install.ps1      # install
.\uninstall.ps1    # uninstall
```

> **Windows note:** symlink creation requires **Developer Mode** (Settings → Privacy & security → For developers) *or* an Administrator PowerShell session. You may also need to allow the scripts once: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`.

Both variants symlink `.claude/skills/java-clean-code` into `~/.claude/skills/` (or `%USERPROFILE%\.claude\skills\` on Windows). Edits to the repo take effect in the next Claude Code session — no reinstall needed.

### Option 3 — Upload to Claude Desktop / claude.ai

Package the skill folder as a zip, then upload via Claude's skill upload UI.

**macOS / Linux:**

```bash
./package.sh
# writes dist/java-clean-code.zip
```

**Windows (PowerShell):**

```powershell
.\package.ps1
# writes dist\java-clean-code.zip
```

The zip contains only `SKILL.md` + `references/` (13 files, ~70 KB) — nothing from the repo's dev tooling, examples, or `.git/`.

## How to use

From any Claude Code session, say:
- "Using the `java-clean-code` skill, implement …"
- "Review this Java file with the `java-clean-code` skill."
- "What does SRP mean for this class? Use the skill."

**Prompt cheatsheet:** See [`PROMPTS.md`](./PROMPTS.md) for worked examples of invoking the skill for writing, reviewing, refactoring, and principle questions — plus the anti-prompts that produce slow/generic responses.

## Repo layout

See `CLAUDE.md` for contributor conventions. Key paths:
- `.claude/skills/java-clean-code/SKILL.md` — top-20 rule checklist
- `.claude/skills/java-clean-code/references/*.md` — topic deep dives
- `examples/before/` and `examples/after/` — paired Spring Boot snippets
- `tests/scenarios.md` — TDD pressure scenarios for the skill itself

## License

MIT — see [LICENSE](LICENSE).
