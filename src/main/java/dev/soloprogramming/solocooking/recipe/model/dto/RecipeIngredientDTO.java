/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
public record RecipeIngredientDTO(
        UUID id,
        UUID ingredientId,
        BigDecimal amount,
        String unit,
        String note,
        Integer sortOrder
) {
}
