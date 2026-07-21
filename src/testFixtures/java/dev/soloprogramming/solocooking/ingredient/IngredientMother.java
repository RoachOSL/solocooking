/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import dev.soloprogramming.solocooking.ingredient.model.request.UpdateIngredientRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
class IngredientMother {

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

    static IngredientEntity ingredientEntityWithName(String name) {
        var ingredient = new IngredientEntity();
        ingredient.setName(name);
        return ingredient;
    }

    static CreateIngredientRequest.CreateIngredientRequestBuilder createIngredientRequestBuilder() {
        return CreateIngredientRequest.builder()
                .name(IngredientTestConstants.INGREDIENT_NAME);
    }

    static UpdateIngredientRequest.UpdateIngredientRequestBuilder updateIngredientRequestBuilder() {
        return UpdateIngredientRequest.builder()
                .name(IngredientTestConstants.INGREDIENT_NAME);
    }
}
