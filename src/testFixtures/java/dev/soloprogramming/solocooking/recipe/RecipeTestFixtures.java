/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.UUID;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RecipeTestFixtures {

    public static RecipeDTO givenExistingRecipe(RecipeFacade recipeFacade, UUID ingredientId) {
        return recipeFacade.createRecipe(RecipeMother.createRecipeRequestBuilder(ingredientId).build());
    }
}
