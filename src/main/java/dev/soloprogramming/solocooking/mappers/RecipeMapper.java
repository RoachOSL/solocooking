package dev.soloprogramming.solocooking.mappers;

import dev.soloprogramming.solocooking.dto.RecipeDTO;
import dev.soloprogramming.solocooking.entities.Recipe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    RecipeDTO toDto(Recipe recipe);

    Recipe fromDto(RecipeDTO recipeDTO);
}
