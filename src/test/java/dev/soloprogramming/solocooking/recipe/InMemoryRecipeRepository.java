/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.InMemoryRepository;
final class InMemoryRecipeRepository extends InMemoryRepository<RecipeEntity, UUID>
        implements RecipeRepository {

    private int recipeIdSequence;
    private int sectionIdSequence;
    private int ingredientIdSequence;

    @Override
    public <S extends RecipeEntity> S save(S recipeEntity) {
        fillChildIds(recipeEntity);
        return super.save(recipeEntity);
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
        return nextId(RecipeTestConstants.RECIPE_ID, "recipe", recipeIdSequence++);
    }

    private void fillChildIds(RecipeEntity recipeEntity) {
        recipeEntity.getSections().forEach(section -> {
            if (section.getId() == null) {
                section.setId(nextId(RecipeTestConstants.RECIPE_SECTION_ID, "recipe-section", sectionIdSequence++));
            }
            section.getIngredients().forEach(ingredient -> {
                if (ingredient.getId() == null) {
                    ingredient.setId(nextId(
                            RecipeTestConstants.RECIPE_INGREDIENT_ID,
                            "recipe-ingredient",
                            ingredientIdSequence++
                    ));
                }
            });
        });
    }

    private UUID nextId(UUID firstId, String idPrefix, int index) {
        if (index == 0) {
            return firstId;
        }

        return UUID.nameUUIDFromBytes("%s-%d".formatted(idPrefix, index).getBytes(StandardCharsets.UTF_8));
    }
}
