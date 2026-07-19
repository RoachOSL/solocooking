# Project notes

SoloCooking-specific facts and decisions. Keep reusable engineering and
architecture rules in `ARCHITECTURE.md`; keep domain and product decisions for
this repository here.

## Domain model

- The `ingredient` module owns the ingredient catalog.
- Ingredient names are stored normalized in the `name` field. Do not add a
  separate `normalizedName` field unless the product later needs to preserve a
  display name distinct from the searchable unique name.
- Ingredient name normalization trims surrounding whitespace, lowercases with
  `Locale.ROOT`, and collapses internal whitespace sequences to a single space.
- Creating a duplicate ingredient returns a conflict instead of behaving as an
  idempotent create operation. If the product needs idempotent ingredient
  creation later, introduce that as an explicit endpoint or command.
- Ingredient creation checks duplicates before saving. Do not catch broad
  database integrity exceptions in the service just to map rare concurrent
  duplicate creates; solve that deliberately later if the product needs it.
- The `recipe` module owns `RecipeEntity -> RecipeSectionEntity ->
  RecipeIngredientEntity`.
- `RecipeIngredientEntity` stores an `ingredientId` instead of referencing
  `IngredientEntity` directly.
- Recipe ingredient amounts are positive decimals with up to nine integer digits
  and three fractional digits. Persist them as `numeric(12,3)` and enforce
  positivity in both request validation and the database schema.
- Recipe creation validates referenced ingredient IDs through
  `IngredientFacade`.
- Deleting a recipe is idempotent. `DELETE /recipes/{recipeId}` returns no
  content when the recipe is deleted or was already absent.
- Recipe sections and recipe ingredients store a persisted `position` field.
  Create requests do not accept `position`; the backend assigns positions from
  the order of the submitted lists.
- Recipe create requests require at least one section, and each section requires
  at least one ingredient.
- Do not use JPA `@OrderBy` on recipe collections. Sort by `position`
  explicitly at the use-case or API boundary when ordered output is needed.
- Create recipe aggregates through `RecipeFactory`. The factory translates
  create requests into new entities and calls aggregate methods. `RecipeEntity`
  and `RecipeSectionEntity` own the invariants for child links and positions
  through domain methods such as `replaceSections` and `replaceIngredients`; do
  not hide that logic in MapStruct `@AfterMapping` hooks.
- The `recipe` module intentionally uses a DDD-lite aggregate style because the
  module will support structural operations such as add, remove, reorder, move,
  and duplicate. This is not a hard safety boundary against all bad code inside
  the package; it is the preferred structure for centralizing child-link and
  position invariants. Simpler modules, such as `ingredient`, can stay
  service-oriented and CRUD-friendly.

## Naming

- Use plain `id` for an entity or DTO field that represents that object's own
  identifier.
- Use qualified identifier names, such as `recipeId`, `ingredientId`, or
  `sectionId`, for method parameters, path variables, cross-module references,
  and DTO fields that point to another object.

## Infrastructure

- The grouped OpenAPI document is published as `SoloCooking API`, version `v1`,
  and defaults response media types to `application/json`.
- Use the latest stable PostgreSQL minor version for local development and
  Testcontainers. Pin the exact Docker image tag instead of using `latest`, and
  update `docker-compose.yml` and integration test containers together.
- For PostgreSQL 18 and newer, mount the persistent volume at
  `/var/lib/postgresql`; the image stores database files in a version-specific
  subdirectory such as `/var/lib/postgresql/18/docker`.
- The public API currently allows CORS requests from any origin without
  credentials. Revisit allowed origins, credentials, and CSRF together when
  introducing cookie or session authentication.
- Database migrations are not configured while the project has no persistent
  deployment environment. Introduce a migration tool and replace Hibernate
  schema updates with validation before creating the first persistent
  environment.

## Continuous integration and delivery

- GitHub Actions `.github/workflows/ci.yml` runs for pull requests and pushes
  to `main`.
- CI runs `code-quality`, `build`, `unit-tests`, and `integration-tests` in
  that order. Integration tests run last to fail fast before starting
  Testcontainers.
- CI publishes JUnit XML results through the local `publish-test-report`
  composite action, which wraps `dorny/test-reporter`.
- A newer run of the same workflow for the same Git ref cancels the older run.
- Continuous delivery is not configured. Add it only after choosing the
  deployment environment and release strategy.
