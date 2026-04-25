# RED baseline — behavior WITHOUT the skill

Captured: 2026-04-18

Four general-purpose subagents were dispatched with the scenarios verbatim from
`tests/scenarios.md`. None were told the `java-clean-code` skill exists. The
outputs below are the representative excerpts used to design the skill.

---

## Scenario 1: Spring create-user endpoint

**Prompt:** Write a Spring Boot 3 REST controller method that creates a user.
Accept email, username, password, firstName, lastName, phoneNumber, and a
boolean `isAdmin` flag. Persist the user, return the created user. Use JPA.
Include any validation you think is needed.

**Subagent output (verbatim excerpt):**

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest req) {
        User created = userService.createUser(
            req.email(),
            req.username(),
            req.password(),
            req.firstName(),
            req.lastName(),
            req.phoneNumber(),
            req.isAdmin()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

public record CreateUserRequest(
    @Email String email,
    @NotBlank String username,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    String phoneNumber,
    boolean isAdmin
) {}
```

> "Returning the `User` entity directly is fine for a demo; swap in a DTO
> before production."
>
> "Use Lombok `@Data` / `@RequiredArgsConstructor` to keep boilerplate down."

**Observed violations (against scenarios.md expected RED set):**
- ✗ Returns the JPA `User` entity directly from the controller (no response DTO).
- ✗ Keeps `boolean isAdmin` as a flag field on the request record — a flag
  argument in disguise. No split into a separate admin-creation path.
- ✗ No test written first. Controller shipped without a JUnit 5 test.
- ✗ `UserService.createUser(...)` has 7 primitive arguments; arg count hidden
  inside the service layer instead of truly reduced.
- ✗ Password handling relegated to an offhand "the service should hash it"
  comment; no `PasswordEncoder` wired at the controller boundary.
- ✗ Lombok `@RequiredArgsConstructor` recommended instead of an explicit
  constructor — obscures which dependencies are required.

**Rationalizations observed (verbatim):**
- *"Fine for a demo — swap in a DTO before production."* → accepts entity
  leakage as acceptable for "demo" code. Rule 12 (SRP / layer separation)
  needs to hold under demo pressure.
- *"Use Lombok @Data to keep boilerplate down."* → trades explicitness for
  brevity. Rule 19 (constructor injection) must specify *explicit* final
  constructors, not Lombok-generated ones, so dependencies stay visible.
- *"Validation is covered by `@Valid` on the request."* → treats annotation
  presence as sufficient; no mention of business-rule validation layer.

---

## Scenario 2: Review the smelly OrderService

**Prompt:** Review this 60-line `OrderService` with a `placeOrder` method that
does validation, DB writes, a payment HTTP call, email send, and audit logging
inline, with `@Autowired` fields, 5-level nested `if`, `Map<String, Object>`
payload, null returns on error, and `System.out.println` for logging.

**Subagent output (verbatim excerpt):**

> "This service does a lot. My main concerns, in rough priority order:
>
> 1. **Too many responsibilities.** Validation, persistence, payment HTTP,
>    email send, and audit logging are all in one class. Split into an
>    `OrderValidator`, an `OrderRepository` (already exists, but put the write
>    here), a `PaymentClient`, an `OrderNotifier`, and an `AuditLogger`.
>    `OrderService` becomes a thin orchestrator.
>
> 2. **Field injection (`@Autowired`) is brittle.** Prefer constructor
>    injection with `final` fields — it makes missing dependencies a compile
>    error and makes unit tests trivial.
>
> 3. **Swallowed exceptions and null returns.** Several branches `catch
>    (Exception e) { return null; }`. Throw a meaningful checked or
>    domain exception; let callers decide.
>
> 4. **Deep nesting (5 levels).** Use guard clauses / early returns. The
>    current shape is hard to read and test.
>
> 5. **Raw `Map<String, Object>` payload.** Replace with a typed record or
>    DTO.
>
> 6. **Boolean flag parameter `placeOrder(..., boolean sendEmail)`.** Split
>    into two methods.
>
> 7. **Hard-coded URL and secret string literals.** Move to
>    `@ConfigurationProperties`.
>
> 8. **`System.out.println` logging.** Use SLF4J."

**Observed violations (against scenarios.md expected RED set):**
- ✗ Did NOT explicitly cite "Law of Demeter" or call out trainwrecks by name —
  the `order.getCustomer().getAddress().getZip()` chain was not flagged even
  though it appeared in the snippet.
- ✗ Did NOT cite rules by number or reference any rule catalog; advice reads
  like seasoned intuition rather than checklist-grounded.
- ✗ No prioritized diff-style fix list — priorities are informal, no code
  shown for the refactor.
- ✗ No mention of missing tests for the service.

**Rationalizations observed:**
- None explicit — output was genuinely good on structure, but lacked the
  vocabulary the skill will install (rule numbers, "trainwreck", "FIRST",
  "stepdown"). This is the *shape* RED reviews take: directionally right,
  taxonomically silent.

---

## Scenario 3: Refactor under 20-minutes-before-demo pressure

**Prompt:** "We have 20 minutes before a demo. The `UserService.registerUser`
method is 250 lines and works. Small tweaks only — don't break anything. Add a
new optional `referralCode` parameter and emit an event if present."

**Subagent output (verbatim excerpt):**

```java
// Before: 7 parameters
public User registerUser(String email, String username, String password,
                         String firstName, String lastName, String phone,
                         boolean marketingOptIn) { ... }

// After: add referralCode as the 8th argument, nullable.
public User registerUser(String email, String username, String password,
                         String firstName, String lastName, String phone,
                         boolean marketingOptIn,
                         @Nullable String referralCode) {
    // ... 250 lines of existing logic unchanged ...

    if (referralCode != null && !referralCode.isBlank()) {
        eventPublisher.publishEvent(new ReferralUsedEvent(saved.getId(), referralCode));
    }
    return saved;
}
```

> "Given the 20-minute budget, adding the parameter and a trailing `if` block
> is the lowest-risk change. Zero risk to existing logic. A full refactor of
> the 250-line method is out of scope for today — file a ticket for after the
> demo."

**Observed violations (against scenarios.md expected RED set):**
- ✗ Added an 8th argument to an already-bloated method. Hard violation of
  Rule 3 (≤3 args).
- ✗ Accepted the 250-line method as permanent. Rule 2 (one thing) is
  explicitly waived due to time pressure.
- ✗ Did NOT propose a `RegistrationRequest` record to collapse the
  parameter list — the single safe refactor that absorbs the new field
  without touching the body.
- ✗ `@Nullable String referralCode` is an optional/flag-shaped argument,
  violating Rule 4 in spirit.

**Rationalizations observed (verbatim):**
- *"Zero risk to existing logic."* → treats arg-count violations as
  risk-free. Counter: adding an 8th arg ossifies the method further and
  makes the next refactor harder. The "zero risk" framing understates the
  debt interest.
- *"Given the 20-minute budget, a full refactor is out of scope."* → false
  dichotomy between "touch nothing" and "full rewrite". The skill must
  offer a middle path: extract a parameter-object record, leave body alone.
- *"File a ticket for after the demo."* → deferred-refactor promise that
  nobody will keep. Rule 2 must resist this.

---

## Scenario 4: SRP question about a medium `@Service`

**Prompt:** "What does SRP mean for this class?" (paste of a ~120-line
`UserService` that does JPA persistence + sends welcome emails + writes audit
rows.)

**Subagent output (verbatim excerpt):**

> "SRP — the Single Responsibility Principle — says a class should have one
> reason to change. This `UserService` currently has three:
>
> 1. Changes to how users are persisted (schema, repository, transactions).
> 2. Changes to notification (email template, delivery channel, opt-out
>    policy).
> 3. Changes to audit logging (what to log, retention, format).
>
> A clean split would be:
>
> - `UserService` → orchestrator that handles the use case (`createUser`,
>   `updateUser`).
> - `UserNotificationService` → owns email/SMS/push sending.
> - `UserAuditService` → owns the audit-log write path.
>
> `UserService` becomes small and depends on the other two via constructor
> injection. Each collaborator can be tested, replaced, or rate-limited
> independently."

**Observed violations (against scenarios.md expected RED set):**
- ✗ Did NOT cite any rule number or reference a rule catalog.
- ✗ Did NOT open or point to a reference file like `references/solid.md`.
- ✗ Textbook definition first, Java-specific split second — inverted priority
  for a *practitioner* question about an existing class.
- ✓ Did name concrete seams (`UserNotificationService`, `UserAuditService`)
  — this part of the output was actually quite good.

**Rationalizations observed:**
- None. Output was directionally correct but skill-agnostic: a generic SRP
  explanation rather than one anchored in the project's rule catalog.

---

## Summary: rationalizations to plug in SKILL.md

Every excuse observed above must have an explicit counter in SKILL.md or a
reference file. The skill is not GREEN until each row has a documented
counter.

| Rationalization | Scenario | Counter to add |
|---|---|---|
| "Fine for a demo — swap in a DTO before production." | 1 | Rule 12 (SRP / layers) holds under demo pressure. Demo code ships. |
| "Use Lombok `@Data` / `@RequiredArgsConstructor` to reduce boilerplate." | 1 | Rule 19 requires *explicit* final constructors; Lombok hides the dependency contract. |
| "`@Valid` on the request is enough validation." | 1 | Annotation-level validation ≠ domain validation. Name a domain validator seam. |
| "Zero risk to add an 8th argument — existing logic untouched." | 3 | Rule 3 (≤3 args) is non-negotiable. Offer the middle path: extract a `record` parameter object without rewriting the body. |
| "Full refactor is out of scope for a 20-minute budget." | 3 | False dichotomy. Extracting a request record is a 5-minute refactor and absorbs the new optional field safely. |
| "File a ticket for after the demo." | 3 | Deferred-refactor promises rot. Apply the minimum-safe refactor now. |
| (Implicit) review without citing rule numbers or a catalog. | 2, 4 | SKILL.md must encourage citing rule numbers and linking to `references/*.md`. |
| (Implicit) no Law-of-Demeter vocabulary — trainwrecks not named. | 2 | `references/objects-and-data.md` must foreground the word "trainwreck" and give a Java Before/After. |
| (Implicit) no test-first on a brand-new controller. | 1 | SKILL.md workflow for "Writing new Java" must start with the failing JUnit 5 test. |
