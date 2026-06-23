package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.common.TestConstants;
import dev.soloprogramming.solocooking.common.InMemoryRepository;

import java.util.UUID;

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
