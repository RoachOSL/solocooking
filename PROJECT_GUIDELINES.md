# Project guidelines

SoloCooking-specific guidance. Keep reusable engineering rules in
`AI_GUIDELINES.md`; keep domain and product decisions for this repository here.

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
- Recipe creation validates referenced ingredient IDs through
  `IngredientFacade`.
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

- Use the latest stable PostgreSQL minor version for local development and
  Testcontainers. Pin the exact Docker image tag instead of using `latest`, and
  update `docker-compose.yml` and integration test containers together.
