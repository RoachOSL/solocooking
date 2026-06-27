/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.InMemoryRepository;

final class InMemoryIngredientRepository extends InMemoryRepository<IngredientEntity, UUID>
        implements IngredientRepository {

    @Override
    public boolean existsByNormalizedName(String normalizedName) {
        return findAll().stream()
                .anyMatch(ingredient -> ingredient.getNormalizedName().equals(normalizedName));
    }

    @Override
    public List<IngredientEntity> findAllByIdIn(Collection<UUID> ingredientIds) {
        return findAllById(ingredientIds);
    }

    @Override
    protected UUID getId(IngredientEntity ingredientEntity) {
        return ingredientEntity.getId();
    }

    @Override
    protected void setId(IngredientEntity ingredientEntity, UUID ingredientId) {
        ingredientEntity.setId(ingredientId);
    }

    @Override
    protected UUID generateId() {
        return IngredientTestConstants.INGREDIENT_ID;
    }
}
