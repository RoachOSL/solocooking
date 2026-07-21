/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.UUID;

import dev.soloprogramming.solocooking.common.dto.PageResponse;
import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import dev.soloprogramming.solocooking.ingredient.model.request.UpdateIngredientRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
final class IngredientController implements IngredientApi {

    private final IngredientFacade ingredientFacade;

    @Override
    public IngredientDTO create(CreateIngredientRequest createIngredientRequest) {
        return ingredientFacade.createIngredient(createIngredientRequest);
    }

    @Override
    public IngredientDTO update(UUID ingredientId, UpdateIngredientRequest updateIngredientRequest) {
        return ingredientFacade.updateIngredient(ingredientId, updateIngredientRequest);
    }

    @Override
    public void deleteById(UUID ingredientId) {
        ingredientFacade.deleteById(ingredientId);
    }

    @Override
    public PageResponse<IngredientDTO> getIngredients(Pageable pageable) {
        return PageResponse.from(ingredientFacade.getIngredients(pageable));
    }

    @Override
    public PageResponse<IngredientDTO> searchIngredients(String name, Pageable pageable) {
        return PageResponse.from(ingredientFacade.searchIngredients(name, pageable));
    }

    @Override
    public IngredientDTO getIngredient(UUID ingredientId) {
        return ingredientFacade.findById(ingredientId);
    }
}
