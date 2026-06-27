/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient.model.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record IngredientDTO(
        UUID id,
        String name,
        String normalizedName
) {
}
