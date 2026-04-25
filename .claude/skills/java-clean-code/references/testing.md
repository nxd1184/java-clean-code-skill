# Testing

## Core principle

Tests are the safety net that lets you refactor without fear. A test suite you trust is worth more than a test suite that exists.

## Rules covered here

| Rule | Summary |
|---|---|
| 15 | Write the test first (TDD) |
| 16 | Tests are FIRST — Fast, Independent, Repeatable, Self-validating, Timely |

---

## Rule 15 — Write the test first (TDD)

The TDD loop is **RED → GREEN → REFACTOR**. A failing test written before implementation forces you to think about the API and intent before writing any logic. That design feedback is lost if you write tests afterward.

### Step 1: RED — write a failing test

```java
// No UserService.register() exists yet — this test must fail to compile/run
@Test
@DisplayName("registerUser saves user with hashed password")
void registerUser_savesUserWithHashedPassword() {
    var request = new CreateUserRequest("alice@example.com", "alice", "secret123");
    var saved = userService.register(request);
    assertThat(saved.passwordHash()).isNotEqualTo("secret123");
}
```

### Step 2: GREEN — write the minimal implementation that makes the test pass

No extra logic. No early generalisation. Just enough to go green.

### Step 3: REFACTOR — clean up without breaking the test

Extract constants, rename variables, reduce duplication. The green test is your guard rail.

> The test is the first consumer of your API. If it is awkward to set up, the production caller will be too.

### Test naming convention

Use `methodName_condition_expectedBehavior` to make tests self-documenting:

```java
@Test void register_withDuplicateEmail_throwsConflictException() { ... }
@Test void register_withNullEmail_throwsIllegalArgumentException() { ... }
@Test void register_validRequest_returnsHashedPassword() { ... }
```

---

## Rule 16 — Tests are FIRST

| Letter | Meaning |
|---|---|
| F | Fast — milliseconds per test, not seconds |
| I | Independent — no shared mutable state; any order, any subset |
| R | Repeatable — same result every run on any machine |
| S | Self-validating — pass/fail, no manual inspection |
| T | Timely — written before (or alongside) the code |

---

### FIRST — Fast

Avoid loading the Spring application context for unit tests. `@SpringBootTest` starts every bean — a 3-second warm-up repeated hundreds of times becomes minutes of CI waste.

```java
// ❌ Slow — loads entire Spring context for a unit test
@SpringBootTest
class UserServiceTest {
    @Autowired UserService userService;
    // ...
}

// ✅ Fast — plain JUnit 5 + Mockito, no context load
class UserServiceTest {
    private final UserRepository repo = mock(UserRepository.class);
    private final UserService service = new UserService(repo);

    @Test
    void register_delegatesToRepository() {
        var request = new CreateUserRequest("alice@example.com", "alice", "secret123");
        service.register(request);
        verify(repo).save(any(User.class));
    }
}
```

> Constructor injection (see [solid.md](solid.md)) is what makes the plain-Mockito style possible — no Spring magic required.

---

### FIRST — Independent

Tests must not share mutable state. Static fields, in-memory lists, and singleton side effects create order-dependent tests that pass alone but fail in suites.

```java
// ❌ Tests share state via static field — order-dependent
class OrderServiceTest {
    static List<Order> orders = new ArrayList<>();

    @Test void firstTest()  { orders.add(new Order(...)); }
    @Test void secondTest() { assertThat(orders).isEmpty(); } // fails if firstTest runs first
}

// ✅ Fresh fixture per test with @BeforeEach
class OrderServiceTest {
    private List<Order> orders;

    @BeforeEach
    void setUp() { orders = new ArrayList<>(); }

    @Test void firstTest()  { orders.add(new Order(...)); assertThat(orders).hasSize(1); }
    @Test void secondTest() { assertThat(orders).isEmpty(); } // always passes
}
```

---

### FIRST — Repeatable

Tests that call `Instant.now()`, `LocalDate.now()`, `UUID.randomUUID()`, or hit the network are non-repeatable by definition. Inject the source of non-determinism.

