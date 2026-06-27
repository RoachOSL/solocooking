# Project guidelines

SoloCooking-specific guidance. Keep reusable engineering rules in
`AI_GUIDELINES.md`; keep domain and product decisions for this repository here.

## Domain model

- The `ingredient` module owns the ingredient catalog.
- Ingredient names are stored normalized in the `name` field. Do not add a
  separate `normalizedName` field unless the product later needs to preserve a
  display name distinct from the searchable unique name.
- Creating a duplicate ingredient returns a conflict instead of behaving as an
  idempotent create operation. If the product needs idempotent ingredient
  creation later, introduce that as an explicit endpoint or command.
- The `recipe` module owns `RecipeEntity -> RecipeSectionEntity ->
  RecipeIngredientEntity`.
- `RecipeIngredientEntity` stores an `ingredientId` instead of referencing
  `IngredientEntity` directly.
- Recipe creation validates referenced ingredient IDs through
  `IngredientFacade`.
