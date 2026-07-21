/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.UUID;

import dev.soloprogramming.solocooking.common.dto.PageResponse;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.UpdateRecipeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
final class RecipeController implements RecipeApi {

    private final RecipeFacade recipeFacade;

    @Override
    public RecipeDTO create(CreateRecipeRequest createRecipeRequest) {
        return recipeFacade.createRecipe(createRecipeRequest);
    }

    @Override
    public RecipeDTO update(UUID recipeId, UpdateRecipeRequest updateRecipeRequest) {
        return recipeFacade.updateRecipe(recipeId, updateRecipeRequest);
    }

    @Override
    public PageResponse<RecipeSummaryDTO> getRecipes(Pageable pageable) {
        return PageResponse.from(recipeFacade.getRecipes(pageable));
    }

    @Override
    public RecipeDTO getRecipe(UUID recipeId) {
        return recipeFacade.findById(recipeId);
    }

    @Override
    public void deleteById(UUID recipeId) {
        recipeFacade.deleteById(recipeId);
    }
}
