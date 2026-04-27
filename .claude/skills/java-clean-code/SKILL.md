---
name: java-clean-code
description: Use when writing, reviewing, or refactoring Java code — services, controllers, libraries, batch jobs, CLIs (Spring Boot, Quarkus, plain Java). Triggers: "clean this up", "is this idiomatic?", code review on Java files, code-smell or technical-debt concerns, SOLID/DRY/SRP questions, TDD setup for Java.
---

# Java Clean Code

## Overview

20 high-ROI rules for Java, with topic references for depth. Same checklist for writing, reviewing, and refactoring.

## When to Apply

Writing, reviewing, or refactoring Java classes/methods/endpoints/services; or answering principle questions about Java design.

**Skip when:** trivial edits, build/config files, non-Java code, or when the user opts out.

## The Top 20 Rules

Each rule links to a reference file for depth. A subset keeps a *Check* hint where the name alone is ambiguous.

1. **Names reveal intent.** [naming](references/naming.md)
2. **Functions do one thing.** [functions](references/functions.md)
3. **≤3 arguments per function.** [functions](references/functions.md)
4. **No flag (boolean) arguments.** [functions](references/functions.md)
5. **Command-Query Separation.** *Method either does OR answers, not both.* [functions](references/functions.md)
6. **One abstraction level per function.** [functions](references/functions.md)
7. **Stepdown rule (newspaper metaphor).** *High-level entry points at top, helpers below.* [formatting](references/formatting.md)
8. **No hidden side effects.** [functions](references/functions.md)
9. **Exceptions over error codes.** [functions](references/functions.md)
10. **Law of Demeter — no trainwrecks.** *No `a.getB().getC().doD()` chains on objects (chains on data structures OK).* [objects-and-data](references/objects-and-data.md)
11. **Objects hide data; expose behavior.** [objects-and-data](references/objects-and-data.md)
12. **Single Responsibility Principle.** [solid](references/solid.md)
13. **DRY.** [functions](references/functions.md)
14. **Consistent vocabulary.** [naming](references/naming.md)
15. **Write the test first (TDD).** [testing](references/testing.md)
16. **Tests are FIRST.** *Fast, Independent, Repeatable, Self-validating, Timely.* [testing](references/testing.md)
17. **No magic numbers or unexplained literals.** [naming](references/naming.md)
18. **Early returns over deep nesting.** *Max 2 levels of nesting inside a function.* [functions](references/functions.md)
19. **Constructor injection (DIP).** *`@Autowired` field injection is a smell; inject via `final` constructor params.* [solid](references/solid.md)
20. **Comments earn their keep.** *Explain WHY, not WHAT.* [comments](references/comments.md)

## Workflow

**Writing:** (1) Name the behavior — if you can't, the design isn't ready. (2) Write the failing test (JUnit 5). (3) Implement minimally, rules 2–6 in mind. (4) Self-check with the checklist below.

**Reviewing:** (1) Cite each violation by rule **name** (*Demeter*, *SRP*, *DIP* — never bare "R10") + file:line. (2) Prioritize severity — SRP/Demeter/testing gaps outrank naming nits. (3) Diff-style fixes, each naming the rule. (4) Open `references/*.md` for rationale.

## References

Each rule links inline to the topic file. Additional depth: [smells](references/smells.md), [architecture](references/architecture.md), [concurrency](references/concurrency.md), [craftsmanship](references/craftsmanship.md), [creating-objects](references/creating-objects.md), [generics](references/generics.md), [lambdas-and-streams](references/lambdas-and-streams.md), [exceptions](references/exceptions.md).

## Pre-delivery checklist

- [ ] Verb+noun names; no flags; ≤3 args.
- [ ] Tests exist, run fast, written first.

## Rationalizations

| Excuse | Counter |
|---|---|
| "One more arg" | Args/Flags: extract a `record`. |
| "Use Lombok `@Data`" | DIP: write the explicit constructor. |
| "Fine for a demo" | SRP holds under pressure. |
| "Review with bare numbers (R10)" | Cite the rule **name** + `references/*.md`. |

**Violating the letter of a rule is violating its spirit.** Pressure (time, demo, sunk cost) does not suspend the rules — it makes them more valuable.
