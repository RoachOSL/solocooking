/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IngredientTestFixtures {

    public static IngredientDTO givenExistingIngredient(IngredientFacade ingredientFacade) {
        return ingredientFacade.createIngredient(IngredientMother.createIngredientRequestBuilder().build());
    }
}
