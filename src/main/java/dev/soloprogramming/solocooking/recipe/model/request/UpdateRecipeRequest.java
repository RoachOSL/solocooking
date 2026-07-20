/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.request;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateRecipeRequest(

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
        List<@NotNull @Valid UpdateRecipeSectionRequest> sections
) {

    public Set<UUID> ingredientIds() {
        return sections.stream()
                .flatMap(section -> section.ingredients().stream())
                .map(UpdateRecipeIngredientRequest::ingredientId)
                .collect(Collectors.toSet());
    }
}
