# Contributing to java-clean-code

Thank you for your interest! Two kinds of contributions are common:

- **Skill content** — rule edits, new references, new pressure scenarios.
- **Examples** — new before/after Java pairs in `examples/`.

Issues, PRs, and discussions are all welcome.

---

## TDD Iron Law (skill content only)

Per `superpowers:writing-skills`, **no edit to `SKILL.md` or `references/*.md` without a failing test first.** Documentation changes pretend to be safe; in practice, untested skill edits introduce regressions you only discover when Claude misbehaves on a real task.

For any change to `SKILL.md` or `references/*.md`:

1. Add a pressure scenario to `tests/scenarios.md` (the "what should Claude do here?" question).
2. Capture baseline behavior in `tests/red-baseline.md` — run a subagent **without** the skill, paste the verbatim output. This is the RED state.
3. Edit the skill (`SKILL.md` or the relevant reference) to address what RED revealed.
4. Verify GREEN compliance in `tests/green-verification.md` — same scenario, with the skill loaded; the previously-broken behavior is now fixed.

Rule edits without a corresponding RED → GREEN diff in the tests files will be requested-changes.

---

## Adding a new before/after example pair

Each pair illustrates one rule (or one tight cluster) so a reader sees the violation and the fix side by side.

1. Pick a rule that's not yet illustrated. The README's [Examples gallery](./README.md#examples-gallery) lists what we have today.
2. Create `examples/before/<IntentionRevealingName>.java` — one focused violation per file. Use `package examples.before;` and real Spring Boot 3 / Java 17 imports where applicable.
3. Create the matching `examples/after/<CleanName>.java` — or multiple files if SRP requires the split (e.g. `OrderProcessingService` + `NotificationPolicy`). Use `package examples.after;`.
4. **Plain-Java pairs MUST NOT import `org.springframework.*`.** The skill's polyglot positioning depends on plain-Java examples staying framework-free. Verify with:
   ```bash
   grep -l 'org\.springframework' examples/before/<your-file>.java examples/after/<your-files>.java
   ```
   If it returns any path, the constraint is violated.
5. Update `README.md`'s Examples gallery to list your pair under the right context column (Spring Boot · Plain Java · Domain).
6. Submit one commit per pair: `examples: add <context> pair for <Rule name>`.

---

## Adding a new pressure scenario

Pressure scenarios are how we *prove* the skill teaches what it claims.

1. Add the scenario to `tests/scenarios.md` — describe the prompt, what counts as success, and what counts as a violation.
2. Capture the RED baseline in `tests/red-baseline.md` — dispatch a subagent **without** the skill, copy its output verbatim. Note which rules it violated and which rationalizations it used.
3. If the skill needs a new counter (e.g. a new row in the Rationalizations table) to address what RED revealed, edit `SKILL.md` or the relevant reference.
4. Verify GREEN in `tests/green-verification.md` — same scenario, skill loaded; record the improved behavior.

---

## Word and line budgets

These are enforced because `SKILL.md` is loaded into every invocation that triggers the skill — every word is a tax paid on every use.

- `.claude/skills/java-clean-code/SKILL.md` — **≤ 500 words total** (including YAML frontmatter).
- `.claude/skills/java-clean-code/references/*.md` — **< 300 lines each**.
- `examples/**/*.java` — typically **30–80 lines per file**; one violation per before-file.

Verify before pushing:

```bash
wc -w .claude/skills/java-clean-code/SKILL.md          # must be ≤ 500
wc -l .claude/skills/java-clean-code/references/*.md   # each must be < 300
```

---

## Bumping skill version

When rule content changes (not just typos), bump the version in two places:

- `.claude-plugin/plugin.json` — the `version` field.
- `.claude-plugin/marketplace.json` — both `metadata.version` and `plugins[0].version`.

Both files must agree. Semver guidance:

- **Patch** (`0.x.Y`) — typo fixes, reformatting, link updates.
- **Minor** (`0.X.0`) — new example pair, new reference file, broadened CSO trigger.
- **Major** (`X.0.0`) — a rule changes meaning, the description trigger surface changes incompatibly, or the file structure under `.claude/skills/java-clean-code/` reorganizes.

---

## Code of Conduct

- Be respectful and constructive — both in PR review comments and in the rationale you put in commits.
- Test before submitting. RED → GREEN is non-negotiable for skill content.
- Cite rules by **name** (Demeter, SRP, DIP, CQS) — never bare numbers like "R10". The skill itself follows this convention; contributions should too.

---

## PR process

1. Fork the repository and create a branch:
   ```bash
   git checkout -b feature/<short-descriptive-name>
   ```
2. Make focused commits — one logical change per commit. A new example pair is one commit; a new pressure scenario plus its skill edit is two commits (RED capture, then GREEN fix).
3. Run the relevant `wc -w` / `wc -l` budget checks before pushing.
4. Open a PR with:
   - **What changed** — one-line summary.
   - **Which rule(s)** it affects.
   - **GREEN verification** — paste the relevant section from `tests/green-verification.md` if your change touches skill behavior.

---

## License

By contributing, you agree that your contributions will be licensed under the MIT License. Examples and skill content are provided "as is" without warranty.
