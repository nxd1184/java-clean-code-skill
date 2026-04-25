# Craftsmanship

## Core principle

Craftsmanship is what makes the other rules work under pressure. Rules 1–20 are easy to follow when there is no pressure. The professional discipline to follow them when there IS pressure is what separates maintainable code from technical debt.

## Topics

### Honest estimation

Give ranges, not single numbers. "2 to 4 days" is honest. "2 days" is a guess dressed as a commitment.

- If you don't know how long something takes, say so — then timebox a spike to find out.
- Never absorb a deadline by silently dropping tests or skipping design.
- Communicate early when scope is larger than expected.

### Saying no (and saying when)

It is more professional to say "I can't do that safely in that time" than to ship code you know is wrong.

- "No, but I can do X by Thursday" is a professional answer.
- Accepting an impossible deadline and shipping broken code is not.
- A clean codebase is a professional obligation, not a luxury.

### CI/CD habits

Every commit should leave the build green.

- Run tests locally before pushing.
- Treat a failing CI build as a production incident — fix it immediately.
- Keep commits small and shippable. A PR that can't be reverted is a liability.

### Continuous learning

The field moves. Staying current is part of the job.

Recommended reading for Java craftspeople:
- *Effective Java* — Bloch (language-level idioms)
- *Clean Code* — Martin (the source of these rules)
- *Clean Architecture* — Martin (the architectural layer model)
- *Domain-Driven Design* — Evans (domain modelling, ubiquitous language)

### Team collaboration

Code is a team sport.

- Pair review: a second set of eyes catches what familiarity hides.
- Shared standards: a linting rule is worth more than a style argument.
- No lone heroes: knowledge hoarded is a bus-factor risk.
- Leave the code better than you found it (Boy Scout Rule).

### Shippable at every commit

Design in increments small enough that you could stop at any commit and the system still works.

- Feature flags for half-built features, not commented-out code.
- One logical change per commit — makes reverting surgical.
- Never commit secrets, credentials, or generated artifacts.

## Common traps

- "Just this once" — every rule broken "just this once" becomes precedent.
- "I'll fix it after launch" — post-launch pressure is higher than pre-launch pressure. It won't be fixed.
- "We don't have time for tests" — you don't have time NOT to have tests. Bugs found in production cost 10× more than bugs found at commit time.
- "The code works, that's what matters" — code that works but can't be changed will be replaced. Working AND maintainable is the standard.

## Cross-references

Craftsmanship applies to every rule in SKILL.md. The most common points of failure under pressure:

| Shortcut taken | Rule violated | Reference |
|---|---|---|
| "No time for tests" | 15, 16 | [testing.md](testing.md) |
| "One more flag is fine" | 4 | [functions.md](functions.md) |
| "We'll refactor later" | 2, 12 | [functions.md](functions.md), [solid.md](solid.md) |
| "Comments explain it" | 1, 20 | [naming.md](naming.md), [comments.md](comments.md) |
