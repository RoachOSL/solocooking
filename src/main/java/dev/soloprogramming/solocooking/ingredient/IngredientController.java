/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.UUID;

import dev.soloprogramming.solocooking.common.dto.PageResponse;
import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import dev.soloprogramming.solocooking.ingredient.model.request.UpdateIngredientRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
@Tag(name = "ingredient-controller", description = "Endpoints for ingredient catalog management.")
final class IngredientController {

    private final IngredientFacade ingredientFacade;

    @Operation(operationId = "createIngredient", summary = "Creates new ingredient")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ingredient created"),
        @ApiResponse(responseCode = "400", description = "Invalid request",
                     content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Ingredient name already exists",
                     content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    IngredientDTO create(@Valid @RequestBody CreateIngredientRequest createIngredientRequest) {
        return ingredientFacade.createIngredient(createIngredientRequest);
    }

    @Operation(operationId = "updateIngredient", summary = "Updates ingredient")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingredient updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request",
                     content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Ingredient not found",
                     content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Ingredient name already exists",
                     content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{ingredientId}")
    IngredientDTO update(@PathVariable UUID ingredientId,
                         @Valid @RequestBody UpdateIngredientRequest updateIngredientRequest) {
        return ingredientFacade.updateIngredient(ingredientId, updateIngredientRequest);
    }

    @Operation(operationId = "deleteIngredient", summary = "Deletes ingredient",
               description = "Deletes an unused ingredient. Repeated requests return no content.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ingredient deleted or already absent"),
        @ApiResponse(responseCode = "409", description = "Ingredient is used by a recipe",
                     content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{ingredientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable UUID ingredientId) {
        ingredientFacade.deleteById(ingredientId);
    }

    @Operation(operationId = "getIngredients", summary = "Get all ingredients")
    @GetMapping
    PageResponse<IngredientDTO> getIngredients(@ParameterObject Pageable pageable) {
        return PageResponse.from(ingredientFacade.getIngredients(pageable));
    }

    @Operation(operationId = "searchIngredients", summary = "Search ingredients")
    @GetMapping("/search")
    PageResponse<IngredientDTO> searchIngredients(@Parameter(description = "Case-insensitive name fragment")
                                                  @RequestParam @NotBlank @Size(max = 255) String name,
                                                  @ParameterObject Pageable pageable) {
        return PageResponse.from(ingredientFacade.searchIngredients(name, pageable));
    }

    @Operation(operationId = "getIngredient", summary = "Get ingredient by id")
    @GetMapping("/{ingredientId}")
    IngredientDTO getIngredient(@PathVariable UUID ingredientId) {
        return ingredientFacade.findById(ingredientId);
    }
}
