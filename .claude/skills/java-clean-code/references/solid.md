# SOLID Principles — Java Reference

## Core principle

SOLID principles are design gravity: each one reduces a specific kind of pain.
Used together, they produce code that is easy to extend without breaking what exists.

**Rules covered:** 12 (SRP — Single Responsibility), 19 (DIP — constructor injection).

---

## SRP — Single Responsibility (Rule 12)

**One reason to change.** A class that does email AND persistence changes when
either email templates OR the DB schema changes. Those are two separate reasons —
SRP says they belong in two separate classes.

```java
// ❌ Three reasons to change: DB schema, email template, audit format
@Service
public class UserRegistrationService {
    @Autowired private UserRepository userRepository;
    @Autowired private JavaMailSender mailSender;
    @Autowired private AuditRepository auditRepository;

    public void register(User user) {
        userRepository.save(user);                                          // persistence
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail()); msg.setSubject("Welcome!");
        msg.setText("Hi " + user.getName() + ", thanks for signing up.");
        mailSender.send(msg);                                               // email
        auditRepository.save(new AuditEntry("REGISTER", user.getId(), Instant.now())); // audit
    }
}
```

```java
// ✅ After — each class has one reason to change

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}

@Service
public class WelcomeEmailService {
    private final JavaMailSender mailSender;
    public WelcomeEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendWelcome(User user) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Welcome!");
        msg.setText("Hi " + user.getName() + ", thanks for signing up.");
        mailSender.send(msg);
    }
}

@Service
public class AuditService {
    private final AuditRepository auditRepository;
    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }
    public void record(String event, Long userId) {
        auditRepository.save(new AuditEntry(event, userId, Instant.now()));
    }
}

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final WelcomeEmailService welcomeEmailService;
    private final AuditService auditService;

    public UserRegistrationService(
            UserRepository userRepository,
            WelcomeEmailService welcomeEmailService,
            AuditService auditService) {
        this.userRepository = userRepository;
        this.welcomeEmailService = welcomeEmailService;
        this.auditService = auditService;
    }

    public void register(User user) {
        userRepository.save(user);
        welcomeEmailService.sendWelcome(user);
        auditService.record("REGISTER", user.getId());
    }
}
```

**Spring seams.** `@Service`, `@Repository`, and `@Component` are natural SRP
checkpoints. If an `@Service` imports both a mail sender and a repository, ask
whether those belong together.

---

## OCP — Open/Closed

**Open for extension, closed for modification.** Adding behavior should mean
adding code, not editing existing code. Spring's `List<T>` injection makes this
natural.

```java
// ❌ Before — adding a new payment type requires editing the switch
@Service
public class PaymentProcessor {
    public void process(Payment payment) {
        switch (payment.type()) {
            case CARD   -> chargeCard(payment);
            case PAYPAL -> chargePaypal(payment);
            // adding CRYPTO means editing this class
        }
    }
}

// ✅ After — Spring injects all handlers; new type = new class only
public interface PaymentHandler {
    boolean supports(PaymentType type);
    void handle(Payment payment);
}

@Service
public class PaymentProcessor {
    private final List<PaymentHandler> handlers;

    public PaymentProcessor(List<PaymentHandler> handlers) {
        this.handlers = handlers;
    }

    public void process(Payment payment) {
        handlers.stream()
            .filter(h -> h.supports(payment.type()))
            .findFirst()
            .orElseThrow(() -> new UnsupportedPaymentTypeException(payment.type()))
            .handle(payment);
    }
}

// Adding CRYPTO = one new class, zero edits elsewhere
@Service
public class CryptoPaymentHandler implements PaymentHandler {
    @Override public boolean supports(PaymentType type) { return type == CRYPTO; }
    @Override public void handle(Payment payment) { /* ... */ }
}
```

Spring collects all `PaymentHandler` beans automatically; new handlers are
discovered at startup without touching `PaymentProcessor`.

---

## LSP — Liskov Substitution

**Subclasses must be substitutable for their base.** Any code that works with
`Rectangle` must also work correctly with every subtype. Classic violation:
`Square extends Rectangle`.

