/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.ArrayList;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeIngredientDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSectionDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeIngredientRequest;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeSectionRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
interface RecipeMapper {

    RecipeDTO toDto(RecipeEntity recipeEntity);

    RecipeSummaryDTO toSummaryDto(RecipeEntity recipeEntity);

    RecipeSectionDTO toDto(RecipeSectionEntity recipeSectionEntity);

    RecipeIngredientDTO toDto(RecipeIngredientEntity recipeIngredientEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RecipeEntity fromRequest(CreateRecipeRequest createRecipeRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RecipeSectionEntity fromRequest(CreateRecipeSectionRequest createRecipeSectionRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RecipeIngredientEntity fromRequest(CreateRecipeIngredientRequest createRecipeIngredientRequest);

    @AfterMapping
    default void linkSections(@MappingTarget RecipeEntity recipeEntity) {
        if (recipeEntity.getSections() == null) {
            recipeEntity.setSections(new ArrayList<>());
        }

        recipeEntity.getSections().forEach(section -> {
            section.setRecipe(recipeEntity);
            if (section.getIngredients() == null) {
                section.setIngredients(new ArrayList<>());
            }
            section.getIngredients().forEach(ingredient -> ingredient.setSection(section));
        });
    }
}
