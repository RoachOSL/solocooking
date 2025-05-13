package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.dto.RecipeRequest;

import java.util.List;

public interface RecipeFacade {

    RecipeDTO createRecipe(RecipeRequest recipeRequest);

    List<RecipeDTO> getAllRecipes();
}
