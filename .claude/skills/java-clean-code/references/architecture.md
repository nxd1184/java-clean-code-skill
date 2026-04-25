# Architecture — Emergent Design and Clean Architecture

## Core Principle

Architecture is where dependencies point. Business rules must not depend on
frameworks, databases, or HTTP. The **dependency rule**: outer layers depend on
inner layers, never the reverse. If your `Order` class imports
`javax.persistence` or `org.springframework`, the domain has leaked into
infrastructure — that is an architectural violation.

Frameworks, databases, and transport protocols are delivery mechanisms. They
change for operational reasons unrelated to business logic. Isolating them
behind boundaries means those changes stay contained.

## Rules Covered

- **Rule 12 — SRP at the architectural level**: each layer has exactly one
  reason to change. The domain layer changes when business rules change. The
  adapter layer changes when an external system changes. They must not share
  reasons to change.
- **Rule 19 — DIP at the architectural level**: inner layers define interfaces
  (ports); outer layers provide implementations (adapters). The domain never
  names a concrete infrastructure class.

---

## Clean Architecture Layers (Inside-Out)

### Layer 1 — Entities (domain)

Pure Java. No Spring annotations, no JPA, no HTTP types. Records, value
objects, and domain logic live here.

- Allowed imports: `java.*`, other domain types, nothing else.
- Changes when: core business rules change.

### Layer 2 — Use Cases (application services)

Orchestrate entities to fulfill a single business intent. No HTTP request
objects, no `EntityManager`, no `@Transactional` in the interface.
Implementations may use Spring-managed transactions, but the port interface
stays framework-free.

- Depends on: Layer 1 only.
- Changes when: application workflow changes.

### Layer 3 — Adapters (inbound + outbound)

Translate between the external world and use cases.

- **Inbound adapters**: REST controllers, GraphQL resolvers, message listeners.
  They receive an external signal and call a use-case port.
- **Outbound adapters**: JPA repositories, HTTP clients, S3 adapters. They
  implement a port interface defined in Layer 2.
- Changes when: the external protocol or persistence technology changes.

### Layer 4 — Frameworks (infrastructure)

Spring Boot wiring, JPA configuration, datasource beans, security filters.
Depends on all inner layers. This layer is deliberately thin — mostly
configuration and wiring code.

- Changes when: infrastructure tooling changes (e.g., migrating from Hibernate
  to jOOQ, upgrading Spring Boot major versions).

---

## Spring-Specific Package Mapping

```
com.example.app/
├── domain/                        # Layer 1: Entities — no Spring
│   ├── Order.java                 # record or class, pure Java
│   └── OrderStatus.java           # enum
├── application/                   # Layer 2: Use cases — no HTTP, no JPA
│   ├── PlaceOrderUseCase.java      # interface (port)
│   └── PlaceOrderService.java      # implementation
├── adapter/
│   ├── inbound/
│   │   └── web/
│   │       └── OrderController.java  # @RestController — Layer 3
│   └── outbound/
│       └── persistence/
│           └── JpaOrderRepository.java  # @Repository — Layer 3
└── config/                        # Layer 4: Spring wiring
    └── AppConfig.java
```

Dependency arrows: `config` → `adapter` → `application` → `domain`.
No arrow ever points outward.

---

## Dependency Rule in Code

```java
// ✅ domain/Order.java — pure Java, no Spring imports
public record Order(String id, long totalCents, OrderStatus status) {
    public boolean isPending() { return status == OrderStatus.PENDING; }
}
```

```java
// ✅ application/PlaceOrderUseCase.java — interface (port), no framework
public interface PlaceOrderUseCase {
    Order place(CreateOrderRequest request);
}
```

```java
// ✅ adapter/inbound/web/OrderController.java — depends inward on use case interface
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final PlaceOrderUseCase placeOrder;

    public OrderController(PlaceOrderUseCase placeOrder) {
        this.placeOrder = placeOrder;
    }

    @PostMapping
    public Order create(@RequestBody CreateOrderRequest request) {
        return placeOrder.place(request);
    }
}
```

The controller knows about the use-case interface but the interface knows
nothing about HTTP. Adding a CLI adapter later requires no changes to
`PlaceOrderUseCase` or `Order`.

### Outbound port example

```java
// ✅ application/OrderRepository.java — port, defined in Layer 2
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
}

// ✅ adapter/outbound/persistence/JpaOrderRepository.java — adapter, Layer 3
@Repository
public class JpaOrderRepository implements OrderRepository {
    private final OrderJpaRepository jpa; // Spring Data repo

    public JpaOrderRepository(OrderJpaRepository jpa) { this.jpa = jpa; }

    @Override
    public void save(Order order) {
        jpa.save(OrderEntity.from(order)); // maps domain → JPA entity
    }

    @Override
    public Optional<Order> findById(String id) {
        return jpa.findById(id).map(OrderEntity::toDomain);
    }
}
```

`PlaceOrderService` depends on `OrderRepository` (the port, Layer 2), not on
`JpaOrderRepository` (the adapter, Layer 3). Spring's DI wires the concrete
implementation at runtime — this is DIP in practice.

---

## Component Cohesion (Brief)

Three principles guide what goes in the same package:

| Principle | Meaning | Package signal |
|-----------|---------|---------------|
| REP — Release Equivalence | Release together | Same versioning cadence |
| CRP — Common Reuse | Reuse together | Clients import all-or-nothing |
| CCP — Common Closure | Change together | Same reason to change |

**Package by feature, not by layer type.**

Prefer `com.example.orders` over `com.example.controllers`. A feature package
groups everything needed to implement one slice of the system. Changes to the
orders feature touch one package. Package-by-layer spreads one feature change
across `controllers`, `services`, and `repositories` simultaneously.

---

## Common Traps

**"`@Service` can be my use case AND my controller."**
No. Use cases have no HTTP knowledge. A `@Service` that takes
`HttpServletRequest` as a parameter has merged two layers with different reasons
to change. Extract the controller as a thin inbound adapter.

**"Pulling JPA entities into the controller layer."**
Returning a `@Entity` class directly from a `@RestController` ties your HTTP
API to your database schema. Changes to the schema break the API. Map to a
response DTO in the adapter layer.

**"Package-by-layer is fine."**
`com.example.controllers`, `com.example.services`, and `com.example.repositories`
create horizontal cuts. Every feature change touches every layer package. CCP is
violated: files that change together are scattered. Package-by-feature collocates
cohesive code.

**"The framework is the architecture."**
Spring Boot is an outer-layer detail. Designing around `@SpringBootApplication`,
`@Service`, and JPA as first-class concepts means the framework owns the
architecture. If migrating Spring Boot major versions or replacing Hibernate
requires changing business logic, the dependency rule has been violated.

---

## When the Rule Doesn't Apply

- **One-file scripts or utilities.** A single-class CLI tool with no domain
  complexity does not need four packages.
- **Monorepos growing organically.** In early growth, a flat structure is
  acceptable. Introduce explicit layer boundaries once a module has multiple
  adapters (e.g., REST + messaging) or once the team feels friction from
  cross-cutting changes.

Forcing layered architecture onto two-class utilities creates ceremony without
benefit. Apply the dependency rule where the codebase has meaningful
infrastructure variation or multiple inbound/outbound channels.

---

## Cross-References

- **SKILL.md rules**: 12 (SRP), 19 (DIP).
- **[solid.md](solid.md)** — DIP drives the dependency rule; the port/adapter
  split is DIP applied at the architectural level.
- **[classes-and-boundaries.md](classes-and-boundaries.md)** — adapter pattern
  at the boundary; data-mapping between layers.
