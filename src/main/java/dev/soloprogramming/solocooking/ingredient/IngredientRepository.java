/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface IngredientRepository extends JpaRepository<IngredientEntity, UUID> {

    boolean existsByName(String name);

    List<IngredientEntity> findAllByIdIn(Set<UUID> ingredientIds);
}
