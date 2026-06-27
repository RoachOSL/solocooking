/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface RecipeRepository extends JpaRepository<RecipeEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"sections", "sections.ingredients"})
    Optional<RecipeEntity> findById(UUID recipeId);

    @Query("select recipe from RecipeEntity recipe where recipe.id = :recipeId")
    Optional<RecipeEntity> findByIdWithoutDetails(@Param("recipeId") UUID recipeId);
}
