package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.dto.RecipeDTO;
import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping()
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeDTO dto) {
        RecipeDTO created = recipeFacade.createRecipe(dto);
        URI location = URI.create("/recipes/" + created.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(
            summary = "Get all recipes",
            description = "Returns a list of all available recipes"
    )
    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        List<RecipeDTO> recipeDTOList = recipeFacade.getAllRecipes();
        return ResponseEntity.ok(recipeDTOList);
    }
}
