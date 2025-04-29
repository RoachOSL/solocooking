package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.dto.RecipeDTO;

import java.util.List;

public interface RecipeFacade {

    RecipeDTO createRecipe(RecipeDTO dto);

    List<RecipeDTO> getAllRecipes();
}
