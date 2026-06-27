/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface IngredientRepository extends JpaRepository<IngredientEntity, UUID> {

    boolean existsByNormalizedName(String normalizedName);

    List<IngredientEntity> findAllByIdIn(Collection<UUID> ingredientIds);
}
