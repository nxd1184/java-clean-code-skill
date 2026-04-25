# CLAUDE.md — contributor notes for java-clean-code-skill

This repo is an installable Claude Code skill that applies clean-code principles
to Java projects. It is **not** a Java project itself — all artifacts are
markdown + JSON.

## Repo conventions

- Skill content lives under `.claude/skills/java-clean-code/`.
- `SKILL.md` must stay under 500 words (frequently loaded on invocation).
- `references/*.md` target 150–300 lines each; split if larger.
- Examples under `examples/` are Spring Boot 3.x + Java 17 LTS baseline.
- Java 21 idioms (virtual threads, pattern matching) are optional notes, never required.

## TDD discipline for this skill

Per `superpowers:writing-skills`, no edits to `SKILL.md` or references without:
1. A pressure scenario in `tests/scenarios.md` (RED baseline).
2. Documented baseline behavior in `tests/red-baseline.md`.
3. Verified GREEN run in `tests/green-verification.md`.

## Quick verification

- `wc -w .claude/skills/java-clean-code/SKILL.md` — target under 500.
- `wc -l .claude/skills/java-clean-code/references/*.md` — target each under 300.
