# Prompts that work with `java-clean-code`

A cheatsheet of invocations that get the skill to perform well. The skill is
installed and loaded — these prompts control whether a given turn is fast and
specific, or slow and generic.

## Why prompt shape matters

The `java-clean-code` skill is a top-20 rule catalog plus 12 topic references.
Vague prompts ("clean this up") force the skill to summarize the whole catalog
instead of applying specific rules to specific code. Concrete prompts (name the
file, ask for rule names, give the workflow) keep responses grounded and
fast.

The working prompts below all share four shapes:

1. They **name the skill explicitly** (`java-clean-code`) to avoid the
   "which skill should I use?" decision tax.
2. They **anchor on a file, class, or pasted snippet** so the skill applies
   rules to concrete code, not the abstract idea of code.
3. They **ask for rule names (Demeter, SRP, DIP, etc.) and `references/*.md`
   links** so the skill commits to its own catalog instead of generic SOLID talk.
4. They **keep scope small** — one method, one controller, one service.

---

## Writing new Java

```text
Using the `java-clean-code` skill, add a POST /users endpoint to
UserController that creates a user. Use constructor injection, a request
`record` parameter object, and write the JUnit 5 test first. Cite the rule
name next to each decision.
```

```text
Write a Spring `@Service` that charges a payment. Apply the `java-clean-code`
skill — test-first, ≤3 args, no flag booleans, DTO in/out. Open
`references/testing.md` for the FIRST test guidance.
```

```text
Scaffold a JPA repository + service + controller for `Order`, following the
`java-clean-code` skill. Explain which rule each file-split decision
satisfies (SRP, DIP, etc.).
```

```text
Using `java-clean-code`, add a `@Scheduled` background job that retries
failed payments. Keep the job thin — one method, delegate to a collaborator.
Write the failing test first.
```

## Reviewing existing Java

```text
Review `src/main/java/com/example/OrderService.java` using the
`java-clean-code` skill. List each violation with its rule name (e.g.
Demeter, SRP, DIP) and a diff-style fix. Prioritize Critical → Important → Minor.
```

```text
Code review this PR diff against the `java-clean-code` top-20. Call out Law
of Demeter trainwrecks and SRP splits explicitly. Cite `references/*.md`
for any violation that deserves deeper reading.
```

```text
Is this service idiomatic Java 17 + Spring Boot 3? Use the `java-clean-code`
skill; for each non-idiomatic piece, cite the rule name and suggest the
minimal fix.
```

```text
Using `java-clean-code`, audit the `@Autowired` usage in this package. Flag
every field-injection site (DIP violation) and propose the constructor-injection
rewrite.
```

```text
Review this controller. Using `java-clean-code`, verify the layer boundary
(entity never returned, DTO response). Cite SRP and `references/architecture.md`.
```

## Refactoring under pressure

```text
I have 20 minutes before a demo. Add a `referralCode` field to user
registration. Apply the `java-clean-code` skill — no shortcuts on the
Args/Flags rules, no "just one more flag".
```

```text
Refactor this 250-line method for clarity. Using `java-clean-code`, resist
the "long but it works" rationalization (One-thing / Abstraction-level / Nesting). Show the stepdown
(newspaper) structure after splitting.
```

```text
Using `java-clean-code`, add one optional field to an existing record. Do
NOT add an 8th argument to the constructor — extend the `record` itself.
Cite the Args rule.
```

## Principle / reference lookups

```text
Using `java-clean-code`, what does SRP mean for this `UserService` that
does persistence, email, and audit? Name the concrete seams and reference
`references/solid.md`.
```

```text
Explain the Law of Demeter in the context of this code, with a Java
Before/After. Use `java-clean-code` → `references/objects-and-data.md`.
```

```text
Using `java-clean-code`, which rules does this snippet violate? Just the
list with rule names — no fixes yet.
```

## Prompt patterns that make it work well

1. **Name the skill explicitly.** `"Using the java-clean-code skill, …"`
   beats `"follow clean code"`.
2. **Anchor on a file, class, or pasted snippet.** Abstract "review my Java"
   gives abstract "follow SRP" back. Paste the code or give the path.
3. **Ask for rule names + `references/*.md` links.** Forces the skill to
   commit to its catalog instead of rewording generic advice.
4. **Give the workflow explicitly.** "test-first", "cite each violation with
   file:line", "produce diff-style fixes" — these match the skill's own
   workflow sections and cut ambiguity.
5. **Keep the scope small.** One method, one controller, one service. Big
   requests force the skill to summarize; small requests force it to apply.

## Anti-prompts (symptoms you'll see)

| Prompt | What you get | Why |
|---|---|---|
| "Make my code cleaner." | Generic SOLID / DRY lecture. | No skill named, no anchor, no rule commitment. |
| "Review everything in `src/`." | Long shallow summary. | Scope too big — skill summarizes instead of applying. |
| "What do you think of this?" | Maybe doesn't load the skill. | No trigger keyword, no skill name. |
| "Just fix it." | Fix without rule citations, no test-first. | No workflow given — skill skips its own discipline. |

## How to pair with SKILL.md's pre-delivery checklist

After Claude produces code or a review, ask:

```text
Before we stop, run the pre-delivery checklist from SKILL.md against this
diff. Confirm: verb+noun names / no flags / ≤3 args; no trainwrecks, one
class purpose; tests exist and were written first.
```

That explicit callback forces the skill's own verification gate before you
merge.

## When this cheatsheet isn't enough

If you've used the prompts above and the skill still feels slow or wrong:

1. Capture the exact prompt you gave and the exact response.
2. Note which rule (by name) it *should* have cited but didn't.
3. File it against `tests/red-baseline.md` — a new RED scenario is how the
   skill gets stronger.
