package dev.soloprogramming.solocooking.controllers;

import dev.soloprogramming.solocooking.dto.RecipeDTO;
import dev.soloprogramming.solocooking.entities.Recipe;
import dev.soloprogramming.solocooking.services.RecipeService;
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
@RequestMapping("/recipe")
@RestController
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping()
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeDTO dto) {
        Recipe recipe = recipeService.createRecipe(dto);
        URI location = URI.create("/recipes/" + recipe.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        List<RecipeDTO> recipeDTOList = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipeDTOList);
    }
}
