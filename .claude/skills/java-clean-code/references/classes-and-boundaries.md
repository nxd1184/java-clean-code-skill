# Classes and Boundaries

## Core Principle

Classes should be small, with a single purpose. Boundaries to third-party code
isolate change — wrap external libs behind interfaces so your domain doesn't
depend on their shapes. When a class grows, it usually means a new collaborator
is trying to emerge; extract it before the class becomes unmaintainable.

---

## Rules Covered

| Rule | Summary |
|------|---------|
| 12 | Single Responsibility Principle — class-level overview (full SOLID deep dive in [solid.md](solid.md)) |

---

## Java Examples

### 1. God Class Before/After

A `UserService` that owns DB persistence, email delivery, and audit logging
is a god class. Any of the three concerns can change independently, so they
belong in separate classes.

```java
// ❌ Before — UserService is a god class (DB + email + audit in one)
@Service
public class UserService {
    @Autowired private JdbcTemplate db;
    @Autowired private JavaMailSender mailer;
    @Autowired private AuditRepository auditRepo;

    public void registerUser(User user) {
        db.update("INSERT INTO users ...", user.email(), ...);
        mailer.send(welcomeMessage(user));
        auditRepo.save(new AuditEvent("USER_REGISTERED", user.id()));
    }
}

// ✅ After — three responsibilities, three classes; coordinator stays thin
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserNotifier userNotifier;
    private final UserAuditLogger userAuditLogger;

    public UserService(UserRepository r, UserNotifier n, UserAuditLogger l) {
        this.userRepository = r;
        this.userNotifier = n;
        this.userAuditLogger = l;
    }

    public void registerUser(User user) {
        userRepository.save(user);
        userNotifier.sendWelcome(user);
        userAuditLogger.logRegistration(user);
    }
}
```

**Why it matters.** Each collaborator now has one reason to change. Swapping
the email provider touches only `UserNotifier`. Adding a second audit sink
touches only `UserAuditLogger`. Tests for each are smaller and faster.

---

### 2. Third-Party Boundary — Adapter Pattern

Domain classes that import Jackson, OkHttp, or any external library are coupled
to that library's release cycle. When the library changes an API or is replaced,
the blast radius is your entire domain. A thin interface (a "port") keeps the
domain ignorant of the implementation.

```java
// ❌ Bad — domain class depends directly on Jackson
public class OrderService {
    // Tied to Jackson; changing libraries touches OrderService
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String serialize(Order order) throws JsonProcessingException {
        return objectMapper.writeValueAsString(order);
    }
}

// ✅ Good — depend on a port; hide Jackson behind an adapter
public interface OrderSerializer {
    String serialize(Order order);
}

@Component
public class JacksonOrderSerializer implements OrderSerializer {
    private final ObjectMapper mapper;

    public JacksonOrderSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String serialize(Order order) {
        try {
            return mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize order", e);
        }
    }
}
```

`OrderService` now depends only on `OrderSerializer`. Switching from Jackson
to Gson is a new `GsonOrderSerializer` class — zero changes to domain logic.
Tests can inject a stub implementation in milliseconds without spinning up
the full Jackson infrastructure.

**Exception translation.** Notice that `JsonProcessingException` (a checked
Jackson exception) is translated to a domain exception at the boundary. Your
domain should never leak third-party exception types.

---

### 3. `package-private` as a Real Access Modifier

Java's default (package-private) visibility is underused. Classes that serve
only their own package should not be `public` — making them `public` invites
coupling from outside.

```java
// ❌ Public class leaks internals outside the package
public class UserMapper {           // any package can depend on this
    public UserEntity toEntity(User user) { ... }
}

// ✅ package-private — only classes in the same package can see it
class UserMapper {                  // no modifier = package-private
    UserEntity toEntity(User user) { ... }
}
```

Guidelines:
- Service/repository implementations that live inside a feature package:
  make them package-private; expose only the interface publicly.
- Test classes in `src/test/java` with the same package name can still
  access package-private members — no need to widen visibility for tests.
- In a multi-module Maven/Gradle project, use module-level exports
  (`module-info.java`) to enforce boundaries at compile time.

---

## Common Traps

