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
        log.debug("Creating recipe [{}]", createRecipeRequest);
        var ingredientIds = createRecipeRequest.ingredientIds();
        log.debug("Validating recipe ingredient ids [{}]", ingredientIds);
        ingredientFacade.validateIngredientsExist(ingredientIds);
        var recipeEntity = recipeFactory.from(createRecipeRequest);
        var savedRecipe = recipeRepository.save(recipeEntity);

        log.debug("Created recipe with id [{}]", savedRecipe.getId());
        return recipeMapper.toDto(savedRecipe);
    }

    @Override
    public Page<RecipeSummaryDTO> getRecipes(Pageable pageable) {
        log.debug("Getting recipes page [{}]", pageable);
        var recipes = recipeRepository.findAll(pageable).map(recipeMapper::toSummaryDto);
        log.debug("Returned recipes page with [{}] elements", recipes.getNumberOfElements());
        return recipes;
    }

    @Override
    public RecipeDTO findById(UUID recipeId) {
        log.debug("Finding recipe by id [{}]", recipeId);
        return recipeRepository.findById(recipeId)
                .map(recipe -> {
                    log.debug("Found recipe with id [{}]", recipeId);
                    return recipeMapper.toDto(recipe);
                })
                .orElseThrow(() -> {
                    log.debug("Recipe with id [{}] was not found", recipeId);
                    return RecipeNotFoundException.byRecipeId(recipeId);
                });
    }

    @Override
    @Transactional
    public void deleteById(UUID recipeId) {
        log.debug("Deleting recipe by id [{}]", recipeId);
        recipeRepository.findByIdWithoutDetails(recipeId)
                .ifPresentOrElse(recipe -> {
                    recipeRepository.delete(recipe);
                    log.debug("Deleted recipe with id [{}]", recipeId);
                }, () -> log.debug("Recipe with id [{}] was already absent", recipeId));
    }
}
