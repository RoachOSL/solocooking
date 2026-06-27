/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.request;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateRecipeRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Pattern(
                regexp = "^(https?://).*$",
                message = "Must be a valid URL starting with http:// or https://"
        )
        @Size(max = 2048)
        String imageUrl,

        @NotBlank
        @Size(max = 5000)
        String description,

        @NotEmpty
        List<@NotNull @Valid CreateRecipeSectionRequest> sections
) {

    public Set<UUID> ingredientIds() {
        if (sections == null) {
            return Set.of();
        }

        return sections.stream()
                .filter(section -> section.ingredients() != null)
                .flatMap(section -> section.ingredients().stream())
                .map(CreateRecipeIngredientRequest::ingredientId)
                .collect(java.util.stream.Collectors.toSet());
    }
}
