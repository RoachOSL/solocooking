package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;


@Validated
public interface RecipeFacade {

    RecipeDTO createRecipe(@NotNull @Valid CreateRecipeRequest createRecipeRequest);

    Page<RecipeDTO> getRecipes(Pageable pageable);

    RecipeDTO findById(UUID recipeId);

    void deleteById(UUID recipeID);
}
