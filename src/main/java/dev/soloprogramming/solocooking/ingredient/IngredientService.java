/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dev.soloprogramming.solocooking.ingredient.exception.IngredientAlreadyExistsException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientInUseException;
import dev.soloprogramming.solocooking.ingredient.exception.IngredientNotFoundException;
import dev.soloprogramming.solocooking.ingredient.model.dto.IngredientDTO;
import dev.soloprogramming.solocooking.ingredient.model.request.CreateIngredientRequest;
import dev.soloprogramming.solocooking.ingredient.model.request.UpdateIngredientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class IngredientService implements IngredientFacade {

    private static final String RECIPE_INGREDIENT_INGREDIENT_FK = "fk_recipe_ingredient_ingredient";

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Override
    @Transactional
    public IngredientDTO createIngredient(CreateIngredientRequest createIngredientRequest) {
        log.debug("Creating ingredient [{}]", createIngredientRequest);
        var ingredientEntity = ingredientMapper.fromRequest(createIngredientRequest);
        if (ingredientRepository.existsByName(ingredientEntity.getName())) {
            log.debug("Ingredient with name [{}] already exists", ingredientEntity.getName());
            throw IngredientAlreadyExistsException.byName(ingredientEntity.getName());
        }

        var savedIngredient = ingredientRepository.save(ingredientEntity);
        log.debug("Created ingredient with id [{}] and name [{}]", savedIngredient.getId(), savedIngredient.getName());
        return ingredientMapper.toDto(savedIngredient);
    }

    @Override
    @Transactional
    public IngredientDTO updateIngredient(UUID ingredientId, UpdateIngredientRequest updateIngredientRequest) {
        log.debug("Updating ingredient with id [{}] using request [{}]", ingredientId, updateIngredientRequest);
        var ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> IngredientNotFoundException.byIngredientId(ingredientId));
        if (updateIngredientRequest.name() == null) {
            log.debug("Ingredient name omitted for id [{}], leaving it unchanged", ingredientId);
            return ingredientMapper.toDto(ingredient);
        }

        var normalizedName = ingredientMapper.normalize(updateIngredientRequest.name());
        if (!normalizedName.equals(ingredient.getName()) && ingredientRepository.existsByName(normalizedName)) {
            log.debug("Ingredient with name [{}] already exists", normalizedName);
            throw IngredientAlreadyExistsException.byName(normalizedName);
        }

        ingredient.setName(normalizedName);
        var savedIngredient = ingredientRepository.save(ingredient);
        log.debug("Updated ingredient with id [{}] and name [{}]", ingredientId, normalizedName);
        return ingredientMapper.toDto(savedIngredient);
    }

    @Override
    @Transactional
    public void deleteById(UUID ingredientId) {
        log.debug("Deleting ingredient with id [{}]", ingredientId);
        var ingredient = ingredientRepository.findById(ingredientId);
        if (ingredient.isEmpty()) {
            log.debug("Ingredient with id [{}] already absent", ingredientId);
            return;
        }

        try {
            ingredientRepository.delete(ingredient.get());
            ingredientRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            if (hasConstraint(exception, RECIPE_INGREDIENT_INGREDIENT_FK)) {
                throw IngredientInUseException.byIngredientId(ingredientId);
            }
            throw exception;
        }
        log.debug("Deleted ingredient with id [{}]", ingredientId);
    }

    @Override
    public Page<IngredientDTO> getIngredients(Pageable pageable) {
        log.debug("Getting ingredients page [{}]", pageable);
        var ingredients = ingredientRepository.findAll(pageable).map(ingredientMapper::toDto);
        log.debug("Returned ingredients page with [{}] elements", ingredients.getNumberOfElements());
        return ingredients;
    }

    @Override
    public Page<IngredientDTO> searchIngredients(String name, Pageable pageable) {
        var normalizedName = ingredientMapper.normalize(name);
        log.debug("Searching ingredients page [{}] by normalized name [{}]", pageable, normalizedName);
        var ingredients = ingredientRepository.findAllByNameContaining(normalizedName, pageable)
                .map(ingredientMapper::toDto);
        log.debug("Found ingredients page with [{}] elements", ingredients.getNumberOfElements());
        return ingredients;
    }

    @Override
    public IngredientDTO findById(UUID ingredientId) {
        log.debug("Finding ingredient by id [{}]", ingredientId);
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
        log.debug("All ingredient ids exist [{}]", ingredientIds);
    }

    private boolean hasConstraint(Throwable throwable, String constraintName) {
        for (var cause = throwable; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException constraintViolation
                    && constraintName.equals(constraintViolation.getConstraintName())) {
                return true;
            }
        }
        return false;
    }
}
