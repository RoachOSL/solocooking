package dev.soloprogramming.solocooking.recipe;

import dev.soloprogramming.solocooking.recipe.model.dto.RecipeDTO;
import dev.soloprogramming.solocooking.recipe.model.request.CreateRecipeRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class RecipeService implements RecipeFacade {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    @Override
    @Transactional
    public RecipeDTO createRecipe(CreateRecipeRequest createRecipeRequest) {
        log.info("Creating recipe [{}]", createRecipeRequest);
        var recipeEntity = recipeMapper.fromRequest(createRecipeRequest);

        return recipeMapper.toDto(recipeRepository.save(recipeEntity));
    }

    public Page<RecipeDTO> getRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(recipeMapper::toDto);
    }

    @Override
    public RecipeDTO findById(UUID recipeId) {
        return null;
    }

    @Override
    public void deleteById(UUID recipeID) {

    }
}
