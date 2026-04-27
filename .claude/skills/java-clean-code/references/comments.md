# Comments

## Core principle

Comments fail when they restate the code; they succeed when they explain the WHY. If you need a comment to explain what the code does, the code needs to be rewritten. If you need a comment to explain why the code does it — write the comment.

## Rules covered here

- **Rule 20: Comments earn their keep.** Every comment must add information the code cannot express itself. If deleting a comment loses nothing, delete it.

## Java examples

### ❌ Bad — redundant (explains the what, not the why)

```java
// increment i by 1
i++;
```

The comment duplicates the code exactly. Anyone reading Java can see `i++`. Delete it.

### ❌ Bad — stale risk (TODO without a ticket or owner)

```java
// TODO: refactor this in Q2
```

"Q2" of which year? Who owns it? No ticket number, no owner, no signal that it was ever done. These accumulate and rot. Use your issue tracker instead.

### ✅ Good — explains WHY, non-obvious constraint

```java
// Fixed-size thread pool = DB connection-pool size - 1, to avoid starving the
// migration worker that shares the pool. See ops incident 2024-11-03.
var workers = Executors.newFixedThreadPool(dbPoolSize - 1);
```

The code alone cannot tell you why `dbPoolSize - 1`. The comment explains the invariant, the risk, and provides an audit trail.

### ✅ Good — public API Javadoc with contract

```java
/**
 * @return never null; empty list if no matches.
 * @throws IllegalArgumentException if {@code status} is null.
 */
public List<Order> findByStatus(OrderStatus status) { ... }
```

Javadoc for public APIs documents the contract: what callers can rely on, what they must not pass in. This is information the signature alone cannot convey.

### ❌ Bad — commented-out code

```java
// List<User> users = userDao.findAllLegacy(true);
List<User> users = userRepository.findAll();
```

The commented-out line is noise. If it is needed, version control has it. If it is not needed, delete it. "We might need this later" is a bet you almost always lose.

### ❌ Bad — section banner comments

```java
// ===== GETTERS =====

public String getName() { return name; }

public int getAge() { return age; }

// ===== SETTERS =====

public void setName(String name) { this.name = name; }
```

Banners signal that the class is too large and relies on visual anchors instead of good structure. Break the class up or use IDE folding — banners are a smell, not a solution. If naming is good, sections are obvious.

## Common traps

- **Stale comments.** The code changed; the comment didn't. Readers trust the comment, get misled, waste time. Comments must be maintained exactly like code.
- **Commented-out code ("we might need this later").** This is what Git is for. Dead code in comments confuses future readers and never gets cleaned up.
- **Section banners (`//===== GETTERS =====`).** They signal a class that is too large. Fix the design; don't wallpaper over it.
- **TODO comments without a ticket or date.** They become permanent fixtures. Always attach a ticket number: `// TODO: JIRA-1234 — remove after migration`.
- **Paraphrasing the code.** `// set x to 5` above `x = 5` adds nothing and doubles the maintenance surface.
- **Apology comments.** `// I know this is ugly but...` — either fix it or file a ticket. Apologies don't help future readers.

## When the rule doesn't apply

- **Public API Javadoc documenting contracts.** `@return never null`, `@throws IllegalArgumentException if X` — these are contracts that the method signature cannot encode. They belong in the source.
- **Non-obvious invariants or historical constraints.** Performance hacks, regulatory constraints, workarounds for known third-party bugs, and system-level interactions (thread-pool sizing, lock ordering) all justify a comment with a reference.
- **License headers.** Required by many open-source licenses; don't remove them.
- **Regular expressions or bit-manipulation.** Even experienced developers benefit from a one-line plain-English summary of what the pattern matches or what the bitmask represents.

## Cross-references

- SKILL.md rule: 20
- Related: [naming.md](naming.md) — names often make comments redundant; if you need a comment to explain a variable, rename it first.

---

## Effective Java additions

### Item 56: Write doc comments for all exposed API elements

Rule 20 says comments earn their keep — and that "WHY not WHAT" is the
test. **Public API surface is a deliberate exception:** doc comments at
the API are part of the contract, not just help-the-reader prose.

A method's doc comment must say:

- **What it does**, not *how* (callers don't see the body).
- **Preconditions** (`@param` if a constraint exists).
- **Postconditions** (`@return` if non-trivial).
- **Side effects** (mention them — even one is worth a sentence).
- **Failure modes** (`@throws` for each declared exception, including
  unchecked when relevant).

```java
/**
 * Charges the given payment method for the order's total.
 *
 * <p>Idempotent: charging the same {@code orderId} twice never charges
 * the card twice.
 *
 * @param orderId   the order to charge; must be persisted and unfulfilled
 * @param cardToken the tokenised payment method (PCI-DSS scope)
 * @return a {@link ChargeReceipt} on success; never null
 * @throws InsufficientFundsException if the card balance is below the order total
 * @throws PaymentGatewayException    if the gateway is unavailable
 * @implSpec the method holds a transaction open for the duration of the
 *           gateway call; callers should not nest in another transaction
 */
public ChargeReceipt charge(OrderId orderId, String cardToken) { /* ... */ }
```

**Tags worth knowing:**
- `@param`, `@return`, `@throws` — the basics.
- `@implSpec` — what subclasses can rely on.
- `@code` — inline code (escapes HTML).
- `@literal` — like `@code` but no monospace; use for `<` and `>` in prose.
- `@deprecated` — paired with `@Deprecated` annotation; explain the
  alternative.

### When NOT to write doc comments

- **Private methods** — Rule 20 still applies; readers can read the body.
- **Trivial getters/setters** — auto-generated noise; the IDE reveals types.
- **`@Override` methods** with no behavior change — inherit the parent's doc.

Reinforces **Rule 20** above. Compact rule: WHY > WHAT, but for *public*
APIs, both must appear because the WHAT is the contract.
