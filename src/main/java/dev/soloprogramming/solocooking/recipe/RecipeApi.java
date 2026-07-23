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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/recipes")
@Tag(name = "recipe-controller", description = "Endpoints for recipe management.")
interface RecipeApi {

    @Operation(operationId = "createRecipe", summary = "Creates new recipe")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recipe created"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RecipeDTO create(@Valid @RequestBody CreateRecipeRequest createRecipeRequest);

    @Operation(operationId = "updateRecipe", summary = "Updates entire recipe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recipe updated"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @PutMapping("/{recipeId}")
    RecipeDTO update(@PathVariable UUID recipeId, @Valid @RequestBody UpdateRecipeRequest updateRecipeRequest);

    @Operation(operationId = "getRecipes", summary = "Get all recipes",
               description = "Returns a paginated list of all available recipes. Default sort: name,id ascending. "
                       + "Supported sort properties: id, name, createdAt, updatedAt.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recipes returned"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest")
    })
    @GetMapping
    PageResponse<RecipeSummaryDTO> getRecipes(@ParameterObject Pageable pageable);

    @Operation(operationId = "getRecipe", summary = "Get recipe by id",
               description = "Returns a recipe for given id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recipe returned"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @GetMapping("/{recipeId}")
    RecipeDTO getRecipe(@PathVariable UUID recipeId);

    @Operation(operationId = "deleteRecipe", summary = "Delete recipe by id",
               description = "Deletes the recipe when it exists. Repeated requests return no content.")
    @ApiResponse(responseCode = "204", description = "Recipe deleted or already absent")
    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable UUID recipeId);
}
