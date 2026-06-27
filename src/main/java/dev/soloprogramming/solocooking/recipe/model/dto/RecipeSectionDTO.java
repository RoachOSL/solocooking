/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record RecipeSectionDTO(
        UUID id,
        String name,
        Integer position,
        List<RecipeIngredientDTO> ingredients
) {
}
