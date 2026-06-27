/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.List;

import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeIngredientRequest;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeSectionRequest;
import org.springframework.stereotype.Component;

@Component
class RecipeFactory {

    RecipeEntity from(CreateRecipeRequest createRecipeRequest) {
        var recipe = new RecipeEntity();
        recipe.setName(createRecipeRequest.name());
        recipe.setImageUrl(createRecipeRequest.imageUrl());
        recipe.setDescription(createRecipeRequest.description());
        recipe.replaceSections(toSectionEntities(createRecipeRequest));

        return recipe;
    }

    private List<RecipeSectionEntity> toSectionEntities(CreateRecipeRequest createRecipeRequest) {
        return createRecipeRequest.sections().stream()
                .map(this::toSectionEntity)
                .toList();
    }

    private RecipeSectionEntity toSectionEntity(CreateRecipeSectionRequest createRecipeSectionRequest) {
        var section = new RecipeSectionEntity();
        section.setName(createRecipeSectionRequest.name());
        section.replaceIngredients(toIngredientEntities(createRecipeSectionRequest));

        return section;
    }

    private List<RecipeIngredientEntity> toIngredientEntities(CreateRecipeSectionRequest createRecipeSectionRequest) {
        return createRecipeSectionRequest.ingredients().stream()
                .map(this::toIngredientEntity)
                .toList();
    }

    private RecipeIngredientEntity toIngredientEntity(CreateRecipeIngredientRequest createRecipeIngredientRequest) {
        var ingredient = new RecipeIngredientEntity();
        ingredient.setIngredientId(createRecipeIngredientRequest.ingredientId());
        ingredient.setAmount(createRecipeIngredientRequest.amount());
        ingredient.setUnit(createRecipeIngredientRequest.unit());
        ingredient.setNote(createRecipeIngredientRequest.note());

        return ingredient;
    }
}
