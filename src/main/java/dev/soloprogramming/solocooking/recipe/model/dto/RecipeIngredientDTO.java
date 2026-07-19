/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(requiredProperties = {"id", "ingredientId", "amount", "unit", "note", "position"})
@Builder
public record RecipeIngredientDTO(
        UUID id,
        UUID ingredientId,
        BigDecimal amount,
        String unit,
        @Schema(nullable = true)
        String note,
        Integer position
) {
}
