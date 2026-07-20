/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface IngredientRepository extends JpaRepository<IngredientEntity, UUID> {

    boolean existsByName(String name);

    Page<IngredientEntity> findAllByNameContaining(String name, Pageable pageable);

    List<IngredientEntity> findAllByIdIn(Set<UUID> ingredientIds);
}
