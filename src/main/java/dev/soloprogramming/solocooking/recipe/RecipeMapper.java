package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.dto.RecipeDTO;
import org.mapstruct.Mapper;

@Mapper
interface RecipeMapper {

    RecipeDTO toDto(RecipeEntity recipeEntity);

    RecipeEntity fromDto(RecipeDTO recipeDTO);
}
