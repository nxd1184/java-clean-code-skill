# Objects and Data

## Core Principle

Objects and data structures serve different purposes. Objects hide their data
behind behavior — you tell them what to do, not how. Data structures expose
their data openly — you compute over them. Mixing the two patterns produces the
worst of both: objects that expose their internals like data structures, forcing
callers to know too much, and data structures burdened with logic that belongs
elsewhere.

Choose deliberately: if a type owns behavior and enforces invariants, make it an
object and hide its fields. If a type is a transparent carrier of values, make it
a data structure (record, DTO) and let callers traverse it freely.

---

## Rules Covered

| Rule | Summary |
|------|---------|
| 10   | Law of Demeter — no trainwreck chains on objects |
| 11   | Objects hide data; expose behavior — tell, don't ask |

---

## Java Examples

### 1. Law of Demeter — trainwreck vs. behavior method (Rule 10)

The Law of Demeter states: a method should only call methods on its direct
collaborators, not on objects returned by those collaborators. Chains of
`.getX().getY().getZ()` on domain objects reveal and depend on hidden structure.

```java
// BAD — trainwreck: caller knows Order has a Customer that has an Address
String zip = order.getCustomer().getAddress().getZipCode();

// GOOD — behavior method: caller asks the object; implementation stays hidden
String zip = order.customerZipCode();
```

`Order.customerZipCode()` can change how it stores or delegates the zip code
without touching any caller. The trainwreck version breaks everywhere the moment
the internal structure shifts.

---

### 2. Demeter on records is fine (Rule 10)

Records are explicitly data structures — they are designed to be traversed.
Applying the Law of Demeter to record chains is overcorrection.

```java
// GOOD — chaining on records / data structures is acceptable
record Address(String street, String zipCode) {}
record Customer(String name, Address address) {}
record OrderDto(String id, Customer customer) {}

String zip = dto.customer().address().zipCode(); // fine — it's data, not an object
```

The distinction: `Order` (a domain object with business rules) vs. `OrderDto`
(a transparent value carrier). The law applies to objects; it does not apply
to data.

---

### 3. Hide data with behavior — tell, don't ask (Rule 11)

Anemic models expose raw fields through getters and push all logic into service
classes. This scatters behavior, weakens encapsulation, and forces callers to
make decisions the object should make itself.

```java
// BAD — anemic: getters everywhere, logic lives outside the class
class Order {
    private String status;
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// Caller must know what "SHIPPED" means and when it's valid:
if (order.getStatus().equals("SHIPPED")) { ... }
```

```java
// GOOD — behavior: tell, don't ask
class Order {
    private OrderStatus status;

    public boolean isShipped() {
        return status == OrderStatus.SHIPPED;
    }

    public void ship() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Can only ship a pending order");
        }
        this.status = OrderStatus.SHIPPED;
    }
}

// Caller asks the object what it knows and tells it what to do:
if (order.isShipped()) { ... }
order.ship();
```

The object now owns its invariant ("only ship when PENDING"). No caller can
accidentally skip the guard, and the status field stays private.

---

### 4. DTO records as first-class data structures (Rule 11 + Java 17)

Records signal intent: this type is data, not behavior. No need to add methods
beyond what records provide automatically.

```java
// GOOD — records are explicitly data structures; chaining is fine; no behavior needed
public record OrderDto(String id, long totalCents, String customerEmail) {}
```

Spring controllers, OpenAPI responses, and inter-service payloads are natural
record territory. Resist the urge to add `isExpensive()` or similar domain logic
to a DTO — keep that in the domain object.

---

## Common Traps

**"Getters are behavior."**
A getter just exposes the field under a different name. Real behavior
encapsulates logic: `order.isShipped()` is behavior; `order.getStatus()` is not.

**"One chain is fine."**
Chains accumulate. One `a.getB().getC()` becomes three next sprint. Establish
the boundary early with a behavior method and the accumulation never happens.

**"I'll add the behavior method later."**
Anemic models compound. Each new feature adds another caller that knows about
internal structure. Refactor when the class is first designed — the cost is
trivial then, expensive after.

