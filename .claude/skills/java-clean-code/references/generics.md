# Generics (Effective Java Items 26–33, selected)

Generics give compile-time type safety without runtime overhead — but
require attention. Raw types defeat the system. Arrays and generics
interact awkwardly. Bounded wildcards make APIs flexible without sacrificing
safety.

**Rules in this topic:**
- EJ Item 26 — Don't use raw types
- EJ Item 28 — Prefer lists to arrays
- EJ Item 29 — Favor generic types
- EJ Item 31 — Use bounded wildcards (PECS)

---

## Item 26: Don't use raw types

A raw type (`List`, `Map`) erases the type parameter, removing compile-time
safety and forcing casts at every read site. The compiler treats the raw
type as `Object` for added elements.

```java
// BEFORE — raw type, runtime ClassCastException waiting
List items = new ArrayList();
items.add("hello");
items.add(42);
String first = (String) items.get(0);   // works
String second = (String) items.get(1);  // ClassCastException

// AFTER — parameterized
List<String> items = new ArrayList<>();
items.add("hello");
// items.add(42);                      // compile error — caught early
String first = items.get(0);            // no cast needed
```

**Don't reach for `List<Object>` either** — that's a different beast: it
allows any type but is invariant (`List<Object>` is not assignable from
`List<String>`). When you genuinely don't know the type, use a wildcard:
`List<?>` for read-only access, `List<? extends T>` / `List<? super T>` for
bounded use.

---

## Item 28: Prefer lists to arrays

Arrays are *covariant* and *reified* (carry their type at runtime). Generics
are *invariant* and *erased* (no runtime type info). The combination causes
trouble.

```java
// BEFORE — covariant arrays compile but fail at runtime
Object[] objectArray = new Long[1];
objectArray[0] = "I don't fit in";   // ArrayStoreException at runtime

// AFTER — invariant Lists fail at compile time
List<Object> objectList = new ArrayList<Long>();   // compile error
```

The corollary: **don't mix arrays and generics**. Code like
`new List<String>[10]` doesn't compile. If you need a generic collection of
generic things, use `List<List<E>>`, not `List<E>[]`.

Spring Boot 3 specific: keep `@RequestBody`, `@ResponseBody`, JPA, and Jackson
on `List<T>` — those frameworks and `T[]` interact awkwardly.

---

## Item 29: Favor generic types

Convert collection-like classes from `Object`-based to generic. Lift the
type parameter to the class declaration; the implementation usually requires
one cast (with `@SuppressWarnings("unchecked")` and a comment explaining why
it's safe).

```java
// BEFORE — Object-based stack
public class Stack {
    private Object[] elements;
    private int size = 0;

    public void push(Object e) { elements[size++] = e; }
    public Object pop() { return elements[--size]; }
}

// Caller is forced to cast and trust:
String s = (String) stack.pop();

// AFTER — generic
public class Stack<E> {
    private Object[] elements;            // can't be E[] (Item 28)
    private int size = 0;

    public Stack() { elements = new Object[16]; }

    public void push(E e) { elements[size++] = e; }

    @SuppressWarnings("unchecked")     // safe: only E values are pushed
    public E pop() {
        E result = (E) elements[--size];
        elements[size] = null;
        return result;
    }
}

// Caller has type safety:
Stack<String> stack = new Stack<>();
stack.push("hello");
String s = stack.pop();
```

---

## Item 31: Bounded wildcards — PECS

**Producer-Extends, Consumer-Super.**

- A parameter that *produces* values for the collection: use `extends`.
- A parameter that *consumes* values from the collection: use `super`.

```java
// PRODUCER — reads Es out of the source
public void copyAll(List<? extends E> src, List<E> dst) {
    for (E e : src) dst.add(e);
}

// CONSUMER — writes Es into the destination
public void fillWith(List<? super E> dst, E value, int n) {
    for (int i = 0; i < n; i++) dst.add(value);
}

// Real example: Comparator<? super T> in Collections.sort
Collections.sort(List<T> list, Comparator<? super T> cmp);
//   you can sort List<Dog> with Comparator<Animal>: dogs are animals
```

**Trap:** don't use bounded wildcards on return types. Return concrete
generic types (`List<T>`, not `List<? extends T>`) — wildcards in returns
force the caller to use wildcards too, propagating the noise.

---

## Common traps

| Trap | Counter |
|---|---|
| `List` instead of `List<T>` | Item 26: parameterize. |
| `T[]` field in a generic class | Item 28: use `Object[]` + safe cast in `pop()`-like methods, with `@SuppressWarnings`. |
| Unbounded `<?>` when you need to read a specific subtype | Item 31: use `<? extends T>`. |
| Mixing reflection and generics | Reflection erases the parameter; you'll need `Class<T>` token witnesses. |
| `List<Object>` "to be flexible" | It's actually less flexible than `List<? extends Object>` — see Item 28. |

## When the rule doesn't apply

- **Item 26 exception:** `List.class` (the *class literal*) is correct;
  `List<String>.class` doesn't compile. Class literals are raw by design.
- **Item 28 exception:** primitive arrays (`int[]`, `byte[]`) — generics
  don't support primitives, so arrays are the only option.

## Cross-references

- [classes-and-boundaries.md](classes-and-boundaries.md) — Item 64 (refer
  to objects by interfaces) often interacts with generic return types.
- [functions.md](functions.md) — Rule 3 (≤3 args) and bounded wildcards
  combine to keep API signatures readable.
