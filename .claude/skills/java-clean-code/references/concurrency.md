# Concurrency

## Core principle

Isolate shared state. Prefer immutability. Use high-level concurrency primitives
over raw `synchronized`. Concurrency bugs are the hardest to reproduce — design
them out rather than debugging them in.

---

## Rules covered

| Topic | Guidance |
|---|---|
| Shared state | Isolate or eliminate |
| Primitives | Prefer `java.util.concurrent` over `synchronized` |
| Java 21+ | Virtual threads (optional — explicitly gated) |

Connects to **Rule 11** (clean design) and supports single-responsibility
thinking from SOLID: each thread pool should own one category of work.

---

## Java examples

### 1. Prefer `ConcurrentHashMap` over `synchronized` Map

```java
// ❌ Manual synchronization — error-prone, easy to forget
private final Map<String, User> cache = new HashMap<>();

public User get(String key) {
    synchronized (this) {
        return cache.get(key);
    }
}

// ✅ ConcurrentHashMap — thread-safe by design
private final Map<String, User> cache = new ConcurrentHashMap<>();

public User get(String key) {
    return cache.get(key);
}
```

`ConcurrentHashMap` uses bucket-level locking internally; contention is far
lower than a single `synchronized` block around the whole map. Prefer it
whenever multiple threads read and write the same map.

---

### 2. `AtomicReference` for single-value state

```java
// ❌ Non-atomic read-modify-write
private volatile boolean running = false;

public void toggle() {
    running = !running;   // read and write are two separate ops — not atomic
}

// ✅ AtomicBoolean — updateAndGet applies the function atomically
private final AtomicBoolean running = new AtomicBoolean(false);

public void toggle() {
    running.updateAndGet(v -> !v);   // atomic read-modify-write in one op
}

// For counters: use AtomicLong or AtomicInteger
private final AtomicLong requestCount = new AtomicLong(0);

public void recordRequest() {
    requestCount.incrementAndGet();
}
```

`volatile` guarantees visibility across threads (no stale cache reads) but
does **not** make compound operations atomic. Use `Atomic*` classes whenever
a read-modify-write sequence must be indivisible.

---

### 3. `CompletableFuture` for async pipelines

```java
// ✅ Async pipeline — readable chain, no raw thread management
CompletableFuture.supplyAsync(() -> orderRepository.findById(orderId))
    .thenApply(order -> paymentService.charge(order))
    .thenAccept(receipt -> notificationService.send(receipt))
    .exceptionally(ex -> {
        log.error("Payment failed", ex);
        return null;
    });
```

Each stage declares what happens next; the runtime schedules execution.
Avoid `get()` / `join()` in production paths — they block the calling thread
and defeat the purpose of async composition.

---

### 4. Thread pool sized to backend resources

```java
// ❌ CPU-count threads for I/O-bound work — undersized for DB calls
var pool = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()
);

// ✅ Size to the bottleneck resource (e.g., DB connection pool)
// Leave one connection slot for schema migrations / health checks
var pool = Executors.newFixedThreadPool(dbConnectionPoolSize - 1);
```

Rules of thumb:
- **CPU-bound**: `availableProcessors()` or `availableProcessors() + 1`.
- **I/O-bound**: match the size of the downstream resource pool (DB connections,
  HTTP client connections, etc.).
- Give each pool a `ThreadFactory` with a descriptive name for easier
  thread-dump analysis:

```java
var factory = Thread.ofPlatform()
    .name("order-processor-", 0)
    .factory();
var pool = Executors.newFixedThreadPool(dbConnectionPoolSize - 1, factory);
```

---

### 5. Immutable records as messages

```java
// ✅ Records are implicitly immutable — safe to share across threads
public record OrderEvent(
    String orderId,
    OrderStatus status,
    Instant occurredAt
) {}

// No locks needed; pass freely between threads, queues, or streams
queue.put(new OrderEvent(order.id(), OrderStatus.PAID, Instant.now()));
```

Immutability eliminates the entire class of shared-state bugs. Prefer records
(or value objects with only final fields) for messages passed between threads.

---

### 6. Java 21 only — Virtual threads

> **Java 21+ only. Do NOT use in Java 17 projects.**

```java
// Virtual threads: cheap threads for I/O-bound work; no pool sizing needed
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> processOrder(order));
}
```

Virtual threads are scheduled by the JVM, not the OS. Thousands can exist
simultaneously without the cost of platform threads. They are ideal for
I/O-bound workloads (blocking DB calls, HTTP requests) where you previously
needed a carefully sized thread pool.

