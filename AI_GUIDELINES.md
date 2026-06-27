# AI guidelines

Before presenting or running any executable command, Codex first explains to the
user what the command does and what effects it may have.

Commits and pushes are done only by the user. Codex prepares and tests changes,
but does not run `git commit` or `git push`.

## Architecture

- When Codex and the user agree on an architectural decision, add it to this
  file in the relevant section so the project keeps a durable record of chosen
  conventions and future direction.
- Keep module boundaries explicit and independent. Internal implementation
  details, such as entities, repositories, mappers, and services, should stay
  package-private whenever possible.
- Expose a module through a public facade interface. Other modules should use
  the facade to request data or perform operations instead of depending on the
  module's internal classes.
- Do not expose JPA entities across module boundaries. Cross-module references
  should use stable identifiers, DTOs, or facade methods.
- Use JPA relations inside a module aggregate when they express ownership and a
  shared lifecycle. Across module boundaries, prefer IDs plus explicit
  validation through the owning module's facade.
- Prefer simple, maintainable designs guided by SOLID, DRY, KISS, and YAGNI.
  Favor composition over inheritance when it keeps responsibilities clearer and
  avoids unnecessary coupling.

## Code style

- Use `var` for local variables when the type is obvious from the right-hand side
  and readability does not suffer.
- Use descriptive variable names that clearly communicate meaning and purpose.
- After code changes, run the available formatting and import cleanup tool. If
  the project does not provide such a Gradle tool, clean up imports manually
  according to the project style.

## Test style

- Shared immutable test data is exposed directly as clearly named constants,
  without unnecessary getters.
- Keep test data close to the module that owns it. Domain-specific constants,
  Mother classes, in-memory repositories, and module service tests live in that
  module's test package, for example `recipe` or `ingredient`.
- The `common` test package is reserved for genuinely shared test
  infrastructure, such as comparison configuration or generic in-memory
  repository support. Do not put module-specific domain data in `common`.
- Test objects are created through Mother classes, for example `RecipeMother`.
- For types with a builder, the Mother returns a new prefilled builder each time.
  The method name ends with `Builder`, and `.build()` is called explicitly in the
  test case.
- If a type has no builder, the Mother returns a new instance every time. Do not
  share mutable objects or builders between tests.
- Stateless helper classes are marked `final`, their methods are explicitly
  `static`, and construction is blocked with
  `@NoArgsConstructor(access = AccessLevel.PRIVATE)`.
- When possible, compare whole objects recursively instead of asserting
  individual fields.
- Generated or time-dependent fields, such as `createdAt` and `updatedAt`, are
  ignored through shared comparison configuration when they are not the subject
  of the test.
- Service tests use in-memory repositories instead of mocks when behavior depends
  on persisted state.
- Shared test repository operations live in generic `InMemoryRepository<T, ID>`.
  Entity-specific repositories contain only behavior specific to that entity and
  custom repository methods.
- In-memory repositories simulate only behavior needed by the tested case. Do
  not add empty hooks or set audit fields when the test ignores those fields.
- Each test gets a fresh in-memory repository instance so data cannot leak
  between tests.
- Test class dependencies are initialized directly in `private final` fields when
  no per-test setup is needed.
- Use `@BeforeEach` only when every test needs additional initialization or an
  explicit state reset.
