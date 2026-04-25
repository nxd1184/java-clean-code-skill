# Formatting — Rule 7: Stepdown / Newspaper Metaphor

## Core Principle

Formatting communicates structure. A reader skimming should grasp the shape of the code before reading a single line. Think of a newspaper: the headline gives the full story at a glance, each paragraph adds detail, and fine print comes last. Apply the same hierarchy to every class.

---

## Rules Covered

| Rule | Name | One-line summary |
|------|------|-----------------|
| 7 | Stepdown rule (newspaper metaphor) | High-level entry points at the top; helpers and details below |

---

## Java Examples

### Rule 7 — Stepdown Order

Place public entry points first. Private helpers follow in the order they are first called. A reader traces the logic top-to-bottom without scrolling back up.

```java
// ❌ helpers before callers — reader hits detail before context
public class OrderProcessor {
    private boolean isEligible(Order order) { ... }   // helper
    private void applyDiscount(Order order) { ... }   // helper
    public void process(Order order) {                // entry point
        if (isEligible(order)) applyDiscount(order);
    }
}

// ✅ stepdown — entry point first, helpers follow in call order
public class OrderProcessor {
    public void process(Order order) {
        if (isEligible(order)) applyDiscount(order);
    }
    private boolean isEligible(Order order) { ... }
    private void applyDiscount(Order order) { ... }
}
```

### Vertical Density — Blank Lines Between Concepts, Not Within

A blank line signals "new concept starts here." Inside a single concept, blank lines add noise and break the visual unit.

```java
// ❌ unnecessary blank lines inside a concept
private boolean isEligible(Order order) {

    return order.totalCents() > 0;

}

// ✅ dense within concept, blank lines between concepts
private boolean isEligible(Order order) {
    return order.totalCents() > 0;
}

private void applyDiscount(Order order) {
    order.applyPercent(DISCOUNT_RATE);
}
```

### Horizontal Line Length — Max 120 Columns

Long lines force horizontal scrolling and hide the structure of a call. Break at logical boundaries — usually before each argument group.

```java
// ❌ too wide
String result = someService.doThing(parameter1, parameter2, parameter3, parameter4, parameter5);

// ✅ break at logical boundary
String result = someService.doThing(
    parameter1, parameter2, parameter3,
    parameter4, parameter5);
```

For method chains, break before each `.`:

```java
// ❌
List<String> names = orders.stream().filter(Order::isActive).map(Order::customerName).sorted().toList();

// ✅
List<String> names = orders.stream()
    .filter(Order::isActive)
    .map(Order::customerName)
    .sorted()
    .toList();
```

---

## Common Traps

**"Helpers at the top make them easier to find."**
It makes entry points harder to find. IDEs navigate to any method in one keystroke; human readers scan top-to-bottom and expect the story to start at the beginning.

**Long methods that mix high-level and low-level logic in the same block.**
If a method calls a helper and also contains the helper's body inline, extract the inline body. Stepdown only works when each level delegates cleanly.

**Blank lines inside a single concept.**
Every blank line is a visual promise: "something different starts here." Breaking that promise inside one `if`-block or one short method trains readers to ignore the signal everywhere.

**Lines over 120 columns.**
Even with wide monitors, long lines obscure structure. Code is read far more often than it is written; optimise for reading width, not writing convenience.

---

## When the Rule Doesn't Apply

**Auto-generated code** — mappers, protocol buffer stubs, JOOQ-generated classes. Do not reorder generated files; the generator will overwrite changes.

**Team formatter enforcement** — if the project uses Spotless, google-java-format, or Checkstyle with a different ordering convention, defer to the tool. Consistent automated style beats manual adherence to any single rule. Document the deviation in a project-level ADR if needed.

---

## Cross-References

- **SKILL.md rule:** 7
- **functions.md** — Rule 2 (one thing per function) is the prerequisite for stepdown: a function that does multiple things cannot be cleanly delegated to helpers.
- **naming.md** — clear method names make the stepdown readable; vague names hide whether a method is an entry point or a helper.
