# Lambdas and Streams (Effective Java Items 42–46, selected)

Java 8 introduced functional patterns; Java 11/17 polished them. They are
not always the right answer — Item 45 explicitly warns against overuse.

**Rules in this topic:**
- EJ Item 42 — Lambdas over anonymous classes
- EJ Item 43 — Method references over lambdas (when clearer)
- EJ Item 44 — Standard functional interfaces from `java.util.function.*`
- EJ Item 45 — Streams judiciously (when, when not, debugability)
- EJ Item 46 — Side-effect-free functions in streams

---

## Item 42: Lambdas over anonymous classes

Anonymous classes are 5+ lines for what a lambda does in one. Java 17
infers types automatically.

```java
// BEFORE — anonymous class
Comparator<String> byLength = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return Integer.compare(a.length(), b.length());
    }
};

// AFTER — lambda
Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());
```

**Trap:** lambdas don't have a `this` of their own; `this` refers to the
enclosing class. Anonymous classes do. If you need self-reference (rare),
keep the anonymous class.

**Trap:** lambdas can't be serialized reliably across process boundaries.
Keep them in-process.

---

## Item 43: Method references over lambdas (when clearer)

A method reference is shorter and names the operation. Use it when the
lambda is just a delegation.

```java
// BEFORE — lambda
list.stream().map(s -> s.toUpperCase()).toList();
list.forEach(s -> System.out.println(s));

// AFTER — method references
list.stream().map(String::toUpperCase).toList();
list.forEach(System.out::println);
```

**Don't force it.** When the parameter or the call adds clarity, keep the
lambda:

```java
// Lambda is clearer here — the cast and field access aren't trivial
people.stream().map(p -> ((Employee) p).getDepartment().getName())
```

Forms: `ClassName::staticMethod`, `ClassName::instanceMethod`,
`instance::instanceMethod`, `ClassName::new`.

---

## Item 44: Standard functional interfaces

Don't write your own SAM (single-abstract-method) interface unless you have
a real reason. The 43 standard ones in `java.util.function.*` cover most
cases and integrate with the JDK.

| Interface | Function shape | Example |
|---|---|---|
| `Function<T,R>` | T → R | `String::length` |
| `Predicate<T>` | T → boolean | `String::isEmpty` |
| `Consumer<T>` | T → void | `System.out::println` |
| `Supplier<T>` | () → T | `Instant::now` |
| `BiFunction<T,U,R>` | (T,U) → R | `BigDecimal::add` |
| `UnaryOperator<T>` | T → T | `String::trim` |

**Write your own** when:
- You have descriptive method names that document intent (e.g.
  `Comparator<T>`).
- You want a default-method API contract (e.g. `Comparator.thenComparing`).
- The functional interface participates in pattern recognition (`Runnable`,
  `Callable`).

---

## Item 45: Streams judiciously

Streams are great for a chain of map/filter/reduce on a single source.
They're worse than a loop when:

- You need stateful side effects mid-pipeline.
- You need to read multiple variables from an enclosing scope.
- The transformation has 5+ steps (the chain becomes hard to debug).
- You need exception handling inside the pipeline.

```java
// GOOD — straight transformation
var emails = users.stream()
                  .filter(User::isActive)
                  .map(User::email)
                  .toList();

// BAD — stateful, hard to debug
int[] counter = {0};
var weird = items.stream()
    .peek(i -> counter[0]++)            // side effect — Item 46 violation
    .map(i -> processWithEnclosingState(i, counter))
    .collect(Collectors.toList());
// A for-each loop would be clearer.
```

**Debugging note:** stepping through a stream pipeline in a debugger is
painful. Prefer a loop when you anticipate troubleshooting.

---

## Item 46: Side-effect-free functions in streams

`map`, `filter`, `reduce` should be pure. The stream pipeline mutates only
the resulting collection (via the *terminal* operation).

```java
// BEFORE — side-effect inside map
Map<String, Long> wordCount = new HashMap<>();
words.stream().forEach(w -> wordCount.merge(w, 1L, Long::sum));

// AFTER — collector handles the mutation
Map<String, Long> wordCount = words.stream()
    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
```

`forEach` is for output (printing, sending) — not for accumulating state.
Use `collect` for accumulation.

---

## Common traps

| Trap | Counter |
|---|---|
| Anonymous inner class for a 1-line callback | Item 42: lambda. |
| Lambda doing only `x -> x.method()` | Item 43: `Class::method`. |
| Custom `interface MyMapper { String map(...); }` | Item 44: use `Function<T,String>`. |
| Long stream chain with side-effect peek | Item 45: rewrite as a for-each loop. |
| `forEach(map::put)` to populate a Map | Item 46: use `Collectors.toMap` or `groupingBy`. |
| `try { ... } catch` inside a `.map(...)` | Streams + checked exceptions don't mix. Wrap in unchecked or use a helper. |

## When the rule doesn't apply

- **Item 42 exception:** if you need `this`-self-reference, the lambda
  `this` is wrong; use an anonymous class.
- **Item 45 exception:** for a single trivial transformation, the
  loop-vs-stream call is style; either is fine.
- **Item 46 exception:** `forEach` is *the* terminal for side effects (I/O,
  external mutation). Just don't use it as a substitute for `collect`.

## Cross-references

- [functions.md](functions.md) — Rule 13 (DRY): often the trigger to extract
  a `Function<T,R>` collaborator.
- [concurrency.md](concurrency.md) — parallel streams (`.parallel()`) need
  Item 48's caveats; Spring Boot's `@Async` is usually a better fit for
  request-scoped parallelism.
