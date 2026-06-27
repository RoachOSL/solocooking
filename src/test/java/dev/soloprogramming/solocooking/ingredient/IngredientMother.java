/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class IngredientMother {

    static IngredientEntity ingredientEntity() {
        var ingredient = new IngredientEntity();
        ingredient.setId(IngredientTestConstants.INGREDIENT_ID);
        ingredient.setName(IngredientTestConstants.INGREDIENT_STORED_NAME);
        return ingredient;
    }

    static IngredientDTO.IngredientDTOBuilder ingredientDtoBuilder() {
        return IngredientDTO.builder()
                .id(IngredientTestConstants.INGREDIENT_ID)
                .name(IngredientTestConstants.INGREDIENT_STORED_NAME);
    }

    static CreateIngredientRequest.CreateIngredientRequestBuilder createIngredientRequestBuilder() {
        return CreateIngredientRequest.builder()
                .name(IngredientTestConstants.INGREDIENT_NAME);
    }
}
