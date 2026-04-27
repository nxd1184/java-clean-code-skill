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

---

## EJ-merge RED baselines (captured 2026-04-27)

Each of the 10 EJ-merge scenarios was run against a fresh general-purpose
subagent with NO skill loaded. Verbatim outputs below. The skill content
written in subsequent tasks must address what these baselines reveal —
specifically, the consistent absence of EJ item number+title citations and
the partial coverage of fixes a calibrated skill would propose.

### Scenario 5 — Constructor with five parameters

**Subagent output (verbatim):**

```
**Code Review: Pizza Constructor**

This class has a classic **boolean parameter problem**. When callers invoke this constructor, they get a wall of unnamed booleans:

    new Pizza("large", true, false, true, false, true)

It's impossible to tell what each `true`/`false` means without jumping to the signature. That's a readability and maintainability risk.

**Suggestion: Use a builder pattern or an enum for toppings.**

Option 1 — Builder (cleaner for future expansion):
    Pizza pizza = new Pizza.Builder("large")
        .withCheese(true)
        .withPepperoni(false)
        .withMushrooms(true)
        .build();

Option 2 — Enum set (if toppings are fixed):
    Pizza pizza = new Pizza("large",
        Set.of(Topping.CHEESE, Topping.MUSHROOMS));

The builder approach scales better as toppings grow. It also makes it obvious *which* toppings are requested and eliminates positional confusion. If you're keeping the constructor, at least extract the booleans into a `Toppings` value object so the signature reads naturally.
```

**Observed gaps:**
- Did the subagent mention Builder pattern? **yes** (suggested as Option 1).
- Did it cite EJ Item 2 by number+title? **no**.
- Did it suggest a `record`? **no** — proposed an `EnumSet`-style constructor instead.
- Did it mention ≤3 args / no flag args explicitly? **partial** — used the phrase "boolean parameter problem" but no rule citation.

### Scenario 6 — Raw type ArrayList

**Subagent output (verbatim):**

```
# Code Review

A few issues here:

**Type safety**: This ArrayList is raw-typed, which defeats Java's generics. You're mixing strings and integers, forcing everything through `Object`. At runtime, if you later try to cast an element assuming it's a String, you'll get a ClassCastException. Use `ArrayList<String>` (or whatever the actual type should be) and let the compiler catch errors early.

**Unnecessary toString()**: You're calling `.toString()` on every Object, but `println()` already does that. Just pass the object directly: `System.out.println(o);`

**List vs ArrayList**: Unless you specifically need ArrayList's mutability or random access, declare it as `List<String> list = new ArrayList<>();`. This gives you flexibility to swap implementations later without changing call sites.

**Practical fix**:
    List<String> list = new ArrayList<>();
    list.add("hello");
    list.add("42");  // or parse if it should be an int
    for (String item : list) {
        System.out.println(item);
    }

This is type-safe, clearer, and follows standard Java idioms. If you really need mixed types, that's a design smell—reconsider why the list contains heterogeneous data.
```

**Observed gaps:**
- Did the subagent cite EJ Item 26 by number? **no**.
- Did it explain WHY raw types are dangerous (compiler-erased type checks, ClassCastException at runtime)? **yes** — explicitly mentioned ClassCastException.

### Scenario 7 — Manual for-loop

**Subagent output (verbatim):**

```
This is a classic case where you should leverage Java's Stream API instead of manual iteration. Here's what I'd suggest:

**Current issues:**
1. **Verbose iteration** — The index-based loop is unnecessary when you already have a List
2. **Mutable state** — You're building up a collection imperatively instead of expressing intent declaratively
3. **Readability** — Takes 3 lines to do what should be 1

**Better approach:**
    List<String> upper = names.stream()
        .map(String::toUpperCase)
        .collect(Collectors.toList());

Or if you want it even more concise:
    List<String> upper = names.stream()
        .map(String::toUpperCase)
        .toList();  // Java 16+ immutable list

**Why this matters:**
- The stream version clearly communicates "transform each name to uppercase"
- It's less error-prone (no off-by-one bugs, clearer intent)
- For a read-heavy codebase, this pattern becomes instantly recognizable
- If you later need filtering or chaining operations, streams scale better
```

**Observed gaps:**
- Did it suggest streams or method references? **yes** — both shown.
- Did it cite EJ Items 42/43/45 by number? **no**.
- Did it warn against over-using streams (Item 45 caveat)? **no** — actually advocated streams unconditionally.

