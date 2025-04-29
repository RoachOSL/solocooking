package dev.soloprogramming.solocooking.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface RecipeRepository extends JpaRepository<RecipeEntity, UUID> {
}
