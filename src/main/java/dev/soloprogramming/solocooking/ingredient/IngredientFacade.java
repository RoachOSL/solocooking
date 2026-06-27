/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.Collection;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface IngredientFacade {

    IngredientDTO createIngredient(@NotNull @Valid CreateIngredientRequest createIngredientRequest);

    Page<IngredientDTO> getIngredients(Pageable pageable);

    IngredientDTO findById(UUID ingredientId);

    void validateExist(Collection<UUID> ingredientIds);
}
