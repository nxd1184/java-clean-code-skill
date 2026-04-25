# Java Clean Code Skill Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build an installable Claude Code skill (`java-clean-code`) that applies clean-code principles to Java code — writing, reviewing, and refactoring — backed by a top-20 rule checklist and 12 topic-specific reference files.

**Architecture:** Claude Code plugin layout (`.claude-plugin/` + `.claude/skills/java-clean-code/`) matching the reference repo `nextlevelbuilder/ui-ux-pro-max-skill`. Lean SKILL.md (<500 words) with a references/ folder for depth. Built TDD-style per `superpowers:writing-skills`: pressure scenarios written first, baseline captured without skill, skill authored to address observed failures, then refactor loop to close rationalizations.

**Tech Stack:** Markdown (skill content), JSON (plugin manifest), bash (install script). Java 17 LTS + Spring Boot 3.x for examples, with Java 21 idioms called out as optional notes.

**Working directory:** `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill`

---

## File Structure

Repo files (all relative to working directory):

| Path | Purpose |
|---|---|
| `.gitignore` | Ignore OS/editor artifacts |
| `LICENSE` | MIT license |
| `README.md` | What it is, install, how to invoke, contribute |
| `CLAUDE.md` | Contributor notes for this repo |
| `install.sh` | Fallback manual install (symlink) |
| `.claude-plugin/plugin.json` | Plugin manifest |
| `.claude-plugin/marketplace.json` | Marketplace install metadata |
| `.claude/skills/java-clean-code/SKILL.md` | Core skill file (top-20 checklist + workflows) |
| `.claude/skills/java-clean-code/references/naming.md` | Meaningful names |
| `.claude/skills/java-clean-code/references/comments.md` | Comments that earn their keep |
| `.claude/skills/java-clean-code/references/formatting.md` | Formatting & file layout |
| `.claude/skills/java-clean-code/references/functions.md` | Small functions, args, CQS, side effects |
| `.claude/skills/java-clean-code/references/classes-and-boundaries.md` | Classes, boundaries, third-party |
| `.claude/skills/java-clean-code/references/objects-and-data.md` | Objects vs data, Law of Demeter |
| `.claude/skills/java-clean-code/references/testing.md` | TDD, FIRST, JUnit 5 |
| `.claude/skills/java-clean-code/references/solid.md` | SRP/OCP/LSP/ISP/DIP (Spring DI) |
| `.claude/skills/java-clean-code/references/architecture.md` | Clean architecture layers |
| `.claude/skills/java-clean-code/references/concurrency.md` | Shared state, `java.util.concurrent`, virtual threads |
| `.claude/skills/java-clean-code/references/smells.md` | Cross-reference lookup |
| `.claude/skills/java-clean-code/references/craftsmanship.md` | Professional discipline |
| `tests/scenarios.md` | TDD pressure scenarios (RED) |
| `tests/red-baseline.md` | Captured baseline violations (generated) |
| `tests/green-verification.md` | Post-skill re-run results (generated) |
| `examples/before/FatUserController.java` | Violation example |
| `examples/after/UserController.java` | Cleaned-up example |
| `examples/before/SmellyOrderService.java` | Violation example |
| `examples/after/OrderService.java` | Cleaned-up example |

---

## Task 1: Scaffold repository layout and initialize git

**Files:**
- Create: directories for the full tree
- Create: `.gitignore`

- [ ] **Step 1: Create directory skeleton**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
mkdir -p .claude-plugin \
         .claude/skills/java-clean-code/references \
         examples/before examples/after \
         tests
```

Expected: no output, directories created.

- [ ] **Step 2: Write `.gitignore`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.gitignore`:
```
.DS_Store
*.swp
*.swo
.idea/
.vscode/
*.class
*.jar
target/
node_modules/
```

- [ ] **Step 3: Initialize git**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git init -b main
git add .gitignore
git commit -m "chore: initial scaffold with .gitignore"
```

Expected: `Initialized empty Git repository` and a commit on `main`.

---

## Task 2: Write LICENSE and CLAUDE.md

**Files:**
- Create: `LICENSE`
- Create: `CLAUDE.md`

- [ ] **Step 1: Write `LICENSE` (MIT)**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/LICENSE` with the standard MIT license text:
```
MIT License

Copyright (c) 2026 Steven Nguyen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

- [ ] **Step 2: Write `CLAUDE.md`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/CLAUDE.md`:
```markdown
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
```

