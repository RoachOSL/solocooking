/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(requiredProperties = {"id", "name", "imageUrl", "description", "updatedAt", "createdAt"})
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
