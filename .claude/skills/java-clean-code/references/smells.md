# Code smells — cross-reference

Look up by symptom. Each entry links to the rule in SKILL.md and the
reference file for depth.

## Function-level smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| Method over 20 lines | 2, 6, 18 | [functions.md](functions.md) |
| Boolean flag argument | 4 | [functions.md](functions.md) |
| Method with ≥4 arguments | 3 | [functions.md](functions.md) |
| Return code / null to signal failure | 9 | [functions.md](functions.md) |
| Deep nesting (>2 levels) | 18 | [functions.md](functions.md) |
| Hidden side effect in getter/query | 5, 8 | [functions.md](functions.md) |
| Near-duplicate code blocks | 13 | [functions.md](functions.md) |

## Class-level smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| Class doing persistence + email + audit | 12 | [solid.md](solid.md) |
| Public fields on a domain object | 11 | [objects-and-data.md](objects-and-data.md) |
| Trainwreck (`a.getB().getC().doD()`) | 10 | [objects-and-data.md](objects-and-data.md) |
| `@Autowired` on field | 19 | [solid.md](solid.md) |
| God class (>300 lines, many concerns) | 12 | [classes-and-boundaries.md](classes-and-boundaries.md) |
| Third-party type leaking into domain | 12 | [classes-and-boundaries.md](classes-and-boundaries.md) |

## Naming smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| `data`, `info`, `tmp`, single letters outside loops | 1 | [naming.md](naming.md) |
| `getXxx` method that mutates | 5, 8 | [functions.md](functions.md) |
| Inconsistent verbs (`fetch` + `get` + `retrieve`) | 14 | [naming.md](naming.md) |
| Magic number or unexplained literal | 17 | [naming.md](naming.md) |
| Comment restating what the code does | 20 | [comments.md](comments.md) |

## Test smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| Test written after the code shipped | 15 | [testing.md](testing.md) |
| Test depends on wall-clock time | 16 | [testing.md](testing.md) |
| Test uses shared mutable fixture | 16 | [testing.md](testing.md) |
| `@SpringBootTest` for a unit test | 16 | [testing.md](testing.md) |
| Over-mocked test (mocks all collaborators) | 16 | [testing.md](testing.md) |

## Architecture smells

| Smell | Rule(s) | Deep dive |
|---|---|---|
| JPA entity in the controller layer | 12 | [architecture.md](architecture.md) |
| Business rule inside a `@RestController` | 12 | [architecture.md](architecture.md) |
| Use case depending on Spring annotations | 12 | [architecture.md](architecture.md) |
| Domain class importing `org.springframework.*` | 19 | [architecture.md](architecture.md) |
