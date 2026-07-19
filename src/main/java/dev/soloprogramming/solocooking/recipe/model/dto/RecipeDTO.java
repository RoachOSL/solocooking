/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(requiredProperties = {"id", "name", "imageUrl", "description", "sections", "updatedAt", "createdAt"})
@Builder
public record RecipeDTO(
        UUID id,
        String name,
        String imageUrl,
        String description,
        List<RecipeSectionDTO> sections,
        Instant updatedAt,
        Instant createdAt
) {
}
