# AI guidelines

Before presenting or running any executable command, Codex first explains to the
user what the command does and what effects it may have.

Commits and pushes are done only by the user. Codex prepares and tests changes,
but does not run `git commit` or `git push`.

## Architecture

- When Codex and the user agree on a reusable architectural or engineering
  decision, add it to this file in the relevant section. Project-specific domain
  and product decisions belong in `PROJECT_GUIDELINES.md`.
- When the user adds GitHub code review comments, Codex reads and discusses
  them with the user in the local console first. Agreed changes are then applied
  locally, tested, and summarized for the user. Codex does not publish GitHub
  comments unless explicitly asked.
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
- Prefer detailed `debug` logs for normal application flow, validation
  decisions, and useful identifiers. Use `info` logs sparingly for genuinely
  high-level lifecycle or operational events, not routine CRUD flow.
- After code changes, run the available formatting and import cleanup tool. If
  the project does not provide such a Gradle tool, clean up imports manually
  according to the project style.
- Keep Gradle dependency and plugin coordinates in `gradle/libs.versions.toml`.
  Reference them from build scripts through the version catalog (`libs.*`)
  instead of hardcoding versions or coordinates directly in `build.gradle`.
- In controllers, use short command method names such as `create` and
  `deleteById` when the controller class already names the resource. Keep read
  methods explicit, for example `getRecipe` and `getRecipes`, because the
  singular/plural distinction improves scanability.

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
- Stateless helper classes use Lombok `@UtilityClass`.
- When possible, compare whole objects recursively instead of asserting
  individual fields.
- Generated or time-dependent fields, such as `createdAt` and `updatedAt`, are
  ignored through shared comparison configuration when they are not the subject
  of the test.
- Service tests use in-memory repositories instead of mocks when behavior depends
  on persisted state.
- Do not create in-memory implementations of module facades for tests. For
  cross-module facade dependencies, use a mock in focused service tests or wire
  the real module in broader integration tests.
- Service tests are focused unit-style tests: use in-memory repositories for the
  service's own persistence boundary and mocks for external module facades or
  collaborators. Broader facade/integration tests can wire real modules,
  database infrastructure, and Testcontainers in a separate test layer.
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
- Structure test methods with `// given`, `// when`, and `// then` comments.
  Use `// when & then` when the action is embedded directly in the assertion.
  Keep the sections meaningful and omit only when a section is genuinely empty.
- When testing HTTP-aware exceptions such as `ResponseStatusException`, use
  fluent throwable assertions with `isInstanceOfSatisfying(...)` and assert the
  status code plus response body detail inside the assertion lambda. Do not
  duplicate the status assertion by also checking `exception.getBody().getStatus()`.
- In MockMvc tests, prefer `andExpectAll(...)` when asserting status and body
  for the same response.
- Controller tests compare response bodies against expected JSON files stored in
  `src/test/resources`, so endpoint contracts stay visible outside Java object
  serialization code.
- Name controller expected JSON files after the controller method or endpoint
  scenario that uses them, for example `get-recipe-response.json`, so multiple
  response contracts in one resource directory stay easy to distinguish.
