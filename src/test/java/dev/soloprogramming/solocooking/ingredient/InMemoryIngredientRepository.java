/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.InMemoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

final class InMemoryIngredientRepository extends InMemoryRepository<IngredientEntity, UUID>
        implements IngredientRepository {

    @Override
    public boolean existsByName(String name) {
        return findAll().stream()
                .anyMatch(ingredient -> ingredient.getName().equals(name));
    }

    @Override
    public Page<IngredientEntity> findAllByNameContaining(String name, Pageable pageable) {
        var matchingIngredients = findAll().stream()
                .filter(ingredient -> ingredient.getName().contains(name))
                .toList();
        return toPage(matchingIngredients, pageable);
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
