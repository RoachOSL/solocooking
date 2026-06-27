/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.UUID;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.dto.RecipeSummaryDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
@Tag(name = "recipe-controller", description = "Endpoints for recipe management.")
final class RecipeController {

    private final RecipeFacade recipeFacade;

    @Operation(
            summary = "Creates new recipe"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RecipeDTO create(@Valid @RequestBody CreateRecipeRequest createRecipeRequest) {
        return recipeFacade.createRecipe(createRecipeRequest);
    }

    @Operation(
            summary = "Get all recipes",
            description = "Returns a paginated list of all available recipes"
    )
    @GetMapping
    Page<RecipeSummaryDTO> getRecipes(@ParameterObject Pageable pageable) {
        return recipeFacade.getRecipes(pageable);
    }

    @Operation(
            summary = "Get recipe by id",
            description = "Returns a recipe for given id"
    )
    @GetMapping("/{recipeId}")
    RecipeDTO getRecipe(@PathVariable UUID recipeId) {
        return recipeFacade.findById(recipeId);
    }

    @Operation(summary = "Delete recipe by id")
    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable UUID recipeId) {
        recipeFacade.deleteById(recipeId);
    }

//    @Operation(summary = "Partially update recipe")
//    @PatchMapping("/recipeId")
//    RecipeDTO updateRecipe(@PathVariable UUID recipeId, @RequestBody UpdateRecipeRequest updateRecipeRequest) {
//        return recipeFacade.update(recipeId, updateRecipeRequest);
//    }
}
