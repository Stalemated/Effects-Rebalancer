---
description: Comprehensive Java Code Review Checklist & Workflow
---

# Java Code Review Workflow

This workflow guides you through a thorough review of Java code, focusing on core language features, common pitfalls, performance, and security.

## 1. Clean Code & Readability
- [ ] **Naming:** Do variable/method names follow `camelCase`? Do class names follow `PascalCase`? Constants to `UPPER_SNAKE_CASE`?
- [ ] **Clarity:** Are names descriptive? (e.g., `retrieveUserData` vs `getData`)
- [ ] **Formatting:** Does it follow the project's style (e.g., Google Java Style)?
    - Braces on same line?
    - Indentation consistent?
- [ ] **Comments:** Do Javadocs exist for public methods? specific implementation details explained?
    - *Avoid* comments that just restate the code (e.g. `i++; // increment i`).
- [ ] **Complexity:** Are methods short and focused (SRP)? Usage of `Extract Method`?

## 2. Common Java Pitfalls
- [ ] **Equality:** Usage of `.equals()` instead of `==` for Objects/Strings?
    - Null-safe comparison: `Objects.equals(a, b)` or `"constant".equals(variable)`.
- [ ] **Null Safety:** 
    - Usage of `Optional` instead of returning null?
    - Null checks on parameters?
    - `@Nullable` / `@NotNull` annotations used?
- [ ] **Resources:** Are `AutoCloseable` resources (Streams, Connections) managed in `try-with-resources`?
- [ ] **Switch:** Are all enum cases covered? Usage of `default` branch?
- [ ] **Exceptions:**
    - No empty `catch` blocks?
    - Specific exceptions caught instead of generic `Exception`?
    - Exceptions logged properly (including stack trace)?

## 3. Modern Java Features (Java 17/21+)
- [ ] **Records:** Used for data carriers instead of POJOs with boilerplate?
- [ ] **Pattern Matching:** Used `instanceof` with binding?
- [ ] **Text Blocks:** Used for multi-line strings (SQL, JSON)?
- [ ] **Var:** Used judicially? (Good for long implementations, bad if type is unclear).

## 4. Performance
- [ ] **Collections:** Correct type used? (`HashSet` for lookups, `ArrayList` for iteration).
    - Initial capacity defined if known?
- [ ] **Streams:** Used effectively?
    - Avoid `stream().forEach()` if a simple loop is cleaner.
    - Avoid massive chains that are hard to debug.
- [ ] **String Concatenation:** Usage of `StringBuilder` inside loops?
- [ ] **O(n):** detecting nested loops on large datasets?

## 5. Thread Safety & Concurrency
- [ ] **State:** Is class state mutable? If so, is it guarded?
- [ ] **Collections:** Usage of `ConcurrentHashMap` vs `HashMap` in multi-threaded contexts?
- [ ] **Thread Creation:** Avoid manual `new Thread()`. Use `ExecutorService`.

## 6. Logic & Correctness
- [ ] **Off-by-one:** Check array/list indices.
- [ ] **Modifying Lists:** Not removing from a list while iterating (unless using Iterator/Stream).

## 7. Security (Basic)
- [ ] **Injection:** Parameterized SQL queries?
- [ ] **Logging:** No sensitive data (passwords, tokens) in logs?
- [ ] **Input:** Validated at the boundary?

## 8. Minecraft/Fabric Specific (If Applicable)
- [ ] **Side Safety:** Client-only code isolated from Server? (`EnvType.CLIENT` checks).
- [ ] **Registries:** Registered at the correct time (ModInitializer)?
- [ ] **Mixin:** `CallbackInfo` cancelled correctly? Locals captured safely?

---
*Run this workflow by manually checking off these items or using them as a reference during review.*
