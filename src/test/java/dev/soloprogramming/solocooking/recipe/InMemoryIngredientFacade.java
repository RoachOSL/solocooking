/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.IngredientFacade;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

final class InMemoryIngredientFacade implements IngredientFacade {

    private final Set<UUID> existingIngredientIds = new HashSet<>(Set.of(RecipeTestConstants.INGREDIENT_ID));

    @Override
    public IngredientDTO createIngredient(CreateIngredientRequest createIngredientRequest) {
        throw unsupported("create ingredient");
    }

    @Override
    public Page<IngredientDTO> getIngredients(Pageable pageable) {
        throw unsupported("get ingredients");
    }

    @Override
    public IngredientDTO findById(UUID ingredientId) {
        throw unsupported("find ingredient by id");
    }

    @Override
    public void validateIngredientsExist(Set<UUID> ingredientIds) {
        var missingIngredientIds = new HashSet<>(ingredientIds);
        missingIngredientIds.removeAll(existingIngredientIds);
        if (!missingIngredientIds.isEmpty()) {
            throw IngredientNotFoundException.byIngredientIds(missingIngredientIds);
        }
    }

    private UnsupportedOperationException unsupported(String operation) {
        return new UnsupportedOperationException(
                "Operation [%s] is not implemented by the in-memory ingredient facade".formatted(operation)
        );
    }
}
