# Naming

## Core principle

A name is a promise. It says what something is, why it exists, and how it's used. Rename as soon as the promise changes — names that lag behind the code mislead every future reader.

## Rules covered here

- **Rule 1: Names reveal intent.** `elapsedTimeInDays`, not `d`.
- **Rule 14: Consistent vocabulary.** Pick one of `fetch` / `get` / `retrieve` per concept and use it everywhere.
- **Rule 17: No magic numbers or unexplained literals.** Extract to named constants.

## Java examples

### ❌ Before

```java
public List<int[]> getThem() {
    List<int[]> list1 = new ArrayList<>();
    for (int[] x : theList) {
        if (x[0] == 4) list1.add(x);
    }
    return list1;
}
```

Problems: `getThem`, `theList`, `x`, `list1`, `4` all hide intent.

### ✅ After

```java
private static final int FLAGGED = 4;

public List<Cell> flaggedCells() {
    return gameBoard.stream()
        .filter(cell -> cell.status() == FLAGGED)
        .toList(); // Java 16+
}
```

Every name answers a question: what is this, what does it do, why this value.

### Records for parameter objects (Java 17+)

When a method takes many primitive arguments, those params get names like `a`, `b`, `c` — use a `record` so each field earns a proper descriptive name:

```java
public record CreateUserRequest(String email, String username, int age) {}

public User create(CreateUserRequest request) { ... }
```

### ❌ Inconsistent vocabulary (Rule 14)

```java
public interface UserRepository {
    User fetchById(Long id);
    List<User> getAll();
    User retrieveByEmail(String email);
}
```

### ✅ Consistent vocabulary

```java
public interface UserRepository {
    User findById(Long id);
    List<User> findAll();
    User findByEmail(String email);
}
```

Pick one verb (`find`, `get`, `fetch`, `retrieve`) per concept and stick to it.

## Common traps

- **"`data`, `info`, `tmp` are good enough."** They carry zero information. Rename.
- **"The context makes it clear."** Names must survive out-of-context reading (grep, stack traces, review comments).
- **"Hungarian prefixes like `strName` help."** Java's type system already tells you that. Don't double up.
- **"Short loop variables are fine."** `i`, `j`, `k` in tight 3-line loops — yes. Beyond that, use a real name.

## When the rule doesn't apply

- Single-letter counters in 2–3 line loops.
- Math-heavy code where `x`, `y`, `n`, `theta` match the domain convention.
- Generic type parameters (`T`, `K`, `V`) — established idiom.

## Cross-references

- SKILL.md rules: 1, 14, 17.
- Related: [comments.md](comments.md) (names often replace comments), [functions.md](functions.md) (a good function name is the first test of its design).