- [ ] **Step 3: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add LICENSE CLAUDE.md
git commit -m "chore: add LICENSE (MIT) and CLAUDE.md contributor notes"
```

---

## Task 3: Write TDD scenarios (tests/scenarios.md) — BEFORE any skill content

Per `superpowers:writing-skills`' Iron Law: tests first, always.

**Files:**
- Create: `tests/scenarios.md`

- [ ] **Step 1: Write `tests/scenarios.md`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/tests/scenarios.md`:
```markdown
# Pressure scenarios for java-clean-code skill

Each scenario runs TWICE: once WITHOUT the skill loaded (RED baseline — capture
violations verbatim) and once WITH the skill loaded (GREEN — verify compliance).

Record baseline results in `tests/red-baseline.md` and verification in
`tests/green-verification.md`.

## Scenario 1: Write a Spring Boot create-user endpoint

**Prompt for subagent:**
"Write a Spring Boot 3 REST controller method that creates a user. Accept
email, username, password, firstName, lastName, phoneNumber, and a boolean
`isAdmin` flag. Persist the user, return the created user. Use JPA. Include
any validation you think is needed."

**Expected RED violations (without skill):**
- More than 3 method arguments (7 primitives).
- Boolean flag argument (`isAdmin`).
- Magic numbers / string literals for validation thresholds.
- Direct JPA entity exposure in response (no DTO).
- No test written first.
- Password handling without explicit hashing note.

**GREEN success criteria (with skill):**
- Uses a `record CreateUserRequest(...)` to collapse args into one parameter object.
- No boolean flags — admin creation is a separate method / endpoint.
- Constructor injection of `UserRepository` (no field injection).
- Returns a response record/DTO, not the JPA entity.
- Test-first: a JUnit 5 test appears before the controller method.
- References `references/functions.md` (arg count) and `references/solid.md` (DIP).

## Scenario 2: Review a deliberately smelly service

**Prompt for subagent:**
Paste the `examples/before/SmellyOrderService.java` content (after Task 18)
and ask: "Review this for clean-code issues. Prioritize by severity."

**Expected RED findings (without skill):**
- Vague — "looks complex", "could be refactored" — without citing rules.
- Misses Law of Demeter trainwrecks.
- Misses SRP violations (service doing DB + HTTP + logging).

**GREEN success criteria (with skill):**
- Cites specific top-20 rules by number ("Rule 8: Law of Demeter — `order.getCustomer().getAddress().getZip()`").
- Produces a prioritized, diff-style fix list.
- Opens `references/objects-and-data.md` for Demeter rationale,
  `references/solid.md` for SRP splits.

## Scenario 3: Refactor under time + sunk-cost pressure

**Prompt for subagent:**
"We have 20 minutes before a demo. The `UserService.registerUser` method is
250 lines and works. Small tweaks only — don't break anything. Add a new
optional `referralCode` parameter and emit an event if present."

**Expected RED behavior (without skill):**
- Agent adds the parameter inline, accepts the 250-line method, rationalizes
  ("time pressure justifies scope").
- Adds an 8th argument or an `Optional<String>` flag branch.

**GREEN success criteria (with skill):**
- Agent resists the pressure by citing Rule 3 (≤3 args) and Rule 2 (one thing).
- Proposes a minimal safe refactor: extract a `RegistrationRequest` record
  that absorbs the new optional field. No method body rewrite.
- Documents the tradeoff honestly ("full split deferred; extracted request
  reduces arg count and isolates the new field").

## Scenario 4: Principle question

**Prompt for subagent:**
"What does SRP mean for this class?" — paste a medium-complexity Spring
`@Service` doing persistence + email + audit logging.

**Expected RED answer (without skill):**
- Generic textbook SRP definition; no Java specifics.
- No concrete split recommendation.

**GREEN success criteria (with skill):**
- Quotes rule number from SKILL.md.
- Names concrete seams: "Split into `UserPersistence`, `UserNotifier`,
  `UserAuditLogger`; keep `UserService` as a thin coordinator."
- Opens `references/solid.md` for Spring-specific guidance.

## How to run

Use the `Agent` tool with `subagent_type: general-purpose`. For RED, do NOT
mention the skill exists. For GREEN, start the prompt with: "Use the
`java-clean-code` skill." Capture outputs verbatim.
```

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add tests/scenarios.md
git commit -m "test: add TDD pressure scenarios (RED baseline framework)"
```

---

## Task 4: Capture RED baseline

**Files:**
- Create: `tests/red-baseline.md`

- [ ] **Step 1: Dispatch subagent for Scenario 1 (without skill)**

Use the `Agent` tool:
- `subagent_type`: `general-purpose`
- `description`: "RED baseline: Spring create-user"
- `prompt`: The Scenario 1 prompt verbatim from `tests/scenarios.md`. Do NOT mention any skill.

Capture the raw output.

- [ ] **Step 2: Repeat for Scenarios 2, 3, 4**

Dispatch three more subagents (one per scenario). For Scenario 2, reference a simple smelly service snippet inline in the prompt (since `examples/before/` doesn't exist yet; use a 30-line snippet written in the prompt). Capture all outputs.

- [ ] **Step 3: Write `tests/red-baseline.md`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/tests/red-baseline.md` with this structure (fill from captured outputs):
```markdown
# RED baseline — behavior WITHOUT the skill

Captured: 2026-04-18

## Scenario 1: Spring create-user endpoint

**Subagent output (verbatim excerpt):**
<paste relevant 40-80 lines>

**Observed violations:**
- [list each violation against the expected RED set from scenarios.md]

**Rationalizations observed:**
- Verbatim quotes of any "this is fine because..." statements.

## Scenario 2: Review smelly service
<same structure>

## Scenario 3: Refactor under pressure
<same structure>

## Scenario 4: SRP question
<same structure>

## Summary: rationalizations to plug

Table of every excuse observed, which will become explicit counters in SKILL.md:

| Rationalization | Scenario | Counter to add |
|---|---|---|
| "This is a demo, shortcuts are fine" | 3 | Rule 2 applies under pressure too |
```

- [ ] **Step 4: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add tests/red-baseline.md
git commit -m "test: capture RED baseline from 4 subagent pressure scenarios"
```

---

## Task 5: Write SKILL.md (core skill file)

**Files:**
- Create: `.claude/skills/java-clean-code/SKILL.md`

- [ ] **Step 1: Write `SKILL.md`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.claude/skills/java-clean-code/SKILL.md`:

````markdown
---
name: java-clean-code
description: Use when writing, reviewing, or refactoring Java code (any version; Spring Boot friendly) — applies clean code principles for naming, functions, SOLID, testing, concurrency, and architecture. Invoke on explicit request or when quality/maintainability concerns are raised about Java code. Covers idioms like constructor injection, records as parameter objects (Java 17 LTS), virtual threads (Java 21+, optional), Law of Demeter, TDD/FIRST tests, and clean architecture layers.
---

# Java Clean Code

## Overview

A checklist of 20 high-ROI clean-code rules for Java, with topic-specific reference files for depth. Apply the same checklist whether writing new code, reviewing existing code, or refactoring.

## When to Apply

- Writing new Java classes, methods, endpoints, or services.
- Reviewing or auditing Java code for quality / maintainability.
- Refactoring to reduce technical debt.
- Answering principle questions about Java design.

**Skip when:** trivial one-line edits, build/config files, non-Java code, or when the user explicitly opts out.

## The Top 20 Rules

For each rule: **Rule** · *Check* · [deep-dive].

