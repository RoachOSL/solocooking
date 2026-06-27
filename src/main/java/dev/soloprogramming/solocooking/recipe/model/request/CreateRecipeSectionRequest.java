/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateRecipeSectionRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        List<@Valid CreateRecipeIngredientRequest> ingredients
) {
}
