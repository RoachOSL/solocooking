/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.List;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeIngredientDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSectionDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeIngredientRequest;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeSectionRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RecipeMother {

    static RecipeEntity recipeEntity() {
        var recipe = new RecipeEntity();
        recipe.setId(RecipeTestConstants.RECIPE_ID);
        recipe.setName(RecipeTestConstants.RECIPE_NAME);
        recipe.setImageUrl(RecipeTestConstants.RECIPE_IMAGE_URL);
        recipe.setDescription(RecipeTestConstants.RECIPE_DESCRIPTION);
        recipe.replaceSections(List.of(recipeSectionEntity()));
        return recipe;
    }

    private static RecipeSectionEntity recipeSectionEntity() {
        var section = new RecipeSectionEntity();
        section.setId(RecipeTestConstants.RECIPE_SECTION_ID);
        section.setName(RecipeTestConstants.RECIPE_SECTION_NAME);
        section.replaceIngredients(List.of(recipeIngredientEntity()));
        return section;
    }

    private static RecipeIngredientEntity recipeIngredientEntity() {
        var ingredient = new RecipeIngredientEntity();
        ingredient.setId(RecipeTestConstants.RECIPE_INGREDIENT_ID);
        ingredient.setIngredientId(RecipeTestConstants.INGREDIENT_ID);
        ingredient.setAmount(RecipeTestConstants.RECIPE_INGREDIENT_AMOUNT);
        ingredient.setUnit(RecipeTestConstants.RECIPE_INGREDIENT_UNIT);
        ingredient.setNote(RecipeTestConstants.RECIPE_INGREDIENT_NOTE);
        return ingredient;
    }

    static RecipeDTO.RecipeDTOBuilder recipeDtoBuilder() {
        return RecipeDTO.builder()
                .id(RecipeTestConstants.RECIPE_ID)
                .name(RecipeTestConstants.RECIPE_NAME)
                .imageUrl(RecipeTestConstants.RECIPE_IMAGE_URL)
                .description(RecipeTestConstants.RECIPE_DESCRIPTION)
                .sections(List.of(recipeSectionDto()))
                .createdAt(RecipeTestConstants.RECIPE_CREATED_AT)
                .updatedAt(RecipeTestConstants.RECIPE_UPDATED_AT);
    }

    static RecipeSummaryDTO.RecipeSummaryDTOBuilder recipeSummaryDtoBuilder() {
        return RecipeSummaryDTO.builder()
                .id(RecipeTestConstants.RECIPE_ID)
                .name(RecipeTestConstants.RECIPE_NAME)
                .imageUrl(RecipeTestConstants.RECIPE_IMAGE_URL)
                .description(RecipeTestConstants.RECIPE_DESCRIPTION)
                .createdAt(RecipeTestConstants.RECIPE_CREATED_AT)
                .updatedAt(RecipeTestConstants.RECIPE_UPDATED_AT);
    }

    private static RecipeSectionDTO recipeSectionDto() {
        return RecipeSectionDTO.builder()
                .id(RecipeTestConstants.RECIPE_SECTION_ID)
                .name(RecipeTestConstants.RECIPE_SECTION_NAME)
                .position(RecipeTestConstants.RECIPE_SECTION_POSITION)
                .ingredients(List.of(recipeIngredientDto()))
                .build();
    }

    private static RecipeIngredientDTO recipeIngredientDto() {
        return RecipeIngredientDTO.builder()
                .id(RecipeTestConstants.RECIPE_INGREDIENT_ID)
                .ingredientId(RecipeTestConstants.INGREDIENT_ID)
                .amount(RecipeTestConstants.RECIPE_INGREDIENT_AMOUNT)
                .unit(RecipeTestConstants.RECIPE_INGREDIENT_UNIT)
                .note(RecipeTestConstants.RECIPE_INGREDIENT_NOTE)
                .position(RecipeTestConstants.RECIPE_INGREDIENT_POSITION)
                .build();
    }

    static CreateRecipeRequest.CreateRecipeRequestBuilder createRecipeRequestBuilder() {
        return CreateRecipeRequest.builder()
                .name(RecipeTestConstants.RECIPE_NAME)
                .imageUrl(RecipeTestConstants.RECIPE_IMAGE_URL)
                .description(RecipeTestConstants.RECIPE_DESCRIPTION)
                .sections(List.of(createRecipeSectionRequestBuilder().build()));
    }

    static CreateRecipeSectionRequest.CreateRecipeSectionRequestBuilder createRecipeSectionRequestBuilder() {
        return CreateRecipeSectionRequest.builder()
                .name(RecipeTestConstants.RECIPE_SECTION_NAME)
                .ingredients(List.of(createRecipeIngredientRequestBuilder().build()));
    }

    static CreateRecipeIngredientRequest.CreateRecipeIngredientRequestBuilder createRecipeIngredientRequestBuilder() {
        return CreateRecipeIngredientRequest.builder()
                .ingredientId(RecipeTestConstants.INGREDIENT_ID)
                .amount(RecipeTestConstants.RECIPE_INGREDIENT_AMOUNT)
                .unit(RecipeTestConstants.RECIPE_INGREDIENT_UNIT)
                .note(RecipeTestConstants.RECIPE_INGREDIENT_NOTE);
    }
}
