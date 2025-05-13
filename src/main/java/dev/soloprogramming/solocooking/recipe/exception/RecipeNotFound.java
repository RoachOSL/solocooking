package dev.soloprogramming.solocooking.recipe.exception;

import dev.soloprogramming.solocooking.common.exception.ErrorDetails;
import org.springframework.http.HttpStatus;

import java.util.UUID;

class RecipeNotFound extends ErrorDetails {
    public RecipeNotFound(UUID recipeId) {
        super("Recipe with ID " + recipeId + " not found", HttpStatus.NOT_FOUND);
    }
}
