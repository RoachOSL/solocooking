package dev.soloprogramming.solocooking.services;

import dev.soloprogramming.solocooking.dto.RecipeDTO;
import dev.soloprogramming.solocooking.entities.Recipe;
import dev.soloprogramming.solocooking.mappers.RecipeMapper;
import dev.soloprogramming.solocooking.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public Recipe createRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = recipeMapper.fromDto(recipeDTO);
        return recipeRepository.save(recipe);
    }

    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }
}
