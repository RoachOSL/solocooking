/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.UUID;

import dev.soloprogramming.solocooking.common.InMemoryRepository;
final class InMemoryRecipeRepository extends InMemoryRepository<RecipeEntity, UUID>
        implements RecipeRepository {

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
        return RecipeTestConstants.RECIPE_ID;
    }

    private void fillChildIds(RecipeEntity recipeEntity) {
        recipeEntity.getSections().forEach(section -> {
            if (section.getId() == null) {
                section.setId(RecipeTestConstants.RECIPE_SECTION_ID);
            }
            section.getIngredients().forEach(ingredient -> {
                if (ingredient.getId() == null) {
                    ingredient.setId(RecipeTestConstants.RECIPE_INGREDIENT_ID);
                }
            });
        });
    }
}
