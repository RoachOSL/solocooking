/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.exception.IngredientAlreadyExistsException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
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
class IngredientService implements IngredientFacade {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Override
    @Transactional
    public IngredientDTO createIngredient(CreateIngredientRequest createIngredientRequest) {
        log.debug("Creating ingredient [{}]", createIngredientRequest);
        var ingredientEntity = ingredientMapper.fromRequest(createIngredientRequest);
        if (ingredientRepository.existsByName(ingredientEntity.getName())) {
            throw IngredientAlreadyExistsException.byName(ingredientEntity.getName());
        }

        return ingredientMapper.toDto(ingredientRepository.save(ingredientEntity));
    }

    @Override
    public Page<IngredientDTO> getIngredients(Pageable pageable) {
        return ingredientRepository.findAll(pageable).map(ingredientMapper::toDto);
    }

    @Override
    public IngredientDTO findById(UUID ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .map(ingredientMapper::toDto)
                .orElseThrow(() -> IngredientNotFoundException.byIngredientId(ingredientId));
    }

    @Override
    public void validateIngredientsExist(Set<UUID> ingredientIds) {
        log.debug("Validating ingredient ids [{}]", ingredientIds);
        if (ingredientIds.isEmpty()) {
            return;
        }

        var missingIngredientIds = new HashSet<>(ingredientIds);
        var foundIngredientIds = ingredientRepository.findAllByIdIn(ingredientIds).stream()
                .map(IngredientEntity::getId)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

        missingIngredientIds.removeAll(foundIngredientIds);
        if (!missingIngredientIds.isEmpty()) {
            log.debug("Missing ingredient ids [{}]", missingIngredientIds);
            throw IngredientNotFoundException.byIngredientIds(missingIngredientIds);
        }
    }
}
