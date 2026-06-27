/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient.exception;

import java.util.Collection;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IngredientNotFoundException extends ResponseStatusException {

    private static final String INGREDIENT_NOT_FOUND_MESSAGE = "Ingredient with id [%s] not found.";
    private static final String INGREDIENTS_NOT_FOUND_MESSAGE = "Ingredients with ids [%s] not found.";

    private IngredientNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public static IngredientNotFoundException byIngredientId(UUID ingredientId) {
        return new IngredientNotFoundException(INGREDIENT_NOT_FOUND_MESSAGE.formatted(ingredientId));
    }

    public static IngredientNotFoundException byIngredientIds(Collection<UUID> ingredientIds) {
        return new IngredientNotFoundException(INGREDIENTS_NOT_FOUND_MESSAGE.formatted(ingredientIds));
    }
}
