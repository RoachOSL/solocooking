/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateIngredientRequest(

        @Schema(minLength = 1, maxLength = 255)
        @NotBlank
        @Size(min = 1, max = 255)
        String name
) {
}
