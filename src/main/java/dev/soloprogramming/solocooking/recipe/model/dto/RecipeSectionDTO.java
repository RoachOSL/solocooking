/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(requiredProperties = {"id", "name", "position", "ingredients"})
@Builder
public record RecipeSectionDTO(
        UUID id,
        String name,
        Integer position,
        List<RecipeIngredientDTO> ingredients
) {
}
