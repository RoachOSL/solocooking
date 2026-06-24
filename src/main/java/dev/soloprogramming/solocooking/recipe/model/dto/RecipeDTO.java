/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record RecipeDTO(
        UUID id,
        String name,
        String imageUrl,
        String description,
        String ingredients,
        Instant updatedAt,
        Instant createdAt
) {
}