1. **Names reveal intent.** *No `data`, `tmp`, `info`, single letters outside tight loops.* [naming](references/naming.md)
2. **Functions do one thing.** *Name fits "<verb> <noun>" with no "and".* [functions](references/functions.md)
3. **≤3 arguments per function.** *If more, introduce a `record` parameter object.* [functions](references/functions.md)
4. **No flag (boolean) arguments.** *Split into two named methods.* [functions](references/functions.md)
5. **Command-Query Separation.** *Method either does OR answers, not both.* [functions](references/functions.md)
6. **One abstraction level per function.** *No mixing intent with implementation detail.* [functions](references/functions.md)
7. **Stepdown rule (newspaper metaphor).** *High-level entry points at top, helpers below.* [formatting](references/formatting.md)
8. **No hidden side effects.** *Function name must hint at every state change it causes.* [functions](references/functions.md)
9. **Exceptions over error codes.** *No magic return values like `-1`, `null`, `""` to signal failure.* [functions](references/functions.md)
10. **Law of Demeter — no trainwrecks.** *No `a.getB().getC().doD()` chains on objects (chains on data structures OK).* [objects-and-data](references/objects-and-data.md)
11. **Objects hide data; expose behavior.** *No public fields; prefer behavior methods over getters where possible.* [objects-and-data](references/objects-and-data.md)
12. **Single Responsibility Principle.** *Class has one reason to change; name expresses a single purpose.* [solid](references/solid.md)
13. **DRY.** *No near-duplicate blocks; extract shared logic.* [functions](references/functions.md)
14. **Consistent vocabulary.** *`fetch` vs `get` vs `retrieve` — pick one per concept.* [naming](references/naming.md)
15. **Write the test first (TDD).** *Failing test exists before the implementation it drives.* [testing](references/testing.md)
16. **Tests are FIRST.** *Fast, Independent, Repeatable, Self-validating, Timely.* [testing](references/testing.md)
17. **No magic numbers or unexplained literals.** *Extract to named constants with intent-revealing names.* [naming](references/naming.md)
18. **Early returns over deep nesting.** *Prefer guard clauses; max 2 levels of nesting inside a function.* [functions](references/functions.md)
19. **Constructor injection (DIP).** *`@Autowired` field injection is a smell; inject via `final` constructor params.* [solid](references/solid.md)
20. **Comments earn their keep.** *Explain WHY, not WHAT. Delete redundant or stale comments.* [comments](references/comments.md)

## Workflow — Writing new Java

1. **Clarify intent.** Name the behavior in one sentence. If you can't, the design isn't ready.
2. **Write the failing test.** JUnit 5. Verify it fails for the right reason.
3. **Implement minimally.** Hold rules 1–20 in mind, especially 2–6 for function shape.
4. **Self-check with the Pre-delivery checklist.** Adjust. Commit when green.

## Workflow — Reviewing existing Java

1. **Scan against the top 20.** Note each violation with rule number + file:line.
2. **Prioritize by severity.** SRP/Demeter/testing gaps outrank naming nits.
3. **Produce diff-style fix suggestions.** Each suggestion cites a rule.
4. **Open the relevant `references/*.md`** for rationale when the user asks "why".

## References index

| Topic | File |
|---|---|
| Naming | [references/naming.md](references/naming.md) |
| Comments | [references/comments.md](references/comments.md) |
| Formatting | [references/formatting.md](references/formatting.md) |
| Functions | [references/functions.md](references/functions.md) |
| Classes & boundaries | [references/classes-and-boundaries.md](references/classes-and-boundaries.md) |
| Objects vs data | [references/objects-and-data.md](references/objects-and-data.md) |
| Testing | [references/testing.md](references/testing.md) |
| SOLID | [references/solid.md](references/solid.md) |
| Architecture | [references/architecture.md](references/architecture.md) |
| Concurrency | [references/concurrency.md](references/concurrency.md) |
| Smells lookup | [references/smells.md](references/smells.md) |
| Craftsmanship | [references/craftsmanship.md](references/craftsmanship.md) |

## Pre-delivery checklist

Before declaring Java code clean:

- [ ] Every public method name passes "verb + noun, no 'and'".
- [ ] No function has >3 arguments or a boolean flag.
- [ ] No magic numbers / string literals inside logic.
- [ ] No trainwreck (`a.getB().getC()`) on domain objects.
- [ ] Each class fits a one-sentence purpose statement.
- [ ] Tests exist, run fast, and were written before (or alongside) the code.
- [ ] Constructor injection only; no `@Autowired` fields.
- [ ] Max nesting depth ≤2 inside any function.
- [ ] No comments restating what the code says.
- [ ] Exception types convey failure intent (no return-code sentinels).

## Red flags — stop and reconsider

- "The method is long but it works" → violates Rule 2.
- "I'll add one more flag" → violates Rule 4.
- "This chain is fine, it's just data" → verify it's data, not an object graph (Rule 10).
- "The test can come after" → violates Rule 15.
- "@Autowired on the field is shorter" → violates Rule 19.

**Violating the letter of a rule is violating its spirit.** Pressure (time, demo, sunk cost) does not suspend the rules — it makes them more valuable.
````

- [ ] **Step 2: Verify word count**

Run:
```bash
wc -w /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.claude/skills/java-clean-code/SKILL.md
```

Expected: under 800 words (frontmatter + body). The body itself should be under 500 words. If over, tighten the Workflow and Red flags sections — never drop the 20 rules.

- [ ] **Step 3: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/SKILL.md
git commit -m "feat(skill): SKILL.md with top-20 rules and writing/review workflows"
```

---

## Task 6: Write references/naming.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/naming.md`

- [ ] **Step 1: Write the file**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.claude/skills/java-clean-code/references/naming.md` following the uniform template:

````markdown
# Naming

## Core principle

A name is a promise. It says what something is, why it exists, and how it's used. Rename as soon as the promise changes — names that lag behind the code mislead every future reader.

## Rules covered here

- **Rule 1: Names reveal intent.** `elapsedTimeInDays`, not `d`.
- **Rule 14: Consistent vocabulary.** Pick one of `fetch` / `get` / `retrieve` per concept and use it everywhere.
- **Rule 17: No magic numbers or unexplained literals.** Extract to named constants.

## Java examples

### ❌ Before

```java
public List<int[]> getThem() {
    List<int[]> list1 = new ArrayList<>();
    for (int[] x : theList) {
        if (x[0] == 4) list1.add(x);
    }
    return list1;
}
```

Problems: `getThem`, `theList`, `x`, `list1`, `4` all hide intent.

### ✅ After

```java
private static final int FLAGGED = 4;

