package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.dto.RecipeDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
class RecipeService implements RecipeFacade {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        RecipeEntity entity = recipeMapper.fromDto(recipeDTO);
        RecipeEntity saved = recipeRepository.save(entity);
        return recipeMapper.toDto(saved);
    }

    @Override
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }
}
