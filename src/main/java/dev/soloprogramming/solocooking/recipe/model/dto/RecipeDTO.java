package dev.soloprogramming.solocooking.recipe.model.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

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