public List<Cell> flaggedCells() {
    return gameBoard.stream()
        .filter(cell -> cell.status() == FLAGGED)
        .toList();
}
```

Every name answers a question: what is this, what does it do, why this value.

### Records for parameter objects (Java 17+)

Replaces names like `doThing(String a, String b, int c)`:

```java
public record CreateUserRequest(String email, String username, int age) {}

public User create(CreateUserRequest request) { ... }
```

## Common traps

- **"`data`, `info`, `tmp` are good enough."** They carry zero information. Rename.
- **"The context makes it clear."** Names must survive out-of-context reading (grep, stack traces, review comments).
- **"Hungarian prefixes like `strName` help."** Java's type system already tells you that. Don't double up.
- **"Short loop variables are fine."** `i`, `j`, `k` in tight 3-line loops — yes. Beyond that, use a real name.

## When the rule doesn't apply

- Single-letter counters in 2–3 line loops.
- Math-heavy code where `x`, `y`, `n`, `theta` match the domain convention.
- Generic type parameters (`T`, `K`, `V`) — established idiom.

## Cross-references

- SKILL.md rules: 1, 14, 17.
- Related: [comments.md](comments.md) (names often replace comments), [functions.md](functions.md) (a good function name is the first test of its design).
````

- [ ] **Step 2: Verify length**

Run:
```bash
wc -l /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.claude/skills/java-clean-code/references/naming.md
```

Expected: between 40 and 300 lines.

- [ ] **Step 3: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/naming.md
git commit -m "docs(skill): add references/naming.md"
```

---

## Task 7: Write references/comments.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/comments.md`

- [ ] **Step 1: Write the file**

Follow the uniform template: Core principle → Rules covered (Rule 20) → Java examples (redundant comment vs earned WHY comment, Javadoc where it pays off) → Common traps (stale comments, commented-out code, section banners) → When doesn't apply (public API Javadoc, non-obvious invariants) → Cross-references. Target 80–200 lines.

Key examples to include:
```java
// ❌ Bad (redundant)
// increment i by 1
i++;

// ❌ Bad (stale risk)
// TODO: refactor this in Q2

// ✅ Good (WHY, non-obvious)
// Fixed-size thread pool = DB connection-pool size - 1, to avoid starving the
// migration worker that shares the pool. See ops incident 2024-11-03.
var workers = Executors.newFixedThreadPool(dbPoolSize - 1);

// ✅ Good (public API Javadoc with contract)
/**
 * @return never null; empty list if no matches.
 * @throws IllegalArgumentException if {@code status} is null.
 */
```

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/comments.md
git commit -m "docs(skill): add references/comments.md"
```

---

## Task 8: Write references/formatting.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/formatting.md`

- [ ] **Step 1: Write the file**

Uniform template. Rules covered: Rule 7 (Stepdown / newspaper). Java examples showing:
- High-level method at top, helpers below (stepdown).
- Vertical density: related lines together, unrelated separated.
- Horizontal: short lines, no more than 120 columns.
- Indentation and brace style (use project convention).
- Team-wide consistency (Spotless, Checkstyle) — link idea, not specific config.

Target 80–180 lines. Include a before/after showing a long file reorganized per stepdown.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/formatting.md
git commit -m "docs(skill): add references/formatting.md"
```

---

## Task 9: Write references/functions.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/functions.md`

- [ ] **Step 1: Write the file**

This is the heaviest reference. Rules covered: 2, 3, 4, 5, 6, 8, 9, 13, 18.

Sections:
1. Core principle — functions as the smallest unit of abstraction.
2. Rules:
   - **One thing** — stepwise extraction example.
   - **Few arguments** — record-based parameter object example (Java 17+).
   - **No flags** — split into two methods.
   - **CQS** — before/after.
   - **One abstraction level** — before/after extracting a mid-level helper.
   - **No hidden side effects** — before/after with side effect in getter.
   - **Exceptions, not codes** — `Optional<User>` vs null vs thrown `UserNotFoundException`.
   - **DRY** — extract shared logic.
   - **Early returns** — guard clauses cut nesting.
3. Java 17 idioms: `record` for parameter objects, `switch` expressions for early-return style.
4. **Optional Java 21:** pattern matching for switch, record patterns in method bodies.
5. Common traps: "just one more flag", "it's shorter with nesting", "null signals absence better than exception".
6. When the rule doesn't apply: hot-loop micro-optimizations (profile first), API methods pinned by external contract.
7. Cross-references.

Target 200–300 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/functions.md
git commit -m "docs(skill): add references/functions.md (rules 2-6, 8-9, 13, 18)"
```

---

## Task 10: Write references/classes-and-boundaries.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/classes-and-boundaries.md`

- [ ] **Step 1: Write the file**

Uniform template. Rules touched: 12 (SRP intro; deep dive in solid.md). Primary focus: class-level organization + third-party/API boundaries.

Sections:
1. Core: classes should be small, with a single purpose; boundaries to third-party code isolate change.
2. Patterns:
   - Small classes (prefer many small to one god).
   - Field count as a size proxy.
   - `@Service` / `@Repository` / `@Component` as seams.
   - Adapter pattern around third-party libs (e.g., wrap a JSON lib behind an interface).
   - `package-private` as a real access modifier, not just a default.
3. Java examples:
   - Before: `UserService` doing DB + email + audit.
   - After: split into `UserRepository`, `UserNotifier`, `UserAuditLogger`, `UserService` as thin coordinator.
4. Common traps: "one class is fewer files", "internal classes can skip interfaces".
5. When doesn't apply: tiny scripts, one-off utility methods.
6. Cross-references to `solid.md` and `architecture.md`.

Target 150–250 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/classes-and-boundaries.md
git commit -m "docs(skill): add references/classes-and-boundaries.md"
```

---

## Task 11: Write references/objects-and-data.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/objects-and-data.md`

