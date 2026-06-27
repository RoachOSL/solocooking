/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.InMemoryRepository;
import dev.soloprogramming.solocooking.common.TestIdGenerator;

final class InMemoryIngredientRepository extends InMemoryRepository<IngredientEntity, UUID>
        implements IngredientRepository {

    private final TestIdGenerator idGenerator = new TestIdGenerator();

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
        return idGenerator.nextId(IngredientTestConstants.INGREDIENT_ID, "ingredient", usedIds());
    }

    private Set<UUID> usedIds() {
        var usedIds = new HashSet<UUID>();
        findAll().stream()
                .map(IngredientEntity::getId)
                .filter(Objects::nonNull)
                .forEach(usedIds::add);
        return usedIds;
    }
}
