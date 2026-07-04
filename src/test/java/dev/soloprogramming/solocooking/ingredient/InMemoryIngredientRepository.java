/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.InMemoryRepository;

final class InMemoryIngredientRepository extends InMemoryRepository<IngredientEntity, UUID>
        implements IngredientRepository {

    @Override
    public boolean existsByName(String name) {
        return findAll().stream()
                .anyMatch(ingredient -> ingredient.getName().equals(name));
    }

    @Override
    public List<IngredientEntity> findAllByIdIn(Set<UUID> ingredientIds) {
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
        return UUID.randomUUID();
    }
}
