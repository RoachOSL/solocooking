/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient.model.request;

import dev.soloprogramming.solocooking.ingredient.ValidIngredientName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CreateIngredientRequest(

        @Schema(minLength = 1, maxLength = 255)
        @ValidIngredientName
        String name
) {
}
