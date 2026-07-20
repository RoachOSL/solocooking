/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.IngredientFacade;
import dev.soloprogramming.solocooking.recipe.exception.InvalidRecipeChildIdException;
import dev.soloprogramming.solocooking.recipe.exception.RecipeNotFoundException;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.UpdateRecipeIngredientRequest;
import dev.soloprogramming.solocooking.recipe.model.request.UpdateRecipeRequest;
import dev.soloprogramming.solocooking.recipe.model.request.UpdateRecipeSectionRequest;
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
    @Transactional
    public RecipeDTO updateRecipe(UUID recipeId, UpdateRecipeRequest updateRecipeRequest) {
        log.debug("Updating recipe with id [{}]", recipeId);
        var recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> RecipeNotFoundException.byRecipeId(recipeId));
        var ingredientIds = updateRecipeRequest.ingredientIds();
        log.debug("Validating updated recipe ingredient ids [{}]", ingredientIds);
        ingredientFacade.validateIngredientsExist(ingredientIds);
        applyUpdate(recipe, updateRecipeRequest);
        var updatedRecipe = recipeRepository.saveAndFlush(recipe);

        log.debug("Updated recipe with id [{}]", recipeId);
        return recipeMapper.toDto(updatedRecipe);
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

    private void applyUpdate(RecipeEntity recipe, UpdateRecipeRequest request) {
        validateChildIds(recipe, request);

        var sectionsById = sectionsById(recipe);
        recipe.setName(request.name());
        recipe.setImageUrl(request.imageUrl());
        recipe.setDescription(request.description());
        recipe.replaceSections(request.sections().stream()
                .map(sectionRequest -> updateSection(sectionsById, sectionRequest))
                .toList());
    }

    private void validateChildIds(RecipeEntity recipe, UpdateRecipeRequest request) {
        var sectionsById = sectionsById(recipe);
        var recipeIngredientSectionIds = recipeIngredientSectionIds(recipe);
        var requestedSectionIds = new HashSet<UUID>();
        var requestedRecipeIngredientIds = new HashSet<UUID>();

        for (var sectionRequest : request.sections()) {
            validateSectionId(sectionRequest.id(), sectionsById, requestedSectionIds);
            for (var ingredientRequest : sectionRequest.ingredients()) {
                validateRecipeIngredientId(
                        ingredientRequest.id(),
                        sectionRequest.id(),
                        recipeIngredientSectionIds,
                        requestedRecipeIngredientIds
                );
            }
        }
    }

    private void validateSectionId(
            UUID sectionId,
            Map<UUID, RecipeSectionEntity> sectionsById,
            Set<UUID> requestedSectionIds
    ) {
        if (sectionId == null) {
            return;
        }
        if (!requestedSectionIds.add(sectionId)) {
            throw InvalidRecipeChildIdException.duplicateSectionId(sectionId);
        }
        if (!sectionsById.containsKey(sectionId)) {
            throw InvalidRecipeChildIdException.invalidSectionId(sectionId);
        }
    }

    private void validateRecipeIngredientId(
            UUID recipeIngredientId,
            UUID requestedSectionId,
            Map<UUID, UUID> recipeIngredientSectionIds,
            Set<UUID> requestedRecipeIngredientIds
    ) {
        if (recipeIngredientId == null) {
            return;
        }
        if (!requestedRecipeIngredientIds.add(recipeIngredientId)) {
            throw InvalidRecipeChildIdException.duplicateRecipeIngredientId(recipeIngredientId);
        }
        var owningSectionId = recipeIngredientSectionIds.get(recipeIngredientId);
        if (owningSectionId == null || !owningSectionId.equals(requestedSectionId)) {
            throw InvalidRecipeChildIdException.invalidRecipeIngredientId(recipeIngredientId);
        }
    }

    private RecipeSectionEntity updateSection(
            Map<UUID, RecipeSectionEntity> sectionsById,
            UpdateRecipeSectionRequest request
    ) {
        var section = request.id() == null ? new RecipeSectionEntity() : sectionsById.get(request.id());
        var ingredientsById = ingredientsById(section);
        section.setName(request.name());
        section.replaceIngredients(request.ingredients().stream()
                .map(ingredientRequest -> updateIngredient(ingredientsById, ingredientRequest))
                .toList());
        return section;
    }

    private RecipeIngredientEntity updateIngredient(
            Map<UUID, RecipeIngredientEntity> ingredientsById,
            UpdateRecipeIngredientRequest request
    ) {
        var ingredient = request.id() == null ? new RecipeIngredientEntity() : ingredientsById.get(request.id());
        ingredient.setIngredientId(request.ingredientId());
        ingredient.setAmount(request.amount());
        ingredient.setUnit(request.unit());
        ingredient.setNote(request.note());
        return ingredient;
    }

    private Map<UUID, RecipeSectionEntity> sectionsById(RecipeEntity recipe) {
        var sectionsById = new HashMap<UUID, RecipeSectionEntity>();
        recipe.getSections().forEach(section -> sectionsById.put(section.getId(), section));
        return sectionsById;
    }

    private Map<UUID, RecipeIngredientEntity> ingredientsById(RecipeSectionEntity section) {
        var ingredientsById = new HashMap<UUID, RecipeIngredientEntity>();
        section.getIngredients().forEach(ingredient -> ingredientsById.put(ingredient.getId(), ingredient));
        return ingredientsById;
    }

    private Map<UUID, UUID> recipeIngredientSectionIds(RecipeEntity recipe) {
        var recipeIngredientSectionIds = new HashMap<UUID, UUID>();
        recipe.getSections().forEach(section -> section.getIngredients()
                .forEach(ingredient -> recipeIngredientSectionIds.put(ingredient.getId(), section.getId())));
        return recipeIngredientSectionIds;
    }
}
