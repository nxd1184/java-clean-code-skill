# GREEN verification — behavior WITH the skill

Captured: 2026-04-19

---

## Scenario 1: Spring create-user endpoint

**GREEN criteria met:**
- [x] ≤3 args via `CreateUserRequest` record (7 fields → 1 parameter object)
- [x] No boolean flag — `UserRole` enum replaces `boolean isAdmin`; agent explicitly cited Rule 4
- [x] Constructor injection of `UserRegistrationService`; no `@Autowired` fields
- [x] DTO response (`RegisteredUserDto`) — JPA entity never returned to controller
- [x] Test-first: `UserRegistrationControllerTest` and `UserRegistrationServiceTest` written before implementation
- [x] Rules cited by number throughout (1–20 coverage, explicit checklist at end)

**New rationalizations observed:** None.

---

## Scenario 2: Review smelly service

**GREEN criteria met:**
- [x] Violations cited by rule number: Rule 12 (SRP), Rule 9 (null returns), Rule 18 (nesting), Rule 19 (field injection), Rule 4 (boolean flags), Rule 10 (Demeter), Rule 3 (arg count), Rule 17 (magic literals), Rule 1 (weak names), Rule 8 (hidden side effects)
- [x] Prioritized severity table (Critical → Important → Minor)
- [x] Diff-style fix suggestions for each violation
- [x] Law of Demeter trainwrecks identified (`order.getCustomer().getAddress().getZipCode()`)
- [x] SRP violations named (service doing DB + HTTP + logging)

**New rationalizations observed:** None.

---

## Scenario 3: Refactor under time + sunk-cost pressure

**GREEN criteria met:**
- [x] Time pressure explicitly resisted — agent quoted SKILL.md: "Pressure does not suspend the rules — it makes them more valuable"
- [x] `referralCode` added to `RegistrationRequest` record, NOT as a new method argument (Rule 3)
- [x] No 8th argument or `Optional<String>` flag branch
- [x] Tradeoff documented honestly: minimal safe change (record field + event method); 250-line method not rewritten
- [x] Two test cases written before implementation (Rule 15)
- [x] Explicit rejection list: `Optional` as param, inlining into long method, skipping tests

**New rationalizations observed:** None.

---

## Scenario 4: SRP principle question

**GREEN criteria met:**
- [x] Rule 12 quoted by number
- [x] Concrete seams named: `UserUniquenessValidator`, `WelcomeEmailService`, `UserAuditService`, `UserRegistrationService` (thin coordinator)
- [x] Referenced `references/solid.md` for Spring-specific guidance
- [x] Also surfaced Rule 19 (field injection), Rule 2 (one thing), Rule 8 (hidden side effects) as secondary violations

**New rationalizations observed:** None.

---

## Net result

| Scenario | RED violations (baseline) | GREEN passes | New rationalizations |
|---|---|---|---|
| 1: Create-user endpoint | 6 (7+ args, boolean flag, magic literals, entity exposure, no test, no hash note) | 6/6 | 0 |
| 2: Review smelly service | 3 (vague review, missed Demeter, missed SRP) | 3/3 (plus 8 additional violations surfaced) | 0 |
| 3: Refactor under pressure | 2 (inline arg added, pressure accepted) | 2/2 | 0 |
| 4: SRP question | 2 (generic definition, no split recommendation) | 2/2 | 0 |

## Refactor items queued

None. All 4 scenarios passed all GREEN criteria with no new rationalizations observed. Task 22 (REFACTOR loop) has no items to action.

---

## EJ-merge GREEN verifications (captured 2026-04-27)

Each of the 10 EJ-merge scenarios was re-run against a fresh general-purpose
subagent **with** the java-clean-code skill loaded (read SKILL.md + relevant
reference). Aggregate result: **10 / 10 PASS**.

### Scenario 5 — Constructor with five parameters: ✅ PASS

Subagent output cites Rule 3 (Args) + Rule 4 (Flags), cites EJ Item 2
(Builder when many parameters), suggests Builder pattern with full code,
cross-links to `references/creating-objects.md`.

### Scenario 6 — Raw type ArrayList: ✅ PASS

Cites **EJ Item 26 — Don't use raw types** explicitly. Recommends
`ArrayList<String>` and explains the runtime-vs-compile-time safety trade.
Cross-links to generics.md.

### Scenario 7 — Manual for-loop: ✅ PASS

Suggests `names.stream().map(String::toUpperCase).toList()`. Cites EJ Item
42 (Lambdas), Item 43 (Method references), Item 45 (Streams judiciously).
Cross-links to lambdas-and-streams.md.

### Scenario 8 — Catch-and-ignore: ✅ PASS

Cites **EJ Item 77 — Don't ignore exceptions**. Recommends naming the
variable `ignored` with a rationale comment; offers log/rethrow/fallback
options. Cross-links to exceptions.md with line numbers.

### Scenario 9 — Missing equals/hashCode: ✅ PASS

Cites **EJ Items 10/11 (equals/hashCode contract)**. Suggests record
conversion as the modern fix. Explains the Set<> failure mode concretely.
Cross-links to objects-and-data.md.

### Scenario 10 — ArrayList in method signature: ✅ PASS

Cites **Rule 19 (DIP)** AND **EJ Item 64 (refer by interface)**. Recommends
`List<Customer>` return type. Cross-links to classes-and-boundaries.md
with line numbers (271–291).

### Scenario 11 — `new Thread().start()`: ✅ PASS

Cites **EJ Item 80** (Executors over threads). Recommends Spring's
`TaskExecutor` with constructor injection. Mentions `@Async` +
`CompletableFuture<T>` for error propagation. Also cites Item 81.

### Scenario 12 — `Map<String,Object> data`: ✅ PASS

Cites Rule 1 (Names), Rule 11 (objects hide data), and **EJ Item 68
(naming conventions)** — notes boolean methods should start with
`is`/`has`/`should`. Suggests typed record + intention-revealing method
name.

### Scenario 13 — Premature optimization comment: ✅ PASS (the BIG one)

This was the largest RED-baseline gap: the unloaded subagent had said
*"the performance comment is helpful — it signals 'this was measured.'"*
— actively wrong (the comment makes the claim *without* showing data).

**With the skill loaded**, the subagent now cites EJ Item 67 (Optimize
judiciously) directly and identifies the comment as the smell:
*"the comment shifts maintenance burden to future developers without
having profiled first… caching without measurement is the anti-pattern."*
Cross-links to craftsmanship.md.

### Scenario 14 — Missing Javadoc: ✅ PASS

Cites Rule 20 + EJ Item 56 (doc comments for exposed APIs). Provides full
Javadoc example with `@param`/`@return`/`@throws`, idempotency note, and
PCI-DSS scope flagged.

### Aggregate result

**10 of 10 PASS.** Every scenario shows the discriminator the RED baseline
lacked: explicit EJ-item citation by number+title, plus a cross-link to
the relevant new/expanded reference. No new rationalizations observed.

The biggest RED→GREEN swing was Scenario 13 (premature optimization):
the skill content explicitly inoculated against the "comment trusts itself"
trap that the unloaded subagent fell into.

### Refactor items queued (EJ merge)

None. All 10 scenarios pass on first run; no skill-content fixes needed.
