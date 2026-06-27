/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record RecipeSummaryDTO(
        UUID id,
        String name,
        String imageUrl,
        String description,
        Instant updatedAt,
        Instant createdAt
) {
}
