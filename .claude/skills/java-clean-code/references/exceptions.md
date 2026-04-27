# Exceptions (Effective Java Items 69–77, selected)

How to design with exceptions: when to throw, what to throw, and what to do
in catch.

**Rules in this topic:**
- EJ Item 69 — Use exceptions only for exceptional conditions
- EJ Item 70 — Checked for recoverable, unchecked for programmer errors
- EJ Item 72 — Favor standard exceptions
- EJ Item 73 — Throw exceptions appropriate to the abstraction
- EJ Item 77 — Don't ignore exceptions

This reference complements **Rule 9** (Exceptions over error codes) in
[functions.md](functions.md). Rule 9 says *use exceptions instead of error
codes*; this reference says *which exception, when, and how to handle it*.

---

## Item 69: Exceptions only for exceptional conditions

Don't use exceptions for control flow. They're slower and signal "something
unusual happened" — which is a lie if you're using them as the normal path.

```java
// BEFORE — catching as control flow
try {
    while (true) range[i++].climb();
} catch (ArrayIndexOutOfBoundsException e) {
    // we ran off the end of the array
}

// AFTER — explicit termination
for (Mountain m : range) m.climb();
```

API design corollary: provide a *state-testing method* alongside the
state-dependent method, so the caller can avoid the exception:

```java
// API users: prefer hasNext+next over try+next+catch NoSuchElementException
while (it.hasNext()) {
    process(it.next());
}
```

---

## Item 70: Checked for recoverable, unchecked for programmer errors

- **Checked exceptions** (`extends Exception`, not `RuntimeException`):
  caller can reasonably recover (e.g. `IOException`, `SQLException`).
- **Runtime exceptions** (`extends RuntimeException`): programmer error,
  unrecoverable contract violation (`NullPointerException`, `IllegalArgumentException`).
- **Errors** (`extends Error`): JVM-level, don't catch.

In Spring Boot 3, the modern pattern is **unchecked everywhere** — domain
exceptions extend `RuntimeException` and a `@ControllerAdvice` translates
them to HTTP responses. Checked exceptions don't compose well with lambdas
and streams, so the ecosystem has drifted toward unchecked.

```java
// Domain exception — unchecked, descriptive
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String accountId, BigDecimal balance, BigDecimal requested) {
        super("account=%s balance=%s requested=%s".formatted(accountId, balance, requested));
    }
}
```

---

## Item 72: Favor standard exceptions

Don't invent `MyArgumentException` when `IllegalArgumentException` already
exists. Reuse signals "this is a known kind of failure", and readers
recognize the standard ones.

| When the failure is... | Throw |
|---|---|
| Bad argument | `IllegalArgumentException` |
| Null argument when not allowed | `NullPointerException` (yes, throw it explicitly via `Objects.requireNonNull`) |
| Object is in the wrong state | `IllegalStateException` |
| Unsupported operation | `UnsupportedOperationException` |
| Index out of range | `IndexOutOfBoundsException` |
| Concurrent modification | `ConcurrentModificationException` |

Custom exceptions are appropriate when callers need to *react differently*
to your exception than to a generic one (e.g. retrying on
`InsufficientFundsException` but not on `IllegalArgumentException`).

---

## Item 73: Throw exceptions appropriate to the abstraction

Higher-level methods should not let lower-level exceptions escape. Catch
the low-level exception and rethrow a domain-level one with context.

```java
// BEFORE — internals leak
public Order findOrder(OrderId id) throws SQLException, IOException { /* ... */ }

// AFTER — exception translation
public Order findOrder(OrderId id) {
    try {
        return repository.findById(id);
    } catch (SQLException e) {
        throw new OrderRepositoryException("failed to load order " + id, e);
    }
}
```

Always pass the underlying exception as the `cause` (`new X(msg, e)`) — never
swallow it. Stack traces preserve the chain.

---

## Item 77: Don't ignore exceptions

The empty catch block is almost always wrong.

```java
// BEFORE — silent failure
try {
    file.delete();
} catch (IOException e) {
    // ignored
}

// AFTER — at minimum: log with context
try {
    file.delete();
} catch (IOException e) {
    log.warn("failed to delete temp file {}", file, e);
}

// OR — explicit decision, named variable
try {
    file.delete();
} catch (IOException ignored) {
    // Best-effort cleanup; Files.deleteIfExists called separately at boot.
}
```

If you genuinely intend to ignore, **name the variable `ignored`** so
readers (and code reviewers) know it was a deliberate choice and not a
forgotten todo.

---

## Common traps

| Trap | Counter |
|---|---|
| Throwing exceptions in a loop's hot path | Item 69: use a state-testing method. |
| Inventing `MyServiceException extends Exception` | Item 72: use `IllegalStateException` first; only invent if reactions differ. |
| Letting `SQLException` leak from a service method | Item 73: translate to a domain exception with cause. |
| `catch (Exception e) { /* nothing */ }` | Item 77: log, rethrow, or rename to `ignored` with rationale comment. |
| Throwing `Exception` as a catch-all | Specific types only — `IOException`, `IllegalArgumentException`, etc. |

## When the rule doesn't apply

- **Item 70 exception:** when interfacing with legacy APIs that throw checked
  exceptions you must catch and wrap into runtime ones for stream/lambda
  use.
- **Item 73 exception:** if the lower-level exception IS the domain exception
  (e.g. you're writing a database driver), pass it through.

## Cross-references

- Rule 9 (Exceptions over error codes) in [functions.md](functions.md) — the
  trigger for using exceptions at all.
- [creating-objects.md](creating-objects.md) — Item 9 (try-with-resources)
  is the safe form for closing resources without losing the original
  exception.
- Rule 8 (No hidden side effects) in [functions.md](functions.md) — exception
  control flow IS a side effect; Item 69 is its prevention.
