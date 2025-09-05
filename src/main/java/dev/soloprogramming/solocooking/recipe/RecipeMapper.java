package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import org.mapstruct.Mapper;

@Mapper
interface RecipeMapper {

    // TODO -> For future make mapping required to not make mapper mistakes

    RecipeDTO toDto(RecipeEntity recipeEntity);

    RecipeEntity fromDto(RecipeDTO recipeDTO);

    RecipeEntity fromRequest(CreateRecipeRequest createRecipeRequest);
}
