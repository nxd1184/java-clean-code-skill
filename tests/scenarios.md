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

---

## EJ-merge scenarios (added 2026-04-27)

These scenarios validate the umbrella behavior of each new/expanded reference
after the Effective Java 3rd Edition merge. One scenario per reference; each
covers the highest-impact EJ items in that file.

### Scenario 5: Constructor with five parameters — advise builder/factory

**Prompt to subagent:**

> Review this Java class and suggest an improvement using the
> `java-clean-code` skill:
>
> ```java
> public class Pizza {
>     private final String size;
>     private final boolean cheese;
>     private final boolean pepperoni;
>     private final boolean mushrooms;
>     private final boolean olives;
>
>     public Pizza(String size, boolean cheese, boolean pepperoni,
>                  boolean mushrooms, boolean olives) { /* ... */ }
> }
> ```

**GREEN criteria:**
- Cites Rule 3 (≤3 args) AND Rule 4 (no flag args).
- Cites EJ Item 2 (Builder when many params) by number+title.
- Suggests Builder OR a `record Pizza(String size, Set<Topping> toppings)`.
- Cross-links to `references/creating-objects.md`.

### Scenario 6: Raw type ArrayList in code review

**Prompt to subagent:**

> Review using the `java-clean-code` skill:
>
> ```java
> ArrayList list = new ArrayList();
> list.add("hello");
> list.add(42);
> for (Object o : list) { System.out.println(o.toString()); }
> ```

**GREEN criteria:**
- Cites EJ Item 26 (no raw types) by number+title.
- Says `List<Object>` is also wrong here; recommends `List<String>` if intent is strings.
- Cross-links to `references/generics.md`.

### Scenario 7: Manual for-loop transforming a list

**Prompt to subagent:**

> Improve this with the `java-clean-code` skill:
>
> ```java
> List<String> upper = new ArrayList<>();
> for (int i = 0; i < names.size(); i++) {
>     upper.add(names.get(i).toUpperCase());
> }
> ```

**GREEN criteria:**
- Suggests `names.stream().map(String::toUpperCase).toList()` OR a method reference equivalent.
- Cites EJ Item 42 (lambdas) AND Item 43 (method refs) by number+title.
- Notes Item 45 caveat (streams judiciously) — for trivial transforms, either form is fine.
- Cross-links to `references/lambdas-and-streams.md`.

### Scenario 8: Catch-and-ignore exception block

**Prompt to subagent:**

> Review using the `java-clean-code` skill:
>
> ```java
> try {
>     loadConfig();
> } catch (IOException e) {
>     // ignored
> }
> ```

**GREEN criteria:**
- Cites Rule 9 (exceptions over error codes) AND EJ Item 77 (don't ignore).
- Suggests at minimum: log the exception, OR rethrow as a runtime exception with context.
- Cross-links to `references/exceptions.md`.

### Scenario 9: Class with `int id` and `String name` lacking equals/hashCode

**Prompt to subagent:**

> A class is being added to a `Set<>`. Review using the `java-clean-code` skill:
>
> ```java
> public class User {
>     private final int id;
>     private final String name;
>
>     public User(int id, String name) {
>         this.id = id;
>         this.name = name;
>     }
> }
> ```

**GREEN criteria:**
- Identifies missing `equals` and `hashCode`. Cites EJ Items 10 + 11 by number+title.
- Suggests converting to `record User(int id, String name)` (Java 17, generates equals/hashCode/toString).
- Cross-links to `references/objects-and-data.md`.

### Scenario 10: ArrayList declared as concrete type in a method signature

**Prompt to subagent:**

> Review using the `java-clean-code` skill:
>
> ```java
> public ArrayList<Customer> findCustomers(String region) {
>     // ...
> }
> ```

**GREEN criteria:**
- Cites Rule 19 (DIP) AND EJ Item 64 (refer to objects by interfaces) by number+title.
- Suggests `List<Customer>` return type.
- Cross-links to `references/classes-and-boundaries.md`.

### Scenario 11: Background work via `new Thread(...).start()`

**Prompt to subagent:**

> Review using the `java-clean-code` skill:
>
> ```java
> @Service
> public class ReportService {
>     public void generateReport(Long id) {
>         new Thread(() -> {
>             // expensive work
>             saveResult(id);
>         }).start();
>     }
> }
> ```

**GREEN criteria:**
- Cites EJ Item 80 (executors over threads) by number+title.
- Suggests `@Async` + `TaskExecutor`, OR an injected `ExecutorService`, OR Spring's
>  `@Scheduled`/`CompletableFuture`.
- Mentions that a fire-and-forget Thread leaks errors (Rule 9 / EJ Item 77).
- Cross-links to `references/concurrency.md`.

### Scenario 12: Variable named `data` of type `Map<String, Object>`

**Prompt to subagent:**

> Review using the `java-clean-code` skill:
>
> ```java
> public boolean handle(Map<String, Object> data) { /* ... */ }
> ```

**GREEN criteria:**
- Cites Rule 1 (names reveal intent) AND EJ Item 68 (naming conventions) by number+title.
- Cites Rule 11 (objects hide data) — `Map<String,Object>` is anti-pattern.
- Suggests a typed `record` and a verb+noun method name.
- Cross-links to `references/naming.md`.

### Scenario 13: Premature optimization comment block

**Prompt to subagent:**

> Review using the `java-clean-code` skill:
>
> ```java
> // Cached for performance — DO NOT REMOVE without profiling
> private static final Map<Integer, String> NUMBERS_AS_WORDS = Map.of(
>     1, "one", 2, "two", 3, "three"
> );
>
> public String numberToWord(int n) {
>     return NUMBERS_AS_WORDS.get(n);
> }
> ```

**GREEN criteria:**
- Cites EJ Item 67 (optimize judiciously) by number+title.
- Notes the cache is fine but the comment is wrong-shape: needs evidence (profiling
>  data) or removal.
- Cross-links to `references/craftsmanship.md`.

### Scenario 14: Public method on a service class with no Javadoc

**Prompt to subagent:**

> Review using the `java-clean-code` skill:
>
> ```java
> @Service
> public class PaymentService {
>     public PaymentResult chargeCard(String cardToken, BigDecimal amount, String currency) {
>         // ...
>     }
> }
> ```

**GREEN criteria:**
- Cites Rule 20 (comments earn their keep) AND EJ Item 56 (doc comments for exposed
>  APIs) by number+title.
- Suggests a Javadoc with `@param`, `@return`, `@throws` for the public API surface.
- Notes Rule 20's "WHY not WHAT" still applies — the Javadoc explains the contract,
>  not the implementation.
- Cross-links to `references/comments.md`.
