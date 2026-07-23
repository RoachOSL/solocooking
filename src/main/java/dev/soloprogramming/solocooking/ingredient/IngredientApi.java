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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/ingredients")
@Tag(name = "ingredient-controller", description = "Endpoints for ingredient catalog management.")
interface IngredientApi {

    @Operation(operationId = "createIngredient", summary = "Creates new ingredient")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ingredient created"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    IngredientDTO create(@Valid @RequestBody CreateIngredientRequest createIngredientRequest);

    @Operation(operationId = "updateIngredient", summary = "Updates ingredient")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingredient updated"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict")
    })
    @PatchMapping("/{ingredientId}")
    IngredientDTO update(@PathVariable UUID ingredientId,
                         @Valid @RequestBody UpdateIngredientRequest updateIngredientRequest);

    @Operation(operationId = "deleteIngredient", summary = "Deletes ingredient",
               description = "Deletes an unused ingredient. Repeated requests return no content.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ingredient deleted or already absent"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict")
    })
    @DeleteMapping("/{ingredientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable UUID ingredientId);

    @Operation(operationId = "getIngredients", summary = "Get all ingredients",
               description = "Default sort: name,id ascending. Supported sort properties: id, name.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingredients returned"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest")
    })
    @GetMapping
    PageResponse<IngredientDTO> getIngredients(@ParameterObject Pageable pageable);

    @Operation(operationId = "searchIngredients", summary = "Search ingredients",
               description = "Default sort: name,id ascending. Supported sort properties: id, name.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matching ingredients returned"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest")
    })
    @GetMapping("/search")
    PageResponse<IngredientDTO> searchIngredients(@Parameter(description = "Case-insensitive name fragment")
                                                  @RequestParam @NotBlank @Size(min = 1, max = 255) String name,
                                                  @ParameterObject Pageable pageable);

    @Operation(operationId = "getIngredient", summary = "Get ingredient by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingredient returned"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @GetMapping("/{ingredientId}")
    IngredientDTO getIngredient(@PathVariable UUID ingredientId);
}