### Scenario 8 — Catch-and-ignore

**Subagent output (verbatim):**

```
**Code Review: Exception Handling**

This catch block swallows the exception silently, which is problematic. Here's why:

**The issue:** If `loadConfig()` fails, your application continues as if nothing happened. You've likely got uninitialized config state downstream, leading to cryptic failures elsewhere that are hard to trace back to the root cause.

**Better approaches:**

1. **Log it** – At minimum, log the exception so you have visibility into what went wrong:
       catch (IOException e) {
           logger.warn("Failed to load configuration", e);
       }

2. **Fail fast** – If config is critical, rethrow or wrap in a RuntimeException:
       catch (IOException e) {
           throw new RuntimeException("Failed to load config", e);
       }

3. **Provide a fallback** – If there's a sensible default behavior:
       catch (IOException e) {
           logger.warn("Config not found, using defaults", e);
           loadDefaults();
       }

**The rule of thumb:** Never silently ignore checked exceptions. Empty catch blocks are a red flag in code reviews—they hide bugs and make debugging much harder.
```

**Observed gaps:**
- Did it cite EJ Item 77 by number? **no**.
- Did it offer a concrete remediation (log? rethrow? wrap?)? **yes** — three concrete options.

### Scenario 9 — Missing equals/hashCode

**Subagent output (verbatim):**

```
# Code Review: User Class

**Concern: Missing `hashCode()` and `equals()` implementation**

If this class is going into a `Set<>`, you need to override both methods. Right now, `User` will use object identity for equality, which means two users with the same `id` and `name` will be treated as different objects in the set. That's almost certainly not what you want.

Implement `equals()` based on `id` (or `id` + `name` if name isn't guaranteed unique), and make sure `hashCode()` is consistent with it. A common pattern:

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

**Minor note:** Consider making `id` a `String` or `UUID` unless you're auto-generating numeric IDs from a database.
```

**Observed gaps:**
- Did it cite EJ Items 10 + 11 by number? **no**.
- Did it suggest record conversion (the modern fix)? **no** — gave the manual override pattern instead.

### Scenario 10 — ArrayList in method signature

**Subagent output (verbatim):**

```
**Code Review**

A few observations:

**1. Return type preference**
`ArrayList` is a concrete implementation detail. Return `List<Customer>` instead—it's more flexible for callers and follows the interface-segregation principle. If this method ever needs to swap implementations (e.g., to `LinkedList` for specific performance characteristics), callers won't break.

**2. Nullability**
The return type doesn't signal whether null is possible. If an empty result is expected, return `Collections.emptyList()` or `List.of()` rather than null.

**3. Parameter validation**
What happens if `region` is null or blank? Consider validating early and throwing `IllegalArgumentException` with a clear message.

**4. Method naming**
`findCustomers` is clear, but consider whether `getCustomersByRegion` or `searchCustomers(String region)` better describes the intent.
```

**Observed gaps:**
- Did it cite EJ Item 64 by number? **no** — gestured at "interface-segregation principle" instead (which is technically wrong; the right principle is DIP / refer-by-interface).
- Did it explain that users get coupled to ArrayList implementation details? **partial** — said "concrete implementation detail" without naming the specific risk.

### Scenario 11 — `new Thread().start()`

**Subagent output (verbatim):**

```
This looks like a threading issue waiting to happen.

**Main concern**: You're spawning raw threads for background work, which is a management nightmare. Threads are expensive resources—if this gets called frequently, you'll exhaust the thread pool and crash. There's also no error handling, so if `saveResult()` throws an exception, it silently dies in the background with no visibility.

**Better approach**: Use Spring's `@Async` or inject a `TaskExecutor`. This gives you proper thread pooling, monitoring, and lifecycle management out of the box:

    @Async
    public void generateReport(Long id) {
        saveResult(id);
    }

**Secondary issue**: No error handling or logging. If something fails, how will you know? Consider wrapping the work in a try-catch or using a `ListenableFuture` / `CompletableFuture` so callers can react to failures.
```

**Observed gaps:**
- Did it cite EJ Item 80 by number? **no**.
- Did it mention error swallowing in fire-and-forget threads? **yes**.
- Did it offer Spring-idiomatic remediation (`@Async`, `TaskExecutor`)? **yes** — both.

### Scenario 12 — `data` of type `Map<String,Object>`

**Subagent output (verbatim):**

