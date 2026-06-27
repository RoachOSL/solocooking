/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.InMemoryRepository;
import dev.soloprogramming.solocooking.common.TestIdGenerator;

final class InMemoryRecipeRepository extends InMemoryRepository<RecipeEntity, UUID>
        implements RecipeRepository {

    private final TestIdGenerator recipeIdGenerator = new TestIdGenerator();
    private final TestIdGenerator sectionIdGenerator = new TestIdGenerator();
    private final TestIdGenerator ingredientIdGenerator = new TestIdGenerator();

    @Override
    public <S extends RecipeEntity> S save(S recipeEntity) {
        fillChildIds(recipeEntity);
        return super.save(recipeEntity);
    }

    @Override
    public Optional<RecipeEntity> findByIdWithoutDetails(UUID recipeId) {
        return findById(recipeId);
    }

    @Override
    protected UUID getId(RecipeEntity recipeEntity) {
        return recipeEntity.getId();
    }

    @Override
    protected void setId(RecipeEntity recipeEntity, UUID recipeId) {
        recipeEntity.setId(recipeId);
    }

    @Override
    protected UUID generateId() {
        return recipeIdGenerator.nextId(RecipeTestConstants.RECIPE_ID, "recipe", usedRecipeIds());
    }

    private void fillChildIds(RecipeEntity recipeEntity) {
        recipeEntity.getSections().forEach(section -> {
            if (section.getId() == null) {
                section.setId(sectionIdGenerator.nextId(
                        RecipeTestConstants.RECIPE_SECTION_ID,
                        "recipe-section",
                        usedSectionIds(recipeEntity)
                ));
            }
            section.getIngredients().forEach(ingredient -> {
                if (ingredient.getId() == null) {
                    ingredient.setId(ingredientIdGenerator.nextId(
                            RecipeTestConstants.RECIPE_INGREDIENT_ID,
                            "recipe-ingredient",
                            usedIngredientIds(recipeEntity)
                    ));
                }
            });
        });
    }

    private Set<UUID> usedRecipeIds() {
        var usedIds = new HashSet<UUID>();
        findAll().stream()
                .map(RecipeEntity::getId)
                .filter(Objects::nonNull)
                .forEach(usedIds::add);
        return usedIds;
    }

    private Set<UUID> usedSectionIds(RecipeEntity recipeEntity) {
        var usedIds = findAll().stream()
                .flatMap(recipe -> recipe.getSections().stream())
                .map(RecipeSectionEntity::getId)
                .filter(Objects::nonNull)
                .collect(HashSet<UUID>::new, HashSet::add, HashSet::addAll);

        recipeEntity.getSections().stream()
                .map(RecipeSectionEntity::getId)
                .filter(Objects::nonNull)
                .forEach(usedIds::add);

        return usedIds;
    }

    private Set<UUID> usedIngredientIds(RecipeEntity recipeEntity) {
        var usedIds = findAll().stream()
                .flatMap(recipe -> recipe.getSections().stream())
                .flatMap(section -> section.getIngredients().stream())
                .map(RecipeIngredientEntity::getId)
                .filter(Objects::nonNull)
                .collect(HashSet<UUID>::new, HashSet::add, HashSet::addAll);

        recipeEntity.getSections().stream()
                .flatMap(section -> section.getIngredients().stream())
                .map(RecipeIngredientEntity::getId)
                .filter(Objects::nonNull)
                .forEach(usedIds::add);

        return usedIds;
    }
}
