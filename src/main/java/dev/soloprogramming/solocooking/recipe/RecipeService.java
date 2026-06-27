/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.IngredientFacade;
import dev.soloprogramming.solocooking.recipe.exception.RecipeNotFoundException;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class RecipeService implements RecipeFacade {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final RecipeFactory recipeFactory;
    private final IngredientFacade ingredientFacade;

    @Override
    @Transactional
    public RecipeDTO createRecipe(CreateRecipeRequest createRecipeRequest) {
        log.info("Creating recipe [{}]", createRecipeRequest);
        ingredientFacade.validateIngredientsExist(createRecipeRequest.ingredientIds());
        var recipeEntity = recipeFactory.from(createRecipeRequest);

        return recipeMapper.toDto(recipeRepository.save(recipeEntity));
    }

    @Override
    public Page<RecipeSummaryDTO> getRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(recipeMapper::toSummaryDto);
    }

    @Override
    public RecipeDTO findById(UUID recipeId) {
        return recipeRepository.findById(recipeId)
                .map(recipeMapper::toDto)
                .orElseThrow(() -> RecipeNotFoundException.byRecipeId(recipeId));
    }

    @Override
    @Transactional
    public void deleteById(UUID recipeID) {
        var recipe = recipeRepository.findById(recipeID)
                .orElseThrow(() -> RecipeNotFoundException.byRecipeId(recipeID));

        recipeRepository.delete(recipe);
    }
}