**"One class per feature keeps the file count down."**
A 500-line class that does five things is harder to read, test, and maintain
than five focused classes of 100 lines each. File count is not a cost; cognitive
load is.

**"Internal classes can skip interfaces."**
If a class crosses a package boundary — or is injected into a collaborator in
another package — it should have an interface. Concrete dependencies across
packages make refactoring expensive.

**"I'll split it later."**
Classes grow. A class that today handles DB + email will next quarter handle
DB + email + push notifications + webhooks. Extract collaborators at design
time, when the seams are clean, not at crisis time when tests cover tangled
logic.

**"Wrapping third-party code is over-engineering."**
It is over-engineering until the third-party library releases a breaking change,
is abandoned, or a security advisory forces an upgrade. The adapter takes 20
minutes to write; the unmigrated blast radius costs days.

---

## When the Rule Doesn't Apply

- **Tiny scripts or one-off utilities.** A standalone main class that imports
  a library directly is fine when there is no domain to protect.
- **Stable value objects.** A `Money` record or `DateRange` class with no
  external dependencies and no meaningful behavior to split does not need
  further decomposition.
- **Micro-services at the boundary of the system.** The outermost adapter layer
  (a Spring `@RestController`, a Kafka listener) necessarily touches framework
  types; keep it thin and push domain logic inward.

---

## Cross-References

- SKILL.md rule: **12**
- Full SOLID deep dive (all five principles): [solid.md](solid.md)
- Architecture layers and package organization: [architecture.md](architecture.md)

---

## Effective Java additions

### Item 15: Minimize accessibility

Make every class and member as private as possible. Order of preference:
`private` > package-private > `protected` > `public`. Public is API
contract — breaking changes hurt.

For Spring Boot 3 services: prefer **package-private classes with public
methods** when only used within the package. The constructor stays public
for DI without leaking the type.

### Item 18: Composition over inheritance

Inheritance binds you to the parent's implementation. Composition lets you
swap collaborators. Classic counter-example — counting `add` calls:

```java
// BAD — InstrumentedHashSet's addAll double-counts because HashSet.addAll
// internally calls add()
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;
    @Override public boolean add(E e) { addCount++; return super.add(e); }
    @Override public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);   // calls add() → double counts
    }
}

// GOOD — composition via a forwarding wrapper
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;
    public InstrumentedSet(Set<E> delegate) { super(delegate); }
    @Override public boolean add(E e) { addCount++; return super.add(e); }
    @Override public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
}
```

Spring Boot 3 mapping: prefer collaborator interfaces injected via the
constructor over `extends BaseService`.

### Item 20: Prefer interfaces to abstract classes

Interfaces support multiple inheritance, mixins, and types without
implementation. Java 8 default methods further closed the gap.

**Interfaces** when: multiple unrelated classes should support the type;
common behavior is non-trivial but not shared.
**Abstract classes** when: a skeletal implementation provides real shared
logic most subclasses benefit from (`AbstractList`).

### Item 22: Interfaces only to define types

Don't put constants in an interface to avoid `Class.CONSTANT` — that
exposes internal implementation as part of the public API.

```java
// BAD — constant interface
public interface PhysicalConstants {
    static final double AVOGADRO = 6.022e23;
}
public class Calculator implements PhysicalConstants { /* ... */ }
// PhysicalConstants is now part of Calculator's public type.

// GOOD — utility class with private constructor
public final class PhysicalConstants {
    private PhysicalConstants() {}
    public static final double AVOGADRO = 6.022e23;
}
```

### Item 64: Refer to objects by their interfaces

Declare parameters, return types, and fields by interface, not
implementation.

```java
// BEFORE — concrete type leaks
public ArrayList<Customer> findCustomers(String region) { /* ... */ }
HashMap<String, User> users = new HashMap<>();

// AFTER — interface
public List<Customer> findCustomers(String region) { /* ... */ }
Map<String, User> users = new HashMap<>();
```

Operationalizes **Rule 19 (DIP)** in [solid.md](solid.md): depend on
abstractions, not concretions, at the type level.

**Exception:** when the implementation has methods the interface doesn't
and you legitimately need them (`ArrayList.ensureCapacity`,
`LinkedList.descendingIterator`), keep the concrete type and document why.
