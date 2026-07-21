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
- The shared ingredient catalog starts with 30 common English ingredients seeded
  by Flyway. Users can extend the catalog through the ingredient creation API.
  Add later seed data through new migrations instead of editing an applied seed
  migration.
- Ingredient listing returns the full paginated catalog. Ingredient search uses
  a separate `/ingredients/search` endpoint, requires a non-blank name
  fragment, normalizes it case-insensitively, and preserves pagination.
- Creating a duplicate ingredient returns a conflict instead of behaving as an
  idempotent create operation. If the product needs idempotent ingredient
  creation later, introduce that as an explicit endpoint or command.
- Ingredient updates are partial. An omitted or null name leaves the stored name
  unchanged; a supplied name uses the same normalization and uniqueness rules as
  creation.
- Ingredient deletion is idempotent. Deleting an absent ingredient succeeds,
  while deleting an ingredient referenced by a recipe returns a conflict.
- Ingredient creation checks duplicates before saving. Do not catch broad
  database integrity exceptions in the service just to map rare concurrent
  duplicate creates; solve that deliberately later if the product needs it.
- The `recipe` module owns `RecipeEntity -> RecipeSectionEntity ->
  RecipeIngredientEntity`.
- `RecipeIngredientEntity` stores an `ingredientId` instead of referencing
  `IngredientEntity` directly.
- The database enforces recipe ingredient references through
  `fk_recipe_ingredient_ingredient`. Ingredient deletion maps only violations of
  this constraint to the domain conflict; unrelated database integrity failures
  remain visible.
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
- Recipe updates replace the full aggregate through `PUT /recipes/{recipeId}`.
  Existing sections and recipe ingredients retain identity when their IDs are
  supplied. Missing IDs create children, omitted children are removed, and list
  order defines positions. Existing recipe ingredients cannot move between
  sections; clients remove and recreate them without an ID. Updates use
  last-write-wins without optimistic locking.
- Recipe updates containing new sections or recipe ingredients without IDs are
  idempotent only at the logical aggregate-content level. Repeating such a
  request can replace those children with newly generated backend IDs. This is
  an accepted v1 tradeoff. After an ambiguous update failure, clients should
  fetch the recipe, compare its logical content with the intended state, and use
  the server-returned child IDs as authoritative instead of automatically
  retrying the update.
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
- SoloCooking intentionally follows code-first OpenAPI. Hand-written Java API
  interfaces define the HTTP contract, springdoc generates the specification for
  Swagger UI, and Hey API generates the frontend client from that specification.
  Contract-first backend generation is not planned.
- Runtime error bodies and generated OpenAPI error schemas must stay synchronized.
  Validation failures use `BadRequestProblemDetail`, whose optional `errors` map
  exposes field-level messages to generated frontend clients.
- Ingredient conflicts expose stable problem types:
  `urn:solocooking:error:ingredient-already-exists` and
  `urn:solocooking:error:ingredient-in-use`. Frontend code maps these types to its
  own localized messages and never parses `detail`.
- Use the latest stable PostgreSQL minor version for local development and
  Testcontainers. Pin the exact Docker image tag instead of using `latest`, and
  update `docker-compose.yml` and integration test containers together.
- For PostgreSQL 18 and newer, mount the persistent volume at
  `/var/lib/postgresql`; the image stores database files in a version-specific
  subdirectory such as `/var/lib/postgresql/18/docker`.
- The public API currently allows CORS requests from any origin without
  credentials. Revisit allowed origins, credentials, and CSRF together when
  introducing cookie or session authentication.
- Flyway SQL migrations are the single source of truth for the PostgreSQL schema.
  Hibernate validates the mappings against that schema and must not create or
  update it. Use sequential versioned migrations such as `V1`, `V2`, and `V3`,
  and fix an applied schema change by rolling forward with a new migration.
- Before the first persistent deployment, development migrations may be rebuilt
  or squashed into a new `V1__create_initial_schema.sql` only while every database
  that used them is disposable and can be recreated. After the first persistent
  deployment, never edit, delete, reorder, or squash applied versioned
  migrations. If fresh installations later need a shorter entry point, add a
  Flyway baseline migration while retaining the old versioned migrations for
  existing databases.
- Store database check constraints and indexes in Flyway migrations rather than
  schema-generating Hibernate annotations. Local databases created before Flyway
  was introduced must be recreated once instead of being adopted through
  `baseline-on-migrate`.

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