```java
// ❌ LSP violation — Square breaks Rectangle's width/height contract
class Rectangle {
    protected int width, height;
    void setWidth(int w)  { this.width = w; }
    void setHeight(int h) { this.height = h; }
    int area() { return width * height; }
}
class Square extends Rectangle {
    @Override void setWidth(int w)  { super.setWidth(w);  super.setHeight(w); } // breaks contract
    @Override void setHeight(int h) { super.setWidth(h);  super.setHeight(h); }
}
// Caller surprise:
Rectangle r = new Square();
r.setWidth(4);
r.setHeight(5);
assert r.area() == 20; // FAILS — area is 25
```

```java
// ✅ Fix — compose, don't inherit; use a shared interface
interface Shape { int area(); }

record Rectangle(int width, int height) implements Shape {
    public int area() { return width * height; }
}
record Square(int side) implements Shape {
    public int area() { return side * side; }
}
```

`record` (Java 16+) makes these immutable value types — no mutable setters,
no opportunity for contract violations.

---

## ISP — Interface Segregation

**Many small, focused interfaces beat one fat interface.** Callers should not be
forced to depend on methods they do not use.

```java
// ❌ Fat repository forces implementors to stub unrelated methods
public interface UserRepository {
    User save(User user);
    User findById(Long id);
    void exportToCsv(OutputStream out);   // unrelated to persistence
    void sendReport(String email);        // also unrelated
}

// ✅ Split by concern — each interface is stable and testable independently
public interface UserRepository { User save(User user); User findById(Long id); }
public interface UserExporter   { void exportToCsv(OutputStream out); }
public interface UserReporter   { void sendReport(String email); }
```

Each interface client only sees the methods it needs; mocking a narrow interface
in tests is far simpler than stubbing a fat one.

---

## DIP — Dependency Inversion (Rule 19)

**Depend on abstractions, not concretions. Constructor injection enforces this.**
High-level modules (`UserService`) and low-level modules (`JpaUserRepository`)
both depend on an abstraction (`UserRepository`). Constructor injection makes
the dependency visible and testable.

```java
// ❌ Field injection hides dependencies; requires Spring context to test
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;   // Spring sets this; test can't easily inject a fake
}

// ✅ Constructor injection — dependency is explicit; testable without Spring
@Service
public class UserService {
    private final UserRepository userRepository;

    // Spring Boot 3: @Autowired is implicit on single-constructor classes
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrThrow(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}

// Test — no Spring context needed
class UserServiceTest {
    @Test
    void throwsWhenUserNotFound() {
        var repo = mock(UserRepository.class);
        given(repo.findById(99L)).willReturn(Optional.empty());

        var service = new UserService(repo);

        assertThrows(UserNotFoundException.class, () -> service.findOrThrow(99L));
    }
}
```

---

## Common traps

- **"Spring handles DI for me, SOLID is automatic."** The DI container wires the
  object graph. SRP, OCP, LSP, ISP are still your responsibility — Spring only
  manages construction.
- **"An interface for every class."** ISP says small and focused, not ubiquitous.
  Stable domain primitives (a `Money` record) rarely need an interface behind them.
- **"Inheritance is reuse."** Prefer composition. LSP violations are the recurring
  cost of inheritance misuse; the `Square`/`Rectangle` trap appears often in
  domain models.
- **"@Autowired field injection is shorter."** It hides dependencies, breaks plain
  instantiation in tests, and prevents `final` fields (Rule 19).

## When the rule doesn't apply

- **Truly stable domain primitives** — a `Money` record, a `Coordinate` value
  object — have no extension points and don't need interfaces.
- **Simple scripts or utilities** with a single entry point and no anticipated
  variation. SOLID is for code that grows; one-off tools are exempt.
- **Internal implementation details** not exposed past a package boundary —
  private helpers need only local coherence, not interface abstraction.

---

## Cross-references

- SKILL.md rules: 12 (SRP), 19 (DIP — constructor injection).
- [classes-and-boundaries.md](classes-and-boundaries.md) — class-level SRP
  overview; the fat-service split introduced there is deepened here.
- [testing.md](testing.md) — constructor injection makes unit tests easy; the
  `UserServiceTest` pattern above applies throughout.
- [architecture.md](architecture.md) — DIP drives the dependency rule in clean
  architecture; outer layers depend on inner abstractions, never the reverse.