**"Records with logic are still records."**
Adding domain logic to a record turns it into a hybrid that is neither a clean
data structure nor a proper object. Keep records pure data; put behavior in
dedicated domain classes.

---

## When the Rules Don't Apply

**Fluent builders.**
`User.builder().email(e).name(n).build()` — each call returns the builder
itself, not a domain object. This is not navigating an object graph; it is
configuring a factory.

**Stream pipelines.**
`.filter(...).map(...).collect(...)` — operating on a data stream, not
traversing domain objects. Stream combinators are not trainwrecks.

**Record / DTO chains.**
Explicitly data structures; chains are expected and correct. See Example 2.

**Value objects.**
`money.currency().code()` where both `Money` and `Currency` are value objects
(immutable, no side effects) is acceptable. Value objects blur the line between
data and object intentionally.

---

## Cross-References

- **SKILL.md rules:** 10 (Law of Demeter), 11 (hide data; expose behavior).
- **Related:** [classes-and-boundaries.md](classes-and-boundaries.md) — boundary
  patterns that keep external shapes out of domain objects.
- **Related:** [solid.md](solid.md) — SRP: anemic models often signal an SRP
  violation at the service layer, where services absorb all the logic that
  should live in the domain object.

---

## Effective Java additions

### Items 10/11/12: equals, hashCode, toString contracts

If you override `equals`, you MUST override `hashCode` (or hash-based
collections break). `toString` should be human-readable for debugging.

**The equals contract** (Item 10):
- **Reflexive:** `x.equals(x)` is true.
- **Symmetric:** `x.equals(y)` ⟺ `y.equals(x)`.
- **Transitive:** if `x.equals(y)` and `y.equals(z)`, then `x.equals(z)`.
- **Consistent:** repeated calls return the same result (no random state).
- **Non-null:** `x.equals(null)` is false.

The contract is hard to satisfy when subclassing — adding a field in a
subclass usually breaks symmetry or transitivity.

**Modern Java fix:** use a `record`. Records auto-generate equals,
hashCode, and toString from the components. They're final by design, which
sidesteps the subclassing problem.

```java
// BEFORE — easy to get wrong
public class Point {
    private final int x, y;
    public Point(int x, int y) { this.x = x; this.y = y; }
    // missing equals/hashCode/toString
}

// AFTER — record gives you all three for free
public record Point(int x, int y) {}
```

If you can't use a record (existing class hierarchy, mutable state), use
your IDE's generator and verify the contract in a unit test.

### Item 17: Minimize mutability + Item 50: Defensive copies

Immutable classes are simpler to reason about, thread-safe by default, and
freely shareable. Five rules: no setters; class final; all fields `final`
and private; defensive copies for any mutable component (Item 50).

Java 17 records satisfy rules 1–4 automatically. Rule 5 still applies for
mutable components (`Date`, `List`).

```java
public record Period(Date start, Date end) {
    public Period {
        start = new Date(start.getTime());     // copy in (TOCTOU-safe)
        end   = new Date(end.getTime());
        if (start.after(end))
            throw new IllegalArgumentException("start > end");
    }
    public Date start() { return new Date(start.getTime()); }   // copy out
    public Date end()   { return new Date(end.getTime()); }
}
```

Always copy *before* validating — otherwise a caller can mutate the
parameter between the check and the assignment. Return mutable internals
as unmodifiable views (`List.copyOf(list)`).

### Item 54: Return empty collections, not null

Returning `null` forces every caller to null-check. Empty singletons
compose with for-each, streams, and `addAll` cleanly.

```java
// BEFORE
return cheesesInStock.isEmpty() ? null : new ArrayList<>(cheesesInStock);

// AFTER
return List.copyOf(cheesesInStock);   // empty list if empty
```

### Item 55: Return Optionals judiciously

`Optional<T>` signals "this might legitimately be absent". Use it for
**return types** where absence is part of the contract — never for fields,
parameters, or collection elements (`List<Optional<T>>` is wrong).

```java
public Optional<User> findByEmail(String email) { /* ... */ }

userRepo.findByEmail(email).map(User::name).orElse("anonymous");
```

Avoid `Optional.get()` without `isPresent()` — `orElse`, `orElseThrow`, and
`ifPresent` are the safe forms.
