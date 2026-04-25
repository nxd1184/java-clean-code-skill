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
