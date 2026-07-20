/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.model.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateRecipeIngredientRequest(

        UUID id,

        @NotNull
        UUID ingredientId,

        @NotNull
        @Positive
        @Digits(integer = 9, fraction = 3)
        BigDecimal amount,

        @NotBlank
        @Size(max = 64)
        String unit,

        @Size(max = 500)
        String note
) {
}