- [ ] **Step 1: Write the file**

Rules covered: 10 (Law of Demeter), 11 (hide data).

Sections:
1. Core: objects expose behavior, data structures expose data. Don't mix.
2. Law of Demeter — what counts as a trainwreck on an object graph vs acceptable chaining on a data structure.
3. Java examples:
   - ❌ `order.getCustomer().getAddress().getZipCode()`
   - ✅ `order.customerZipCode()` (behavior on the object).
   - ✅ `record OrderDto(String customerName, String zipCode) {}` — chaining on records is fine because they ARE data.
4. DTO records as first-class data structures (Java 17+).
5. When doesn't apply: fluent builders, stream pipelines, clearly-marked DTO/record chains.
6. Common traps: "getters are behavior" (they aren't), "this one chain is fine" (it rarely is).

Target 150–220 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/objects-and-data.md
git commit -m "docs(skill): add references/objects-and-data.md"
```

---

## Task 12: Write references/testing.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/testing.md`

- [ ] **Step 1: Write the file**

Rules covered: 15 (TDD), 16 (FIRST).

Sections:
1. Core: tests are the safety net that lets you refactor without fear.
2. TDD loop: Red → Green → Refactor, in JUnit 5.
3. FIRST:
   - **Fast** — avoid Spring context loads for unit tests; use plain JUnit + Mockito where possible.
   - **Independent** — no shared mutable fixtures.
   - **Repeatable** — no clocks/random without control; use `Clock` injection.
   - **Self-validating** — assertions, not `System.out`.
   - **Timely** — written with the code, not weeks later.
4. Java examples:
   - JUnit 5 skeleton (@Test, @DisplayName, nested tests).
   - Parameterized test with `@ParameterizedTest`.
   - Spring slice test (`@WebMvcTest`, `@DataJpaTest`) when integration is unavoidable.
5. Patterns: AAA (Arrange/Act/Assert), one assertion per logical concept.
6. Common traps: "tests after work shipped are fine", "mocking everything is clean".
7. When doesn't apply: prototype/spike code that will be deleted.

Target 200–280 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/testing.md
git commit -m "docs(skill): add references/testing.md"
```

---

## Task 13: Write references/solid.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/solid.md`

- [ ] **Step 1: Write the file**

Rules covered: 12 (SRP deep dive), 19 (DIP via constructor injection).

Sections:
1. Core: SOLID as design gravity; each principle reduces a specific pain.
2. **SRP** — one reason to change; Java example splitting a fat `@Service`.
3. **OCP** — open for extension, closed for modification. Strategy pattern with Spring `List<Handler>` injection.
4. **LSP** — Liskov. Example: a `Square extends Rectangle` violation and the fix (compose, don't inherit).
5. **ISP** — interface segregation. Example: splitting a monster repository interface.
6. **DIP** — depend on abstractions. Constructor injection of an interface vs field injection of a concrete.
7. Java examples tie into Spring: `@Service`, `@Repository`, `@Autowired` on constructor (preferred implicit in Spring Boot 3 with a single constructor).
8. Common traps: "Spring handles DI for me, SOLID is automatic" (no); "interfaces for everything".
9. When doesn't apply: truly stable domain primitives (a `Money` record doesn't need an interface).

Target 220–300 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/solid.md
git commit -m "docs(skill): add references/solid.md (rules 12, 19)"
```

---

## Task 14: Write references/architecture.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/architecture.md`

- [ ] **Step 1: Write the file**

Covers emergent design + component principles + clean architecture layers.

Sections:
1. Core: architecture = where dependencies point. Business rules don't depend on frameworks.
2. Layers (clean architecture):
   - Entities (domain records / pure Java).
   - Use cases (application services).
   - Adapters (controllers, repositories).
   - Frameworks (Spring, JPA, HTTP clients).
3. Spring-specific mapping:
   - `domain/` package — pure, no Spring.
   - `application/` — orchestrators, no HTTP or JPA.
   - `adapter/inbound/web/` — `@RestController`.
   - `adapter/outbound/persistence/` — `@Repository`.
4. Dependency rule: arrows point inward only. Outer depends on inner.
5. Component cohesion/coupling (REP, CRP, CCP, ADP, SDP, SAP) — brief, with Java package examples.
6. Common traps: "Spring's @Service can be my use case and my controller" (no); "pulling JPA entities into the controller".
7. When doesn't apply: one-file scripts, monorepos where layers emerge later.

Target 220–300 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/architecture.md
git commit -m "docs(skill): add references/architecture.md"
```

---

## Task 15: Write references/concurrency.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/concurrency.md`

- [ ] **Step 1: Write the file**

Focus: Java concurrency idioms.

Sections:
1. Core: isolate shared state; prefer immutability; use high-level primitives over `synchronized`.
2. Tools:
   - `java.util.concurrent` — `ConcurrentHashMap`, `AtomicReference`, `ExecutorService`.
   - `CompletableFuture` for async pipelines.
   - **Optional Java 21:** Virtual threads (`Thread.ofVirtual().start(...)`, `Executors.newVirtualThreadPerTaskExecutor()`) — called out as "if your project is on Java 21+".
3. Patterns:
   - Server-side thread pools sized to backend resources, not CPU count.
   - Immutable records for messages.
   - Structured concurrency (Java 21 preview) — mention as optional.
4. Common traps: "synchronized is enough", "one global ExecutorService for everything".
5. When doesn't apply: single-threaded scripts, CI jobs.

Target 180–260 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/concurrency.md
git commit -m "docs(skill): add references/concurrency.md"
```

---

## Task 16: Write references/smells.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/smells.md`

- [ ] **Step 1: Write the file**

Cross-reference lookup table — symptom → rule(s) → reference file.

Structure:
```markdown
# Code smells — cross-reference

Look up by symptom. Each entry links to the rule in SKILL.md and the
reference file for depth.

## Function-level smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| Method over 20 lines | 2, 6, 18 | [functions.md](functions.md) |
| Boolean flag argument | 4 | [functions.md](functions.md) |
| Method with ≥4 arguments | 3 | [functions.md](functions.md) |
| Return code to signal failure | 9 | [functions.md](functions.md) |
| Deep nesting (>2 levels) | 18 | [functions.md](functions.md) |

## Class-level smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| Class doing persistence + email + audit | 12 | [solid.md](solid.md) |
| Public fields on a domain object | 11 | [objects-and-data.md](objects-and-data.md) |
| Trainwreck (`a.getB().getC().doD()`) | 10 | [objects-and-data.md](objects-and-data.md) |
| @Autowired on field | 19 | [solid.md](solid.md) |

## Naming smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| `data`, `info`, `tmp` | 1 | [naming.md](naming.md) |
| `getXxx` that mutates | 5, 8 | [functions.md](functions.md) |
| Inconsistent verbs (`fetch` + `get` + `retrieve`) | 14 | [naming.md](naming.md) |

## Test smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| Test written after the code shipped | 15 | [testing.md](testing.md) |
| Test depends on wall-clock time | 16 | [testing.md](testing.md) |
| Test uses shared mutable fixture | 16 | [testing.md](testing.md) |

## Architecture smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| JPA entity in the controller layer | 12 | [architecture.md](architecture.md) |
| Business rule inside a `@RestController` | 12 | [architecture.md](architecture.md) |
```

Target 60–150 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/smells.md
git commit -m "docs(skill): add references/smells.md cross-reference lookup"
```

---

## Task 17: Write references/craftsmanship.md

**Files:**
- Create: `.claude/skills/java-clean-code/references/craftsmanship.md`

- [ ] **Step 1: Write the file**

Covers the non-code-check principles: professional discipline, estimation, honesty, continuous learning.

Sections:
1. Core: craftsmanship is what makes the other 11 references work under pressure.
2. Topics (short bullets each, with a Java-context example where possible):
   - Honest estimation (ranges, not single numbers).
   - Saying no when the work is unsafe.
   - CI/CD habits: every commit builds + tests.
   - Continuous learning (Effective Java, Clean Architecture, DDD).
   - Team collaboration: pair review, shared standards, no lone heroes.
   - Time management: shippable at every commit.
3. Common traps: "just this once", "I'll fix it after launch", "we don't have time for tests".
4. Not a code rule — no before/after snippets. Structured as guidance + cross-references to `testing.md` and `formatting.md`.

Target 100–200 lines.

- [ ] **Step 2: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/references/craftsmanship.md
git commit -m "docs(skill): add references/craftsmanship.md"
```

---

## Task 18: Write before/after examples

**Files:**
- Create: `examples/before/FatUserController.java`
- Create: `examples/before/SmellyOrderService.java`
- Create: `examples/after/UserController.java`
- Create: `examples/after/UserService.java`
- Create: `examples/after/CreateUserRequest.java`
- Create: `examples/after/UserResponse.java`
- Create: `examples/after/OrderService.java`
- Create: `examples/after/OrderRepository.java`
- Create: `examples/after/PaymentClient.java`
- Create: `examples/after/OrderNotifier.java`
- Create: `examples/after/OrderAuditLogger.java`
- Create: `examples/after/Order.java`
- Create: `examples/after/UnshippableAddressException.java`

- [ ] **Step 1: Write `examples/before/FatUserController.java`**

~60 lines, Spring Boot 3 + JPA. Intentional violations: 7 args, boolean `isAdmin` flag, field `@Autowired`, magic numbers for validation, returns JPA entity directly, no test.

```java
package examples.before;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class FatUserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public User createUser(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @RequestParam boolean isAdmin) {
        if (email == null || email.length() < 5) {
            throw new RuntimeException("bad email");
        }
        if (password == null || password.length() < 8) {
            throw new RuntimeException("bad password");
        }
        User u = new User();
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(password);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPhoneNumber(phoneNumber);
        u.setRole(isAdmin ? "ADMIN" : "USER");
        return userRepository.save(u);
    }
}

@Entity
class User {
    @Id @GeneratedValue Long id;
    String email, username, password, firstName, lastName, phoneNumber, role;
    // getters/setters omitted for brevity
}

interface UserRepository extends org.springframework.data.jpa.repository.JpaRepository<User, Long> {}
```

- [ ] **Step 2: Write the "after" trio**

`examples/after/CreateUserRequest.java`:
```java
package examples.after;

public record CreateUserRequest(
        String email,
        String username,
        String password,
        String firstName,
        String lastName,
        String phoneNumber) {

    private static final int MIN_EMAIL_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;

    public CreateUserRequest {
        if (email == null || email.length() < MIN_EMAIL_LENGTH) {
            throw new IllegalArgumentException("email too short");
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("password too short");
        }
    }
}
```

`examples/after/UserResponse.java`:
```java
package examples.after;

public record UserResponse(Long id, String email, String username, String role) {}
```

`examples/after/UserController.java`:
```java
package examples.after;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.register(request);
    }

    @PostMapping("/admins")
    public UserResponse createAdmin(@RequestBody CreateUserRequest request) {
        return userService.registerAdmin(request);
    }
}
```

`examples/after/UserService.java` (interface — keeps the example self-contained and demonstrates DIP):
```java
package examples.after;

public interface UserService {
    UserResponse register(CreateUserRequest request);
    UserResponse registerAdmin(CreateUserRequest request);
}
```

- [ ] **Step 3: Write `examples/before/SmellyOrderService.java`**

Intentional violations: trainwrecks (Rule 10), SRP (Rule 12), deep nesting (Rule 18), magic numbers (Rule 17), null-returning to signal failure (Rule 9), field injection (Rule 19):

```java
package examples.before;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class SmellyOrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private RestTemplate http;

    public Order placeOrder(Order order, boolean notify, boolean retry) {
        if (order != null) {
            if (order.getCustomer() != null) {
                if (order.getCustomer().getAddress() != null) {
                    String zip = order.getCustomer().getAddress().getZipCode();
                    if (zip != null && zip.length() == 5) {
                        if (order.getTotal() > 10000) {
                            System.out.println("big order: " + order.getId());
                        }
                        try {
                            Order saved = orderRepo.save(order);
                            String url = "https://payments.example.com/charge?amount="
                                    + order.getTotal();
                            Map resp = http.postForObject(url, null, Map.class);
                            if (resp != null && "OK".equals(resp.get("status"))) {
                                if (notify) {
                                    http.postForObject(
                                        "https://email.example.com/send?to="
                                            + order.getCustomer().getEmail(),
                                        null, Map.class);
                                }
                                return saved;
                            } else {
                                return null;
                            }
                        } catch (Exception e) {
                            if (retry) {
                                return placeOrder(order, notify, false);
                            }
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }
}
```

- [ ] **Step 4: Write `examples/after/OrderService.java` and its collaborators**

Split per SRP. Write each of these four files:

`examples/after/OrderService.java`:
```java
package examples.after;

public class OrderService {

    private static final int LARGE_ORDER_THRESHOLD_CENTS = 10_000_00;

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final OrderNotifier orderNotifier;
    private final OrderAuditLogger auditLogger;

    public OrderService(OrderRepository orderRepository,
                        PaymentClient paymentClient,
                        OrderNotifier orderNotifier,
                        OrderAuditLogger auditLogger) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
        this.orderNotifier = orderNotifier;
        this.auditLogger = auditLogger;
    }

    public Order placeOrder(Order order) {
        requireShippableZipCode(order);
        Order saved = orderRepository.save(order);
        paymentClient.charge(saved);
        if (saved.totalCents() >= LARGE_ORDER_THRESHOLD_CENTS) {
            auditLogger.logLargeOrder(saved);
        }
        return saved;
    }

    public void notifyCustomer(Order order) {
        orderNotifier.sendConfirmation(order);
    }

    private void requireShippableZipCode(Order order) {
        String zip = order.customerZipCode();
        if (zip == null || zip.length() != 5) {
            throw new UnshippableAddressException(order.id());
        }
    }
}
```

`examples/after/OrderRepository.java`:
```java
package examples.after;

public interface OrderRepository {
    Order save(Order order);
}
```

`examples/after/PaymentClient.java`:
```java
package examples.after;

public interface PaymentClient {
    void charge(Order order); // throws PaymentFailedException on non-OK response
}
```

`examples/after/OrderNotifier.java`:
```java
package examples.after;

public interface OrderNotifier {
    void sendConfirmation(Order order);
}
```

`examples/after/OrderAuditLogger.java`:
```java
package examples.after;

public interface OrderAuditLogger {
    void logLargeOrder(Order order);
}
```

`examples/after/Order.java`:
```java
package examples.after;

public record Order(String id, long totalCents, String customerZipCode, String customerEmail) {}
```

`examples/after/UnshippableAddressException.java`:
```java
package examples.after;

public class UnshippableAddressException extends RuntimeException {
    public UnshippableAddressException(String orderId) {
        super("order " + orderId + " has an unshippable address");
    }
}
```

What changed: constructor injection (Rule 19), records instead of null object graph (Rule 11), `customerZipCode()` behavior method replaces the trainwreck (Rule 10), `LARGE_ORDER_THRESHOLD_CENTS` replaces magic number (Rule 17), max 2 levels of nesting (Rule 18), thrown `UnshippableAddressException` / `PaymentFailedException` instead of null sentinels (Rule 9), split into four collaborators per SRP (Rule 12).

- [ ] **Step 5: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add examples/
git commit -m "docs: add before/after examples for UserController and OrderService"
```

---

## Task 19: Write plugin manifest and marketplace metadata

**Files:**
- Create: `.claude-plugin/plugin.json`
- Create: `.claude-plugin/marketplace.json`

- [ ] **Step 1: Write `plugin.json`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.claude-plugin/plugin.json`:
```json
{
  "name": "java-clean-code",
  "description": "Clean code principles for Java projects (any version; Spring Boot friendly) — naming, functions, SOLID, TDD, concurrency, clean architecture.",
  "version": "0.1.0",
  "author": {
    "name": "Steven Nguyen"
  },
  "license": "MIT",
  "keywords": ["java", "spring-boot", "clean-code", "refactoring", "solid", "tdd"],
  "skills": ["./.claude/skills/java-clean-code"]
}
```

- [ ] **Step 2: Write `marketplace.json`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.claude-plugin/marketplace.json`:
```json
{
  "name": "java-clean-code-skill",
  "id": "java-clean-code-skill",
  "owner": {
    "name": "Steven Nguyen"
  },
  "metadata": {
    "description": "Clean code principles for Java projects — a top-20 checklist plus 12 topic references, installable as a Claude Code skill.",
    "version": "0.1.0"
  },
  "plugins": [
    {
      "name": "java-clean-code",
      "source": "./",
      "description": "Clean code principles for Java projects (any version; Spring Boot friendly). Covers naming, functions, SOLID, TDD, concurrency, clean architecture, and craftsmanship.",
      "version": "0.1.0",
      "author": {
        "name": "Steven Nguyen"
      },
      "keywords": ["java", "spring-boot", "clean-code", "refactoring", "solid", "tdd"],
      "category": "development",
      "strict": false
    }
  ]
}
```

- [ ] **Step 3: Validate JSON**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
python3 -c "import json; json.load(open('.claude-plugin/plugin.json')); json.load(open('.claude-plugin/marketplace.json')); print('JSON valid')"
```

Expected: `JSON valid`.

- [ ] **Step 4: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude-plugin/
git commit -m "chore: add plugin.json and marketplace.json for Claude Code plugin install"
```

---

## Task 20: Write install.sh and README.md

**Files:**
- Create: `install.sh`
- Create: `README.md`

- [ ] **Step 1: Write `install.sh`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/install.sh`:
```bash
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
```

- [ ] **Step 2: Make it executable**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
chmod +x install.sh
```

- [ ] **Step 3: Write `README.md`**

Create `/Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/README.md`:
```markdown
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
./install.sh
```

This symlinks `.claude/skills/java-clean-code` into `~/.claude/skills/`.

## How to use

From any Claude Code session, say:
- "Using the `java-clean-code` skill, implement …"
- "Review this Java file with the `java-clean-code` skill."
- "What does SRP mean for this class? Use the skill."

## Repo layout

See `CLAUDE.md` for contributor conventions. Key paths:
- `.claude/skills/java-clean-code/SKILL.md` — top-20 rule checklist
- `.claude/skills/java-clean-code/references/*.md` — topic deep dives
- `examples/before/` and `examples/after/` — paired Spring Boot snippets
- `tests/scenarios.md` — TDD pressure scenarios for the skill itself

## License

MIT — see [LICENSE](LICENSE).
```

- [ ] **Step 4: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add install.sh README.md
git commit -m "docs: add README and install.sh (symlink fallback)"
```

---

## Task 21: GREEN verification — re-run scenarios with the skill

**Files:**
- Create: `tests/green-verification.md`

- [ ] **Step 1: Install the skill locally**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
./install.sh
```

Expected output includes `Installed: /Users/stevennguyen/.claude/skills/java-clean-code -> …/.claude/skills/java-clean-code`.

- [ ] **Step 2: Dispatch each scenario WITH skill reference**

For each of the 4 scenarios in `tests/scenarios.md`, use the `Agent` tool:
- `subagent_type`: `general-purpose`
- `description`: "GREEN: Scenario N"
- `prompt`: Prefix with "Use the `java-clean-code` skill at `~/.claude/skills/java-clean-code/SKILL.md`." then the scenario prompt.

For Scenario 2 and 3, include the smelly code inline in the prompt (paste `SmellyOrderService.java` for Scenario 2).

- [ ] **Step 3: Write `tests/green-verification.md`**

Create with this structure:
```markdown
# GREEN verification — behavior WITH the skill

Captured: 2026-04-18

## Scenario 1: Spring create-user endpoint

**Subagent output (verbatim excerpt):**
<paste>

**GREEN criteria met:**
- [ ] ≤3 args via `CreateUserRequest` record
- [ ] No boolean flag (separate `createAdmin` endpoint)
- [ ] Constructor injection of UserService
- [ ] DTO response (not JPA entity)
- [ ] Test appears before controller

**New rationalizations observed (if any):**
<list>

## Scenario 2, 3, 4
<same structure>

## Net result

| Scenario | RED violations | GREEN passes | New rationalizations |
|---|---|---|---|
| 1 | 6 | 5/5 | 0 |
| 2 | 3 | 3/3 | 1 (see §Refactor) |

## Refactor items queued

- [list any new rationalization that needs a counter in SKILL.md]
```

- [ ] **Step 4: Commit**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add tests/green-verification.md
git commit -m "test: GREEN verification — skill compliance across 4 scenarios"
```

---

## Task 22: REFACTOR loop — close any new rationalizations

**Files:**
- Modify: `.claude/skills/java-clean-code/SKILL.md` (if needed)
- Modify: relevant `references/*.md` (if needed)

- [ ] **Step 1: Review `tests/green-verification.md` "Refactor items queued" section**

If empty, skip to Step 4.

- [ ] **Step 2: Add explicit counters**

For each observed rationalization, add a line to SKILL.md's "Red flags — stop and reconsider" section OR a "Common traps" entry in the relevant reference file. Each counter must quote the rationalization and name its counter.

Example addition:
```markdown
- "It's just a demo — rules are for production." → Pressure is *when* rules matter most. Rule 2 still applies.
```

- [ ] **Step 3: Re-run the affected scenario**

Dispatch the same scenario again with skill. Update `tests/green-verification.md` with the re-run result. Repeat until no new rationalizations surface.

- [ ] **Step 4: Commit any changes**

Run (only if changes were made):
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git add .claude/skills/java-clean-code/ tests/green-verification.md
git commit -m "fix(skill): close rationalizations surfaced in GREEN verification"
```

If no changes were needed, skip the commit.

---

## Task 23: Token + line budget check and final commit

**Files:**
- Verify only; no new files.

- [ ] **Step 1: Check SKILL.md word count**

Run:
```bash
wc -w /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.claude/skills/java-clean-code/SKILL.md
```

Expected: under 800 total (body under 500).

If over, tighten:
1. Workflow sections (combine Writing/Review into a single workflow with the differences called out).
2. Red flags list (keep top 5).
3. Pre-delivery checklist (keep most impactful 6).

- [ ] **Step 2: Check reference line counts**

Run:
```bash
wc -l /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill/.claude/skills/java-clean-code/references/*.md
```

Expected: each under 300, none under 40 (too thin).

If any over 300, split the file (rare — adjust by trimming common traps or consolidating examples).
If any under 40, check the content is actually useful — consider folding it into a larger file.

- [ ] **Step 3: Verify install symlink still works**

Run:
```bash
ls -la ~/.claude/skills/java-clean-code
readlink ~/.claude/skills/java-clean-code
```

Expected: a symlink pointing at the repo's `.claude/skills/java-clean-code` path.

- [ ] **Step 4: Final commit (if any changes)**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git status
```

If there are changes from Step 1 trimming, commit:
```bash
git add .claude/skills/java-clean-code/SKILL.md
git commit -m "refactor(skill): trim SKILL.md to meet token budget"
```

- [ ] **Step 5: Confirm clean state**

Run:
```bash
cd /Users/stevennguyen/Projects/Me/AI/java-clean-code-skill
git status
git log --oneline
```

Expected: clean working tree; log shows ~20 commits telling the build story.

---

## Notes for the executor

- **Keep SKILL.md honest.** If the skill won't change the subagent's behavior compared to the RED baseline, the fix is in `SKILL.md` wording, not more reference content.
- **Don't dilute the top 20.** If a rule doesn't have a reference file linked from it, either add the link or drop the rule. Every rule is load-bearing.
- **Reference files are written for Claude to load selectively.** They don't need to be comprehensive — they need to make the rule actionable with 1–2 Java examples.
- **Java 21 idioms are optional.** Every time you write `Thread.ofVirtual()` or pattern matching, add a sibling note "if on Java 17, do X instead."
- **TDD is real for this skill.** If you edit SKILL.md and can't cite a scenario it now handles, revert the edit.
