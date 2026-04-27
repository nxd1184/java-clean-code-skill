# java-clean-code

> Clean Code principles, applied to Java by Claude Code — every time you write or review.

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![Java 17](https://img.shields.io/badge/Java-17%20LTS-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![Claude Code](https://img.shields.io/badge/Claude%20Code-skill-D77757)
![Markdown](https://img.shields.io/badge/-Markdown-000000?logo=markdown&logoColor=white)
![Java](https://img.shields.io/badge/-Java-ED8B00?logo=openjdk&logoColor=white)
![TDD](https://img.shields.io/badge/TDD-RED%E2%86%92GREEN-brightgreen)

**20 rules · 12 references · 10 before/after pairs (35 Java files)**

---

## Quickstart (60 seconds)

```bash
git clone https://github.com/nxd1184/java-clean-code-skill.git
cd java-clean-code-skill && ./install.sh
```

Then in Claude Code:

> *"Review UserService.java with the `java-clean-code` skill."*

That's it. Claude loads the 20-rule checklist and reviews your file with rule-cited fixes.

---

## Contents

- [Why this exists](#why-this-exists)
- [Quick demo: before / after](#quick-demo)
- [How it works — the 20 rules](#how-it-works--the-20-rules)
- [Install](#install)
- [How to invoke](#how-to-invoke)
- [Examples gallery](#examples-gallery)
- [Repo layout](#repo-layout)
- [Contributing & TDD discipline](#contributing--tdd-discipline)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Credits](#credits)
- [License](#license)

---

## Why this exists

> Clean code reads like well-written prose. — *Clean Code*, Ch. 1

Clean code principles work best **applied inline while you're writing or reviewing**, not looked up afterwards. This skill packages 20 high-ROI rules into a checklist Claude Code applies on demand — covering naming, function shape, SOLID, TDD, concurrency, and clean architecture.

**You get:**

- A fast 20-rule checklist (loaded in <500 words on every invocation).
- 12 topic references (`naming.md`, `functions.md`, `solid.md`, …) loaded on demand for depth.
- 10 before/after Java pairs (35 files) grounded in Spring Boot 3, plain Java, and pure domain logic.
- A TDD-discipline harness: every skill change starts with a failing pressure scenario and ends with a verified GREEN run.

---

## Quick demo

Drop this `OrderService` into Claude Code with the skill loaded:

```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository repo;
    @Autowired
    private PaymentClient paymentClient;

    public void processOrder(Long orderId, boolean isExpedited,
                             boolean sendEmail, boolean auditLog,
                             String notes) {
        Order o = repo.findById(orderId).get();
        if (o.getCustomer().getAddress().getCountry().equals("US")) {
            paymentClient.charge(
                o.getCustomer().getPaymentMethod().getCard(),
                o.getTotal());
            if (isExpedited) { /* ... 30 more lines ... */ }
        }
    }
}
```

Then ask:

> *"Review this with the `java-clean-code` skill."*

Claude responds with rule-cited fixes (excerpt):

> **DIP violation** (`@Autowired` field) at line 3 — switch to constructor injection.
>
> **Args + Flags** (5 args, 3 booleans) at line 8 — extract a `record` parameter object.
>
> **Demeter trainwreck** at line 13 — `o.getCustomer().getAddress()...` chains through 4 objects. Move the country check onto `Order` as `shipsToUS()`.
>
> **Hidden side effect / CQS** at line 14 — `processOrder` both validates and charges. Split into `validateOrder` (query) and `chargeOrder` (command).

Then it produces the after:

```java
@Service
public class OrderService {
    private final OrderRepository repo;
    private final PaymentClient paymentClient;
    private final OrderValidator validator;

    public OrderService(OrderRepository repo,
                        PaymentClient paymentClient,
                        OrderValidator validator) {
        this.repo = repo;
        this.paymentClient = paymentClient;
        this.validator = validator;
    }

    public void chargeOrder(OrderId id, ShipmentSpeed speed) {
        Order order = repo.findById(id);
        validator.requireShippable(order);
        paymentClient.charge(order.payment(), order.total());
    }
}
```

**No re-reading docs. No "did I follow SOLID?" guesswork. Just rule-cited diffs.**

---

## How it works — the 20 rules

The skill loads a compact 20-rule checklist (under 500 words) on every invocation. Each rule links to a topic reference for depth.

| # | Rule | Reference |
|---|---|---|
| 1 | Names reveal intent | [naming](.claude/skills/java-clean-code/references/naming.md) |
| 2 | Functions do one thing | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 3 | ≤3 arguments per function | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 4 | No flag (boolean) arguments | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 5 | Command-Query Separation | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 6 | One abstraction level per function | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 7 | Stepdown rule (newspaper) | [formatting](.claude/skills/java-clean-code/references/formatting.md) |
| 8 | No hidden side effects | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 9 | Exceptions over error codes | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 10 | Law of Demeter — no trainwrecks | [objects-and-data](.claude/skills/java-clean-code/references/objects-and-data.md) |
| 11 | Objects hide data; expose behavior | [objects-and-data](.claude/skills/java-clean-code/references/objects-and-data.md) |
| 12 | Single Responsibility Principle | [solid](.claude/skills/java-clean-code/references/solid.md) |
| 13 | DRY | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 14 | Consistent vocabulary | [naming](.claude/skills/java-clean-code/references/naming.md) |
| 15 | Write the test first (TDD) | [testing](.claude/skills/java-clean-code/references/testing.md) |
| 16 | Tests are FIRST | [testing](.claude/skills/java-clean-code/references/testing.md) |
| 17 | No magic numbers | [naming](.claude/skills/java-clean-code/references/naming.md) |
| 18 | Early returns over deep nesting | [functions](.claude/skills/java-clean-code/references/functions.md) |
| 19 | Constructor injection (DIP) | [solid](.claude/skills/java-clean-code/references/solid.md) |
| 20 | Comments earn their keep | [comments](.claude/skills/java-clean-code/references/comments.md) |

Additional depth: [smells.md](.claude/skills/java-clean-code/references/smells.md) (symptom lookup), [architecture.md](.claude/skills/java-clean-code/references/architecture.md) (clean architecture layers), [concurrency.md](.claude/skills/java-clean-code/references/concurrency.md), [craftsmanship.md](.claude/skills/java-clean-code/references/craftsmanship.md).

---

## Install

### Option 1 — Claude Code plugin marketplace

```
/plugin marketplace add nxd1184/java-clean-code-skill
/plugin install java-clean-code@java-clean-code-skill
```

### Option 2 — Local symlink

```bash
git clone https://github.com/nxd1184/java-clean-code-skill.git
cd java-clean-code-skill
```

**macOS / Linux:**

```bash
./install.sh       # install
./uninstall.sh     # uninstall
```

**Windows (PowerShell):**

```powershell
.\install.ps1      # install
.\uninstall.ps1    # uninstall
```

> **Windows note:** symlink creation requires **Developer Mode** (Settings → Privacy & security → For developers) *or* an Administrator PowerShell session. You may also need: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`.

Both variants symlink `.claude/skills/java-clean-code` into `~/.claude/skills/` (or `%USERPROFILE%\.claude\skills\` on Windows). Edits to the repo take effect in the next Claude Code session — no reinstall needed.

### Option 3 — Upload to Claude Desktop / claude.ai

Package the skill folder as a zip, then upload via Claude's skill upload UI.

**macOS / Linux:**

```bash
./package.sh
# writes dist/java-clean-code.zip
```

**Windows (PowerShell):**

```powershell
.\package.ps1
# writes dist\java-clean-code.zip
```

The zip contains only `SKILL.md` + `references/` (13 files, ~70 KB) — nothing from the repo's dev tooling, examples, or `.git/`.

---

## How to invoke

From any Claude Code session, say:

- *"Using the `java-clean-code` skill, implement a POST /orders endpoint that creates an order. Constructor injection, request `record`, test-first."*
- *"Review `src/main/java/com/example/OrderService.java` with the `java-clean-code` skill. Cite each violation by rule name."*
- *"What does SRP mean for this `UserService` that does persistence, email, and audit? Use the skill."*

**Prompt cheatsheet:** See [`PROMPTS.md`](./PROMPTS.md) for worked examples covering writing, reviewing, refactoring, and principle questions — plus the anti-prompts that produce slow/generic responses.

---

## Examples gallery

10 before/after pairs (35 `.java` files total) demonstrating the skill on three kinds of Java code. Each pair shows the before, the after, and which rule(s) the diff demonstrates.

### Spring Boot

| Pair | Rules |
|---|---|
| [`FatUserController` → `UserController`](./examples/) | SRP, ≤3 args, DIP, DTO |
| [`SmellyOrderService` → `OrderService`](./examples/) | SRP, Demeter, exceptions over error codes |
| [`BooleanFlagOrderService` → `OrderProcessingService` + `NotificationPolicy`](./examples/) | No flag args, SRP |
| [`MixedConcernsPaymentService` → `PaymentValidator` + `PaymentCharger`](./examples/) | CQS, no hidden side effects |
| [`HardcodedRetryJob` → `OrderRetryJob` + `RetryProperties`](./examples/) | No magic numbers |

### Plain Java / library

| Pair | Rules |
|---|---|
| [`DuplicatedFileParsers` → `GenericFileParser` + `RecordParser`](./examples/) | DRY |
| [`PyramidValidator` → `GuardedValidator`](./examples/) | Early returns over deep nesting |
| [`UntypedDataProcessor` → `AccountTransfer` + `AccountTransferService`](./examples/) | Names reveal intent, objects hide data |

### Domain logic (no framework)

| Pair | Rules |
|---|---|
| [`MonolithicInvoiceCalculator` → `InvoiceCalculator`](./examples/) | One abstraction level, stepdown |
| [`OverCommentedCounter` → `Counter`](./examples/) | Comments earn their keep |

---

## Repo layout

```
java-clean-code-skill/
├── .claude/skills/java-clean-code/
│   ├── SKILL.md                  ← top-20 rule checklist (loaded every invocation)
│   └── references/*.md           ← 12 topic deep-dives (loaded on demand)
├── .claude-plugin/
│   ├── plugin.json               ← plugin manifest
│   └── marketplace.json          ← marketplace listing
├── examples/
│   ├── before/                   ← violation snippets
│   └── after/                    ← cleaned versions
├── tests/
│   ├── scenarios.md              ← TDD pressure scenarios
│   ├── red-baseline.md           ← captured violations without skill
│   └── green-verification.md     ← compliance verified with skill
├── install.sh / install.ps1      ← symlink install (macOS/Linux/Windows)
├── uninstall.sh / uninstall.ps1  ← symlink removal
├── package.sh / package.ps1      ← zip the skill for Claude Desktop upload
├── PROMPTS.md                    ← invocation cheatsheet
├── CLAUDE.md                     ← contributor conventions
├── README.md                     ← you are here
└── LICENSE                       ← MIT
```

---

## Contributing

Contributions welcome — see [CONTRIBUTING.md](CONTRIBUTING.md) for full guidelines, including the TDD Iron Law for skill changes, the word/line budgets, and the PR process.

- Add a new before/after example pair (10 pairs today; more rules waiting)
- Add a pressure scenario in `tests/scenarios.md`
- Improve a reference file
- File issues for skill behavior that doesn't match the rule

---

## Troubleshooting

**Claude doesn't seem to load the skill.**
Make sure you invoke it explicitly: *"Using the `java-clean-code` skill, …"*. The skill description targets natural-language triggers but the explicit name is the most reliable.

**Windows: `New-Item -ItemType SymbolicLink` fails with `UnauthorizedAccessException`.**
Enable Developer Mode (Settings → Privacy & security → For developers) or run PowerShell as Administrator. See the Windows note in the Install section.

**`./install.sh` fails with "Permission denied".**
Make it executable first: `chmod +x install.sh uninstall.sh package.sh`.

**`wc -w SKILL.md` reports a different count than expected on macOS.**
macOS `wc` counts differently than GNU `wc` for some Unicode. Both should be under 500 — the budget has comfortable headroom.

**Skill cites violations as "R10" / "R12" instead of rule names.**
You're on an older version. Update via `git pull` and reinstall — the skill now cites by name (Demeter, SRP, DIP, etc.).

---

## FAQ

**Does this work outside Spring Boot?**
Yes. The 20 rules are language-agnostic Java. Only 5 of 12 reference files mention Spring; the rest (`naming.md`, `functions.md`, `comments.md`, `formatting.md`, `concurrency.md`, `craftsmanship.md`, `objects-and-data.md`) are pure clean-code. Examples are mixed: Spring Boot, plain Java, and pure domain logic.

**Java 8 / 11 compatibility?**
Rules apply identically. Some examples use `record` (Java 14+) and `var` (Java 10+); substitute plain classes and explicit types for older versions. The principles don't change.

**Why 20 rules, not all 37 chapters of *Clean Code*?**
Top-20 are the highest-ROI day-to-day decisions. Topics like classes, error handling, smells lookup, concurrency, and craftsmanship are still covered — see `references/`.

**How do I know Claude is actually applying the skill?**
Look for **rule-cited fixes by name** — *"Demeter violation at line 12"*, *"SRP: this class has 3 reasons to change"*. Generic *"follow SOLID"* responses mean the skill probably didn't load.

**Can I use this with Quarkus / Micronaut / plain JDBC?**
Yes — the rules apply to any Java. The framework-specific Spring examples in `references/architecture.md` and `references/solid.md` translate directly (constructor injection works the same way; SRP is SRP).

**Does this replace a code review?**
No. It catches mechanical violations (Demeter chains, flag args, magic numbers, missing tests). Human review still owns architecture, security, business logic, and naming judgment.

---

## Credits

The 20 rules and topic references are distilled from *Clean Code: A Handbook of Agile Software Craftsmanship* by Robert C. Martin (2008, Prentice Hall) and *Effective Java, 3rd Edition* by Joshua Bloch (2018, Addison-Wesley). This skill is an unofficial application of those principles to Java — not affiliated with or endorsed by either author or publisher.

---

## License

MIT — see [LICENSE](LICENSE).

This skill is provided "as is" without warranty. The maintainer does not audit or guarantee that Claude's application of these rules will be correct in every situation. Review Claude's suggestions before applying them. The maintainer accepts no liability for issues arising from using this skill.

If you find an issue with skill behavior, please [open an issue](https://github.com/nxd1184/java-clean-code-skill/issues) and we'll address it.