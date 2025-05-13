package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.dto.RecipeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/recipes")
@RestController
public class RecipeController {

    private final RecipeFacade recipeFacade;

    @Operation(
            summary = "Create a new recipe",
            description = "Creates a new recipe from the provided data and returns a Location header pointing to the created resource"
    )
    @ApiResponse(responseCode = "201", description = "Recipe created")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeRequest recipeRequest) {
        RecipeDTO created = recipeFacade.createRecipe(recipeRequest);
        URI location = URI.create("/recipes/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @Operation(
            summary = "Get all recipes",
            description = "Returns a list of all available recipes"
    )
    @ApiResponse(responseCode = "200", description = "Recipe returned")
    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        List<RecipeDTO> recipeDTOList = recipeFacade.getAllRecipes();
        return ResponseEntity.ok(recipeDTOList);
    }
}
