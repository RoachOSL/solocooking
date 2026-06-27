/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
@Tag(name = "ingredient-controller", description = "Endpoints for ingredient catalog management.")
final class IngredientController {

    private final IngredientFacade ingredientFacade;

    @Operation(summary = "Creates new ingredient")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    IngredientDTO createIngredient(@Valid @RequestBody CreateIngredientRequest createIngredientRequest) {
        return ingredientFacade.createIngredient(createIngredientRequest);
    }

    @Operation(summary = "Get all ingredients")
    @GetMapping
    Page<IngredientDTO> getIngredients(@ParameterObject Pageable pageable) {
        return ingredientFacade.getIngredients(pageable);
    }

    @Operation(summary = "Get ingredient by id")
    @GetMapping("/{ingredientId}")
    IngredientDTO getIngredient(@PathVariable UUID ingredientId) {
        return ingredientFacade.findById(ingredientId);
    }
}
