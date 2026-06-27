/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IngredientAlreadyExistsException extends ResponseStatusException {

    private static final String INGREDIENT_ALREADY_EXISTS_MESSAGE =
            "Ingredient with normalized name [%s] already exists.";

    private IngredientAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public static IngredientAlreadyExistsException byNormalizedName(String normalizedName) {
        return new IngredientAlreadyExistsException(INGREDIENT_ALREADY_EXISTS_MESSAGE.formatted(normalizedName));
    }
}
