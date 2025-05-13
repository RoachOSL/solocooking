package dev.soloprogramming.solocooking.recipe.dto;

import java.time.Instant;
import java.util.UUID;

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