**On Java 17:** use a fixed-size `ExecutorService` sized to your I/O bottleneck
(see Example 4). Do not add a Java version gate only in comments — guard at the
build level (`<java.version>21</java.version>` in the POM or a Gradle
toolchain block) so the code fails to compile on an older JDK.

---

## Common traps

### "`synchronized` is enough for everything"

Coarse-grained locking serialises all threads through a single bottleneck.
Use `java.util.concurrent` structures (`ConcurrentHashMap`, `CopyOnWriteArrayList`,
queues from `java.util.concurrent`) to allow safe concurrent access with much
lower contention.

### "One global `ExecutorService` for all tasks"

CPU-bound tasks and I/O-bound tasks have opposite sizing requirements. Mixing
them in a single pool means either CPU tasks starve (pool too large → context
switching overhead) or I/O tasks block unnecessarily (pool too small).
Create separate named pools per responsibility.

### "Volatile makes it thread-safe"

`volatile` ensures every thread reads from main memory rather than a CPU cache.
It does **not** make compound operations (check-then-act, read-modify-write)
atomic. For those, use `AtomicBoolean`, `AtomicLong`, or a proper lock.

### "Virtual threads replace thread pools on all Java versions"

Virtual threads require Java 21. Always add an explicit version gate. Using
`Executors.newVirtualThreadPerTaskExecutor()` on Java 17 fails at runtime, not
compile time, if you somehow compile with `--release 17` but run on an older
JVM — misleading and hard to diagnose. Pin the JDK version in your build tool.

---

## When the rule doesn't apply

- **Single-threaded scripts or CI jobs.** If there is exactly one thread,
  concurrency primitives add noise with zero benefit.
- **`@Test` methods.** JUnit 5 runs tests single-threaded by default.
  Concurrency primitives inside a test method usually signal a design smell —
  the code under test should be testable without requiring multi-threaded
  orchestration. Extract concurrency concerns to an adapter layer and test the
  core logic synchronously.

---

## Cross-references

- [solid.md](solid.md) — SRP: each thread pool should have one responsibility
  (one kind of work, one backing resource).
- [architecture.md](architecture.md) — async adapters and thread pools live in
  the outbound adapter layer; the domain core stays synchronous and testable.

---

## Effective Java additions

### Item 78: Synchronize access to shared mutable data

Without synchronization, threads see stale or torn values. Three mechanisms:

1. **`synchronized`** — atomicity AND visibility. Use for compound actions.
2. **`volatile`** — visibility only (no atomicity). Use for single-write flags.
3. **`java.util.concurrent.atomic`** — atomic primitives without locking.

```java
// WRONG — long writes are not atomic on 32-bit JVMs; reads can tear
private long count;
public void increment() { count++; }

// RIGHT — AtomicLong
private final AtomicLong count = new AtomicLong();
public void increment() { count.incrementAndGet(); }

// RIGHT for compound action — synchronized
private final Map<String, Integer> totals = new HashMap<>();
public synchronized void add(String k, int v) {
    totals.merge(k, v, Integer::sum);
}
```

Spring Boot caveat: `@Transactional` does NOT synchronize across threads —
it bounds DB transactions, not in-memory state.

### Item 80: Executors, tasks, and streams over threads

`new Thread().start()` is rarely the right answer in modern Java: no
back-pressure, errors get swallowed, no concurrency limit, hard to monitor
or shut down.

```java
// BEFORE
new Thread(() -> generateReport(id)).start();

// AFTER — Spring's TaskExecutor
@Service
public class ReportService {
    private final TaskExecutor executor;
    public ReportService(TaskExecutor executor) { this.executor = executor; }

    public void requestReport(Long id) {
        executor.execute(() -> generateReport(id));
    }
}
```

For request-scoped async, prefer `@Async` + `CompletableFuture<T>` so
errors propagate.

**Java 21 angle:** virtual threads (`Executors.newVirtualThreadPerTaskExecutor()`)
make I/O-bound work cheap while keeping the executor model.

### Item 81: Concurrency utilities over wait/notify

`wait()` and `notify()` are low-level and easy to get wrong. The
`java.util.concurrent` toolbox covers nearly every coordination need:

| Need | Use |
|---|---|
| Wait for N events | `CountDownLatch` |
| Limit concurrent access | `Semaphore` |
| Producer-consumer queue | `BlockingQueue` (e.g. `LinkedBlockingQueue`) |
| Wait for several futures | `CompletableFuture.allOf` |
| Periodic task | `ScheduledExecutorService` (or Spring's `@Scheduled`) |

If you're tempted to write `synchronized (x) { x.wait(); }`, stop. There's
a higher-level utility for the case.
