/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IngredientInUseException extends ResponseStatusException {

    private static final URI TYPE = URI.create("urn:solocooking:error:ingredient-in-use");
    private static final String INGREDIENT_IN_USE_MESSAGE =
            "Ingredient cannot be deleted because it is used by a recipe.";

    private IngredientInUseException() {
        super(HttpStatus.CONFLICT, INGREDIENT_IN_USE_MESSAGE);
        getBody().setType(TYPE);
    }

    public static IngredientInUseException forDeletion() {
        return new IngredientInUseException();
    }
}
