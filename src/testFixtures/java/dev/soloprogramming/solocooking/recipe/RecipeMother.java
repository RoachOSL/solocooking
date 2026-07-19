/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.List;
import java.util.UUID;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeIngredientDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSectionDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeIngredientRequest;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeSectionRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
class RecipeMother {

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
        return recipeDtoBuilder(List.of(persistedRecipeSectionDto()))
                .id(RecipeTestConstants.RECIPE_ID)
                .createdAt(RecipeTestConstants.RECIPE_CREATED_AT)
                .updatedAt(RecipeTestConstants.RECIPE_UPDATED_AT);
    }

    static RecipeDTO.RecipeDTOBuilder recipeDtoBuilder(UUID ingredientId) {
        return recipeDtoBuilder(List.of(newRecipeSectionDtoBuilder(ingredientId).build()));
    }

    static RecipeDTO.RecipeDTOBuilder recipeDtoWithNullNoteBuilder() {
        var ingredient = newRecipeIngredientDtoBuilder(RecipeTestConstants.INGREDIENT_ID)
                .id(RecipeTestConstants.RECIPE_INGREDIENT_ID)
                .note(null)
                .build();
        var section = newRecipeSectionDtoBuilder(RecipeTestConstants.INGREDIENT_ID)
                .id(RecipeTestConstants.RECIPE_SECTION_ID)
                .ingredients(List.of(ingredient))
                .build();
        return recipeDtoBuilder(List.of(section))
                .id(RecipeTestConstants.RECIPE_ID)
                .createdAt(RecipeTestConstants.RECIPE_CREATED_AT)
                .updatedAt(RecipeTestConstants.RECIPE_UPDATED_AT);
    }

    private static RecipeDTO.RecipeDTOBuilder recipeDtoBuilder(List<RecipeSectionDTO> sections) {
        return RecipeDTO.builder()
                .name(RecipeTestConstants.RECIPE_NAME)
                .imageUrl(RecipeTestConstants.RECIPE_IMAGE_URL)
                .description(RecipeTestConstants.RECIPE_DESCRIPTION)
                .sections(sections);
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

    private static RecipeSectionDTO persistedRecipeSectionDto() {
        return newRecipeSectionDtoBuilder(RecipeTestConstants.INGREDIENT_ID)
                .id(RecipeTestConstants.RECIPE_SECTION_ID)
                .ingredients(List.of(persistedRecipeIngredientDto()))
                .build();
    }

    private static RecipeSectionDTO.RecipeSectionDTOBuilder newRecipeSectionDtoBuilder(UUID ingredientId) {
        return RecipeSectionDTO.builder()
                .name(RecipeTestConstants.RECIPE_SECTION_NAME)
                .position(RecipeTestConstants.RECIPE_SECTION_POSITION)
                .ingredients(List.of(newRecipeIngredientDtoBuilder(ingredientId).build()));
    }

    private static RecipeIngredientDTO persistedRecipeIngredientDto() {
        return newRecipeIngredientDtoBuilder(RecipeTestConstants.INGREDIENT_ID)
                .id(RecipeTestConstants.RECIPE_INGREDIENT_ID)
                .build();
    }

    private static RecipeIngredientDTO.RecipeIngredientDTOBuilder newRecipeIngredientDtoBuilder(UUID ingredientId) {
        return RecipeIngredientDTO.builder()
                .ingredientId(ingredientId)
                .amount(RecipeTestConstants.RECIPE_INGREDIENT_AMOUNT)
                .unit(RecipeTestConstants.RECIPE_INGREDIENT_UNIT)
                .note(RecipeTestConstants.RECIPE_INGREDIENT_NOTE)
                .position(RecipeTestConstants.RECIPE_INGREDIENT_POSITION);
    }

    static CreateRecipeRequest.CreateRecipeRequestBuilder createRecipeRequestBuilder() {
        return createRecipeRequestBuilder(RecipeTestConstants.INGREDIENT_ID);
    }

    static CreateRecipeRequest.CreateRecipeRequestBuilder createRecipeRequestBuilder(UUID ingredientId) {
        return CreateRecipeRequest.builder()
                .name(RecipeTestConstants.RECIPE_NAME)
                .imageUrl(RecipeTestConstants.RECIPE_IMAGE_URL)
                .description(RecipeTestConstants.RECIPE_DESCRIPTION)
                .sections(List.of(createRecipeSectionRequestBuilder(ingredientId).build()));
    }

    static CreateRecipeSectionRequest.CreateRecipeSectionRequestBuilder createRecipeSectionRequestBuilder() {
        return createRecipeSectionRequestBuilder(RecipeTestConstants.INGREDIENT_ID);
    }

    static CreateRecipeSectionRequest.CreateRecipeSectionRequestBuilder createRecipeSectionRequestBuilder(UUID ingredientId) {
        return CreateRecipeSectionRequest.builder()
                .name(RecipeTestConstants.RECIPE_SECTION_NAME)
                .ingredients(List.of(createRecipeIngredientRequestBuilder(ingredientId).build()));
    }

    static CreateRecipeIngredientRequest.CreateRecipeIngredientRequestBuilder createRecipeIngredientRequestBuilder() {
        return createRecipeIngredientRequestBuilder(RecipeTestConstants.INGREDIENT_ID);
    }

    static CreateRecipeIngredientRequest.CreateRecipeIngredientRequestBuilder createRecipeIngredientRequestBuilder(UUID ingredientId) {
        return CreateRecipeIngredientRequest.builder()
                .ingredientId(ingredientId)
                .amount(RecipeTestConstants.RECIPE_INGREDIENT_AMOUNT)
                .unit(RecipeTestConstants.RECIPE_INGREDIENT_UNIT)
                .note(RecipeTestConstants.RECIPE_INGREDIENT_NOTE);
    }
}
