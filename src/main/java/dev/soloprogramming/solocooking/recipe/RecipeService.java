package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.dto.RecipeRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
class RecipeService implements RecipeFacade {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    @Override
    @Transactional
    public RecipeDTO createRecipe(RecipeRequest recipeRequest) {
        var recipeEntity = recipeMapper.fromRequest(recipeRequest);
        recipeEntity.setCreatedAt(Instant.now());
        recipeEntity.setUpdatedAt(Instant.now());

        var savedEntity = recipeRepository.save(recipeEntity);
        return recipeMapper.toDto(savedEntity);
    }

    @Override
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }
}
