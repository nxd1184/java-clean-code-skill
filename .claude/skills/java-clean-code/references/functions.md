# Functions

## Core principle
Functions are the smallest unit of abstraction. The goal: a reader should grasp what a function does from its name alone, then trust its body to match.

## Rules covered here
| Rule | Summary |
|---|---|
| 2 | Functions do one thing |
| 3 | ≤3 arguments |
| 4 | No flag (boolean) arguments |
| 5 | Command-Query Separation |
| 6 | One abstraction level per function |
| 8 | No hidden side effects |
| 9 | Exceptions over error codes |
| 13 | DRY |
| 18 | Early returns over deep nesting |

---

## Rule 2 — One thing

**Before** — validates, persists, and emails in a single body:
```java
public void registerUser(User user) {
    if (user.getEmail() == null || !user.getEmail().contains("@"))
        throw new IllegalArgumentException("Invalid email");
    if (userRepository.existsByEmail(user.getEmail()))
        throw new IllegalStateException("Email already registered");
    userRepository.save(user);                         // persistence
    emailService.send(user.getEmail(), "Welcome",      // notification
        buildWelcomeBody(user));
}
```

**After** — thin coordinator delegates to focused helpers:
```java
public void registerUser(User user) {
    validateNewUser(user);
    userRepository.save(user);
    sendWelcomeEmail(user);
}
```

---

## Rule 3 — ≤3 arguments → record parameter object

**Before** — 5 positional strings, easy to transpose:
```java
public User createUser(String email, String username, String password,
                       String firstName, String lastName) { ... }
```

**After** — Java 17+ record collapses args into a named value object:
```java
public record CreateUserRequest(String email, String username, String password,
                                String firstName, String lastName) {}

public User createUser(CreateUserRequest request) { ... }
```

> Java 17+ records are the idiomatic way to collapse args — immutable by default, with `equals`/`hashCode`/`toString` generated automatically.

---

## Rule 4 — No boolean flag arguments

**Before** — caller must know what `true` means:
```java
void sendNotification(User user, boolean isUrgent) {
    if (isUrgent) smsGateway.send(...);
    else          emailService.send(...);
}
sendNotification(user, true);  // what does true mean?
```

**After** — two named methods:
```java
void sendNotification(User user)       { emailService.send(...); }
void sendUrgentNotification(User user) { smsGateway.send(...); }
```

---

## Rule 5 — Command-Query Separation (CQS)

A method either changes state (command) **or** returns information (query).

**Before** — validates AND mutates AND returns a computed result in one call:
```java
// Caller can't tell if this is a check or a mutation
OrderSummary processAndSummarize(Order order) {
    order.setStatus(PROCESSED);             // mutation
    orderRepository.save(order);            // side effect
    return summaryBuilder.build(order);     // query
}
```

**After** — command and query split:
```java
void process(Order order) {                              // command only
    order.setStatus(PROCESSED);
    orderRepository.save(order);
}
OrderSummary summarize(Order order) {                    // query only
    return summaryBuilder.build(order);
}
```

---

## Rule 6 — One abstraction level per function

**Before** — high-level orchestration mixed with low-level string work:
```java
public void processOrder(Order order) {
    validateOrder(order);
    inventory.deduct(order);
    StringBuilder sb = new StringBuilder();           // low-level detail
    sb.append(order.getId()).append(",")
      .append(order.getCustomer().getEmail()).append(",")
      .append(order.getTotal().toPlainString());
    auditLog.write(sb.toString());
}
```

**After** — all steps at the same level; detail extracted:
```java
public void processOrder(Order order) {
    validateOrder(order);
    inventory.deduct(order);
    auditLog.write(toAuditRecord(order));  // detail hidden behind a name
}

private String toAuditRecord(Order order) {
    return order.getId() + "," + order.getCustomer().getEmail()
           + "," + order.getTotal().toPlainString();
}
```

---

## Rule 8 — No hidden side effects

**Before** — name implies a pure check; body secretly writes to a log:
```java
boolean isValidEmail(String email) {
    boolean valid = email != null && email.contains("@");
    if (!valid) auditLogger.warn("Invalid email: " + email);  // hidden I/O
    return valid;
}
```

