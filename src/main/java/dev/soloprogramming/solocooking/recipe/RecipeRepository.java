/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface RecipeRepository extends JpaRepository<RecipeEntity, UUID> {
}
