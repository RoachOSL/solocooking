/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateIngredientRequest(

        @Schema(nullable = true)
        @Pattern(regexp = "(?s).*\\S.*")
        @Size(max = 255)
        String name
) {
}
