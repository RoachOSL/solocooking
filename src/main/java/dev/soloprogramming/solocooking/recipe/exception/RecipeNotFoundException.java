/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RecipeNotFoundException extends ResponseStatusException {

    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id [%s] not found.";

    private RecipeNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public static RecipeNotFoundException byRecipeId(UUID recipeId) {
        return new RecipeNotFoundException(RECIPE_NOT_FOUND_MESSAGE.formatted(recipeId));
    }
}
