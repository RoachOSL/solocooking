/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidRecipeChildIdException extends ResponseStatusException {

    private InvalidRecipeChildIdException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public static InvalidRecipeChildIdException duplicateSectionId(UUID sectionId) {
        return new InvalidRecipeChildIdException("Duplicate recipe section id [%s].".formatted(sectionId));
    }

    public static InvalidRecipeChildIdException invalidSectionId(UUID sectionId) {
        return new InvalidRecipeChildIdException("Recipe section id [%s] does not belong to recipe.".formatted(sectionId));
    }

    public static InvalidRecipeChildIdException duplicateRecipeIngredientId(UUID recipeIngredientId) {
        return new InvalidRecipeChildIdException("Duplicate recipe ingredient id [%s].".formatted(recipeIngredientId));
    }

    public static InvalidRecipeChildIdException invalidRecipeIngredientId(UUID recipeIngredientId) {
        return new InvalidRecipeChildIdException(
                "Recipe ingredient id [%s] does not belong to requested section.".formatted(recipeIngredientId)
        );
    }
}