```
This method signature is too vague for a production codebase. Here are my concerns:

**Method name**: `handle` tells me nothing. What's being handled? Is this processing a request, validating data, persisting records? Use something like `processUserInput`, `validateConfiguration`, or `persistMetadata`.

**Parameter**: A raw `Map<String, Object>` is a code smell. You're losing type safety and self-documentation. What keys does this map expect? What types should the values be? Six months from now, someone (maybe you) will have to dig through the implementation to figure it out.

**Return type**: A bare boolean is ambiguous. Does `true` mean "success" or "should retry"?

**Better approach**: Define a domain object (e.g., `UserRegistrationRequest`) instead of `Map`. If you genuinely need flexibility, document the expected structure in a constant or separate validation method.
```

**Observed gaps:**
- Did it cite EJ Item 68 by number? **no**.
- Did it propose a typed `record` substitute? **partial** — said "domain object" but didn't name `record` specifically.

### Scenario 13 — Premature optimization comment

**Subagent output (verbatim):**

```
Good code. A few thoughts:

**What works:**
- Static final immutable map is the right choice here. `Map.of()` is clean and prevents accidental mutation.
- The performance comment is helpful—it signals "this was measured, not premature optimization."
- Simple, readable method that does one thing.

**Minor considerations:**

1. **Null handling**: `get(n)` returns null for unmapped keys. Depending on your contract, consider `getOrDefault(n, "unknown")` or throw an exception if an invalid number is a genuine error condition.

2. **Scope of the cache**: If this cache only serves this one method, it's fine.

3. **Completeness**: The map only covers 1–3.
```

**Observed gaps:**
- Did it cite EJ Item 67 by number? **no**.
- Did it identify the comment as wrong-shape (claims without evidence)? **NO — actively WRONG**: said "the performance comment is helpful—it signals 'this was measured'." The comment makes that claim *without* showing any profiling data; the subagent fell for the assertion. This is the most striking RED finding: without the skill, the reviewer trusts unverified performance claims.

### Scenario 14 — Missing Javadoc on public method

**Subagent output (verbatim):**

```
**Code Review: PaymentService.chargeCard()**

This method is handling sensitive payment operations, which raises several concerns:

**Parameter Design**
Taking `cardToken` as a String is risky—there's no type safety or validation wrapper. Consider creating a `CardToken` value object that enforces non-empty, properly-formatted tokens and prevents accidental logging.

**Missing Error Handling**
What happens if the payment gateway is down? Does this throw, timeout, or retry? The return type `PaymentResult` suggests you're handling failures gracefully, but the method signature doesn't hint at what exceptions callers should expect.

**Lack of Idempotency**
Payment operations must be idempotent. Without seeing the implementation, I'd want to know: does this method generate an idempotency key?

**Audit Trail**
This is a financial operation. Are calls being logged?

**Suggestions**
- Extract `CardToken`, `Money` (amount + currency tuple) as domain objects
- Document expected exceptions and retry behavior
- Add a request ID or idempotency key parameter
```

**Observed gaps:**
- Did it cite EJ Item 56 by number? **no**.
- Did it propose `@param`, `@return`, `@throws` tags? **partial** — said "Document expected exceptions and retry behavior" but didn't propose the Javadoc tag form. The reviewer pivoted into payment-domain concerns (idempotency, audit) instead of the documentation gap, which is a different (and valid) angle but misses the explicit doc-comment requirement.

---

### Aggregate finding

Across all 10 scenarios:

- **0 of 10** cited any EJ item by number+title (universal absence — the discriminator the skill must add).
- **6 of 10** subagents reached an *adjacent* fix (Builder, streams, log-on-catch, override-equals, interface return, executor) without naming the source.
- **2 of 10** missed entirely or got the principle wrong (Scenario 13: trusted the unverified perf claim; Scenario 10: invoked "interface-segregation" instead of DIP / refer-by-interface).
- **2 of 10** pivoted to a different (also-valid) concern (Scenario 14: payment domain instead of doc comments; Scenario 9: id type instead of record conversion).

The skill content for the EJ merge must, at minimum:
1. Surface EJ item numbers + titles so reviews carry the citation.
2. Warn explicitly that a "// cached for performance" comment without
   accompanying profiling data is *itself* the smell.
3. Push the modern Java idiom (record over manual equals/hashCode;
   `List.of()` over `Collections.emptyList()`).
4. Carry the framework-idiomatic remediation (Spring `@Async` /
   `TaskExecutor`) where applicable.

