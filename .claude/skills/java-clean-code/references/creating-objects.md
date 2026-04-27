# Creating Objects (Effective Java Items 1–9)

How a class hands instances to callers. Static factories vs constructors,
builders vs telescoping constructors, dependency injection, resource
lifecycles. Java 17 records simplify many of these decisions.

**Rules in this topic:**
- EJ Item 1 — Consider static factories instead of constructors
- EJ Item 2 — Builder for many parameters
- EJ Item 5 — Prefer dependency injection (cross-references Rule 19 / DIP)
- EJ Item 9 — try-with-resources over try-finally

---

## Item 1: Static factories vs constructors

A static factory method is a public static method that returns an
instance — a smarter constructor. Five advantages:

1. **They have names.** `BigInteger.probablePrime(32, rng)` reads better than
   `new BigInteger(32, rng)`.
2. **No new instance required.** They can return cached or shared values
   (`Boolean.valueOf(true)`).
3. **They can return a subtype** of the declared return type.
4. **Returned class can vary** with input parameters
   (`EnumSet.noneOf(Color.class)` returns `RegularEnumSet` or `JumboEnumSet`).
5. **The returned class doesn't have to exist** when the factory is written
   (service-provider pattern).

**Conventional names:** `of`, `from`, `valueOf`, `getInstance`, `newInstance`,
`create`, `getType`, `newType`.

### Before / After

```java
// BEFORE — what does this constructor mean?
public class UserSession {
    public UserSession(String userId, Instant created) { /* ... */ }
}
new UserSession("alice", Instant.now());        // unclear from call site
new UserSession("alice", Instant.EPOCH);        // is this anonymous?

// AFTER — named static factories
public class UserSession {
    private final String userId;
    private final Instant createdAt;

    private UserSession(String userId, Instant createdAt) {
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static UserSession forUser(String userId) {
        return new UserSession(userId, Instant.now());
    }

    private static final UserSession ANONYMOUS =
        new UserSession("anonymous", Instant.EPOCH);

    public static UserSession anonymous() {
        return ANONYMOUS;     // cached flyweight
    }
}
// Call sites read like English:
UserSession.forUser("alice");
UserSession.anonymous();
```

**Trap:** classes with only static factories and no public constructors
cannot be subclassed by clients. Often a feature, not a bug.

---

## Item 2: Builder when many parameters

Telescoping constructors (4 overloads, 6 args each) are unreadable.
JavaBeans setters break immutability. Builders combine the two.

### Before / After

```java
// BEFORE — telescoping constructor
public class Pizza {
    public Pizza(String size, boolean cheese, boolean pepperoni,
                 boolean mushrooms, boolean olives) { /* ... */ }
}
new Pizza("large", true, true, false, true);    // unreadable

// AFTER — builder
public class Pizza {
    private final String size;
    private final Set<Topping> toppings;

    private Pizza(Builder b) {
        this.size = b.size;
        this.toppings = Set.copyOf(b.toppings);
    }

    public static Builder builder(String size) { return new Builder(size); }

    public static class Builder {
        private final String size;
        private final EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        Builder(String size) { this.size = size; }
        public Builder add(Topping t) { toppings.add(t); return this; }
        public Pizza build() { return new Pizza(this); }
    }
}

Pizza p = Pizza.builder("large").add(Topping.CHEESE).add(Topping.OLIVES).build();
```

**Modern Java angle:** for ≤7 fields, prefer a `record` with named arguments:

```java
public record Pizza(String size, Set<Topping> toppings) {
    public Pizza {
        toppings = Set.copyOf(toppings);   // defensive copy; see Item 50
    }
}
```

Use the Builder when toppings is constructed step-by-step at call sites or
defaults are involved.

---

## Item 5: Prefer dependency injection (deepens Rule 19 / DIP)

See `solid.md` Rule 19 for the constructor-injection convention. EJ Item 5
adds:

- A class that depends on a *resource* (configuration, database,
  random-number generator) should accept that resource as a constructor
  parameter, never instantiate it itself.
- Static utility classes that depend on resources are the wrong shape —
  convert them to instantiable classes with constructor injection.

```java
// BEFORE — hard-wired dependency
public class SpellChecker {
    private static final Lexicon DICTIONARY = new EnglishLexicon();
    private SpellChecker() {}
    public static boolean isValid(String word) { /* uses DICTIONARY */ }
}

// AFTER — inject the lexicon
public class SpellChecker {
    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isValid(String word) { /* uses this.dictionary */ }
}
```

In Spring Boot 3, this becomes a constructor-injected `@Service`. See the
`UserService` and `OrderService` after-pairs in `examples/after/`.

---

## Item 9: try-with-resources over try-finally

`try-finally` for closeables is verbose, error-prone (`close()` can throw),
and obscures the original exception. `try-with-resources` is the standard
modern form.

```java
// BEFORE — try-finally
BufferedReader br = new BufferedReader(new FileReader(path));
try {
    return br.readLine();
} finally {
    br.close();   // can throw, masking the original exception
}

// AFTER — try-with-resources
try (BufferedReader br = new BufferedReader(new FileReader(path))) {
    return br.readLine();
}   // close() called automatically; suppressed exceptions chained
```

Works with any `AutoCloseable`. Multiple resources go in one declaration:

```java
try (var in = Files.newBufferedReader(src);
     var out = Files.newBufferedWriter(dst)) {
    in.transferTo(out);
}
```

---

## Common traps

| Trap | Counter |
|---|---|
| Using a constructor when a `static of(...)` would name the intent | Apply Item 1's naming convention; one `of`/`from` factory per intent. |
| Writing a 7-arg constructor "for convenience" | Apply Item 2 OR convert to a `record` if no defaults. |
| Static `private static final` resources hard-wired in a service | Item 5: inject via constructor. |
| `try { ... } finally { x.close(); }` | Item 9: `try (x) { ... }`. |

## When the rule doesn't apply

- **Static factories:** if subclassability is required, you need a public
  constructor.
- **Builders:** for 2–4 parameter constructors, the ceremony exceeds the
  benefit. Use a `record`.
- **DI:** for truly value-typed objects (`Money(BigDecimal, Currency)`)
  there's nothing to inject.
- **try-with-resources:** when the resource is borrowed (you didn't open it,
  the caller did), don't close it.

## Cross-references

- Rule 3 (≤3 arguments) in [functions.md](functions.md) — the trigger for
  reaching for a Builder or record.
- Rule 19 (Constructor injection / DIP) in [solid.md](solid.md) — Item 5's
  alignment.
- Rule 9 (Exceptions over error codes) in [functions.md](functions.md) — Item 9
  prevents silent close-failure exception loss.
- [objects-and-data.md](objects-and-data.md) — Item 17 (immutability) and
  Item 50 (defensive copies) are the natural follow-ups to Items 1/2.
