# Architecture guidelines

Reusable engineering and architecture backend rules for this repository.
Project-specific facts and decisions belong in `PROJECT_NOTES.md`. Agent
workflow rules live in `CLAUDE.md` / `AGENTS.md`. When a reusable
architectural or engineering decision is agreed, add it here in the relevant
section.

## Repository hygiene

- Do not add AI attribution anywhere in the repository or its Git history.
  Do not add AI co-author trailers, generation footers, or similar attribution
  to commits, pull requests, source files, or documentation.

## Architecture

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
- Comments are the exception, not the norm. Write one only when it states a
  non-obvious constraint or reason the code itself cannot show, for example why
  a no-op interceptor exists, that a type mirrors a backend DTO, or why a lint
  rule is disabled for a path. Never narrate what the next line does, restate a
  rule already documented in these Markdown files, or leave tool or template
  boilerplate links. Short section labels in configuration files, such as
  `# Test output` in `.gitignore`, are fine. Test `given`, `when`, and `then`
  markers follow the test structure rules below.
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
- Keep test data close to the module that owns it. Domain-specific constants and
  Mother classes shared by multiple test suites live under `src/testFixtures` in
  that module's test package, for example `recipe` or `ingredient`. Suite-only
  mechanics, such as in-memory repositories and module service tests, stay in the
  suite that uses them.
- The `common` test package is reserved for genuinely shared test
  infrastructure, such as comparison configuration or generic in-memory
  repository support. Do not put module-specific domain data in `common`.
- Test objects are created through Mother classes, for example `RecipeMother`.
- Do not add private test helper methods that only delegate to a Mother builder and call `.build()`. Call the Mother directly in the test unless the helper also creates meaningful scenario state, such as persisted data.
- When tests in one module need persisted data owned by another module, use the
  owning module's public test fixture helper and its public facade. Do not
  expose entities or repositories across module boundaries just to arrange test
  state.
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
- Controller slice tests use `@WebMvcTest` with `MockMvcTester`, AssertJ
  assertions, `@MockitoBean` for facade dependencies, and the application-provided
  `ObjectMapper` injected from the test slice. Legacy raw `MockMvc` tests may use
  `andExpectAll(...)` when asserting status and body for the same response.
- Controller tests compare response bodies against expected JSON files stored in
  `src/test/resources`, so endpoint contracts stay visible outside Java object
  serialization code. Controller test requests should use shared constants/helpers for
  the API servlet path instead of repeating literal base paths.
- Paged controller responses use `PageResponse.from(...)` instead of returning
  Spring Data `Page` directly, so the API response shape stays explicit and
  stable.
- Name controller expected JSON files after the controller method or endpoint
  scenario that uses them, for example `get-recipe-response.json`, so multiple
  response contracts in one resource directory stay easy to distinguish.
- Integration tests use a separate Gradle `integrationTest` source set and task.
  Keep integration test class names ending with `IT`, and keep unit, service,
  and controller tests under the regular `test` task.
- Integration tests exercise public facades through a real Spring context and
  PostgreSQL Testcontainers. The shared `BaseIntegrationTest` should use
  `@ServiceConnection` for Spring Boot Testcontainers wiring.
- Do not put `@Transactional` on the shared integration test base by default.
  Test production transaction boundaries and isolate data with explicit database
  cleanup between tests instead.
- In facade integration tests, compare returned DTOs recursively with the shared
  comparison configuration whenever generated fields such as IDs or audit
  timestamps are not the behavior under test.

## Continuous integration

- Run unit tests and integration tests as separate CI steps.
- Use `gradle/actions/setup-gradle` for Gradle dependency and wrapper caching.
  Do not add overlapping `actions/cache` or `setup-java` Gradle cache
  configuration.
- Pin every external GitHub Action to a full commit SHA with a version comment,
  including official `actions/*` Actions. A mutable tag can be retargeted to
  silently run different external code with access to the workflow token and
  granted permissions. Upgrade an Action only through an intentional SHA change
  visible in the repository diff. Local `./.github/actions/*` Actions are
  versioned with this repository and do not use external SHAs.
- Cancel an in-progress CI run when a newer commit starts the same workflow for
  the same Git ref.
