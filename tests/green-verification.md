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
