/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.UUID;

import dev.soloprogramming.solocooking.common.InMemoryRepository;
import dev.soloprogramming.solocooking.common.TestConstants;

final class InMemoryRecipeRepository extends InMemoryRepository<RecipeEntity, UUID>
        implements RecipeRepository {

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
        return TestConstants.RECIPE_ID;
    }
}
