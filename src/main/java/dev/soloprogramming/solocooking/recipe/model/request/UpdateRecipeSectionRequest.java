/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateRecipeSectionRequest(

        UUID id,

        @NotBlank
        @Size(max = 255)
        String name,

        @NotEmpty
        List<@NotNull @Valid UpdateRecipeIngredientRequest> ingredients
) {
}