```java
// ❌ Non-repeatable — depends on wall clock
public boolean isExpired(Instant expiresAt) {
    return Instant.now().isAfter(expiresAt);  // non-deterministic in tests
}

// ✅ Inject Clock for full testability
public boolean isExpired(Instant expiresAt, Clock clock) {
    return clock.instant().isAfter(expiresAt);
}

// Test — the clock is frozen; the result is always the same
@Test
void isExpired_whenPastExpiry_returnsTrue() {
    Clock fixed = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    assertThat(service.isExpired(Instant.parse("2025-12-31T23:59:59Z"), fixed)).isTrue();
}
```

---

### FIRST — Self-validating

Every test must produce a binary pass/fail result without manual log inspection. AssertJ gives readable failure messages automatically.

```java
// ❌ Prints to stdout — a human must read it to know if it's correct
@Test
void manualInspection() {
    System.out.println("Result: " + service.register(request));
}

// ✅ Assertion fails with a clear message on mismatch
@Test
void register_validRequest_returnsNonNullId() {
    var saved = service.register(new CreateUserRequest("alice@example.com", "alice", "secret123"));
    assertThat(saved.id()).isNotNull();
}
```

---

### FIRST — Timely (and parameterized tests)

Write the test before or alongside the code. Parameterized tests let one test cover multiple edge cases without duplication — write them up front to drive out all the validation paths.

```java
@ParameterizedTest
@ValueSource(strings = {"", " ", "not-an-email", "a@"})
@DisplayName("invalid emails are rejected")
void register_rejectsInvalidEmail(String email) {
    assertThatThrownBy(() -> new CreateUserRequest(email, "user", "pass123"))
        .isInstanceOf(IllegalArgumentException.class);
}
```

---

## Patterns

### AAA — Arrange / Act / Assert

Every test body maps to one logical concept with three clear sections:

```java
@Test
void register_withValidRequest_persistsUser() {
    // Arrange
    var request = new CreateUserRequest("bob@example.com", "bob", "hunter2");

    // Act
    var saved = service.register(request);

    // Assert
    assertThat(saved.email()).isEqualTo("bob@example.com");
    assertThat(saved.passwordHash()).isNotEqualTo("hunter2");
}
```

One assert-group per test. If you find yourself writing two unrelated `assertThat` blocks, split into two tests.

---

### Spring slice tests — when integration is unavoidable

Use the narrowest slice that covers what you need to test. Never reach for `@SpringBootTest` when a slice will do.

```java
// Controller layer only — no JPA context, no full boot
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired MockMvc mvc;
    @MockBean  UserService userService;

    @Test
    void getUser_returnsOk() throws Exception {
        given(userService.findById(1L)).willReturn(new UserDto(1L, "alice@example.com"));
        mvc.perform(get("/users/1"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
}

// Repository layer only — in-memory H2, no web context
@DataJpaTest
class UserRepositoryTest {
    @Autowired UserRepository repo;

    @Test
    void findByEmail_returnsUser() {
        repo.save(new User("alice@example.com", "alice", "hash"));
        assertThat(repo.findByEmail("alice@example.com")).isPresent();
    }
}
```

---

## Common traps

- **"Tests after work shipped are fine"** — you lose the design feedback TDD provides. The test written first shapes the API; the test written last documents what you built, flaws included.
- **"Mocking everything is clean"** — over-mocking tests the mocks, not the code. Prefer real collaborators for simple value objects; mock only external I/O and expensive infrastructure.
- **"One test file for all tests"** — independent fixtures become impossible. One test class per production class is the baseline; split further when a class has distinct concern groups.
- **"@SpringBootTest for every test"** — makes the suite slow and fragile. Reserve it for end-to-end smoke tests; use slices or plain JUnit for everything else.

---

## When the rule doesn't apply

- **Prototype / spike code** that will be deleted before merging. TDD overhead is not justified for throwaway exploration.
- **Trivial delegating adapters** with no logic to verify (e.g., a one-line wrapper that forwards calls unchanged). Test the real logic through the collaborator's own suite.

---

## Cross-references

- SKILL.md rules: 15, 16.
- Related: [solid.md](solid.md) (constructor injection makes classes testable without Spring context), [craftsmanship.md](craftsmanship.md) (professional discipline around test discipline).
