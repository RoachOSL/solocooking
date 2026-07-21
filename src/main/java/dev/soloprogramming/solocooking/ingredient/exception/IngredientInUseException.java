/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IngredientInUseException extends ResponseStatusException {

    private static final String INGREDIENT_IN_USE_MESSAGE =
            "Ingredient with id [%s] is used by a recipe and cannot be deleted.";

    private IngredientInUseException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public static IngredientInUseException byIngredientId(UUID ingredientId) {
        return new IngredientInUseException(INGREDIENT_IN_USE_MESSAGE.formatted(ingredientId));
    }
}
