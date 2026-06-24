/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.common.TestConstants;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RecipeMother {

    static RecipeEntity recipeEntity() {
        var recipe = new RecipeEntity();
        recipe.setId(TestConstants.RECIPE_ID);
        recipe.setName(TestConstants.RECIPE_NAME);
        recipe.setImageUrl(TestConstants.RECIPE_IMAGE_URL);
        recipe.setDescription(TestConstants.RECIPE_DESCRIPTION);
        recipe.setIngredients(TestConstants.RECIPE_INGREDIENTS);
        recipe.setCreatedAt(TestConstants.RECIPE_CREATED_AT);
        recipe.setUpdatedAt(TestConstants.RECIPE_UPDATED_AT);
        return recipe;
    }

    static RecipeDTO.RecipeDTOBuilder recipeDtoBuilder() {
        return RecipeDTO.builder()
                .id(TestConstants.RECIPE_ID)
                .name(TestConstants.RECIPE_NAME)
                .imageUrl(TestConstants.RECIPE_IMAGE_URL)
                .description(TestConstants.RECIPE_DESCRIPTION)
                .ingredients(TestConstants.RECIPE_INGREDIENTS)
                .createdAt(TestConstants.RECIPE_CREATED_AT)
                .updatedAt(TestConstants.RECIPE_UPDATED_AT);
    }

    static CreateRecipeRequest.CreateRecipeRequestBuilder createRecipeRequestBuilder() {
        return CreateRecipeRequest.builder()
                .name(TestConstants.RECIPE_NAME)
                .imageUrl(TestConstants.RECIPE_IMAGE_URL)
                .description(TestConstants.RECIPE_DESCRIPTION)
                .ingredients(TestConstants.RECIPE_INGREDIENTS);
    }
}
