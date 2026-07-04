/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.Optional;
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
        return UUID.randomUUID();
    }

    private void fillChildIds(RecipeEntity recipeEntity) {
        recipeEntity.getSections().forEach(section -> {
            if (section.getId() == null) {
                section.setId(UUID.randomUUID());
            }
            section.getIngredients().forEach(ingredient -> {
                if (ingredient.getId() == null) {
                    ingredient.setId(UUID.randomUUID());
                }
            });
        });
    }
}