**After — option A:** remove the side effect; keep the pure check:
```java
boolean isValidEmail(String email) {
    return email != null && email.contains("@");
}
```
**After — option B:** rename to advertise the side effect:
```java
boolean validateEmailAndLog(String email) {
    boolean valid = email != null && email.contains("@");
    if (!valid) auditLogger.warn("Invalid email: " + email);
    return valid;
}
```

---

## Rule 9 — Exceptions over error codes

**Before** — null is a silent error code callers can silently ignore:
```java
User findUser(Long id) { return userRepository.findById(id).orElse(null); }
// Caller: user.getEmail() → NullPointerException
```

**After** — typed exception forces acknowledgment:
```java
User findUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
}

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) { super("User not found: " + id); }
}
```

> Use `Optional<User>` when "not found" is a **normal** outcome (e.g., a search endpoint). Use an exception when absence is unexpected.

---

## Rule 13 — DRY

**Before** — identical validation blocks copied into two methods; one will drift:
```java
public void createProduct(ProductRequest r) {
    if (r.getName() == null || r.getName().isBlank()) throw ...;
    if (r.getPrice().signum() < 0) throw ...;
    // create logic
}
public void updateProduct(Long id, ProductRequest r) {
    if (r.getName() == null || r.getName().isBlank()) throw ...;  // duplicate
    if (r.getPrice().signum() < 0) throw ...;                     // duplicate
    // update logic
}
```

**After** — single source of truth:
```java
private void validateProductRequest(ProductRequest r) {
    if (r.getName() == null || r.getName().isBlank()) throw ...;
    if (r.getPrice().signum() < 0) throw ...;
}
public void createProduct(ProductRequest r) { validateProductRequest(r); ... }
public void updateProduct(Long id, ProductRequest r) { validateProductRequest(r); ... }
```

---

## Rule 18 — Early returns (guard clauses)

**Before** — 4 levels of nesting; happy path is buried:
```java
public String processPayment(Order order) {
    if (order != null) {
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            if (order.getCustomer() != null) {
                if (order.getCustomer().isActive()) {
                    return paymentGateway.charge(order);
                } else { return "ERROR: inactive customer"; }
            } else { return "ERROR: missing customer"; }
        } else { return "ERROR: empty order"; }
    } else { return "ERROR: null order"; }
}
```

**After** — guard clauses exit early; happy path stays at column 0:
```java
public String processPayment(Order order) {
    if (order == null)                              throw new IllegalArgumentException("Order required");
    if (order.getItems() == null || order.getItems().isEmpty()) throw new IllegalArgumentException("No items");
    if (order.getCustomer() == null)                throw new IllegalStateException("No customer");
    if (!order.getCustomer().isActive())            throw new IllegalStateException("Inactive customer");

    return paymentGateway.charge(order);  // happy path — no nesting
}
```

---

## Java 17 idioms

- **`record` for parameter objects** (Rule 3) — immutable, compact, no boilerplate.
- **`switch` expressions** support early-return style without fall-through:
  ```java
  String label = switch (status) {
      case PENDING  -> "Awaiting approval";
      case APPROVED -> "Ready to ship";
      case REJECTED -> "Contact support";
  };
  ```

## Optional Java 21

- **Pattern matching for `switch`** — match on types and extract values in one step. If on Java 17, use traditional `instanceof` cast instead.

---

## Common traps

- **"Just one more flag"** — violates Rule 4. Add a new method instead.
- **"The method is short; adding one more thing is fine"** — violates Rule 2. Length is not the measure of focus.
- **"Null is simpler than an exception"** — violates Rule 9. Silent failures are never simpler.
- **"This nesting is readable"** — violates Rule 18 if >2 levels. Invert and return early.

## When the rule doesn't apply

- **Hot-loop micro-optimizations**: profile first; inlining may be required for measurable gains.
- **API methods pinned by external contract**: you cannot rename a method mandated by a serialization framework or third-party interface.

---

## Cross-references

- SKILL.md rules: 2, 3, 4, 5, 6, 8, 9, 13, 18.
- Related: `naming.md` (Rule 1 — a function's name is the first test of its design), `solid.md` (Rule 12 — SRP at the class level mirrors Rule 2 at the function level).
