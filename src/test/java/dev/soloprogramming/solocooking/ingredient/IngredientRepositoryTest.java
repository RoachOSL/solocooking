/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.ingredient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void shouldGenerateUuidV7Id() {
        // given
        var ingredient = IngredientMother.ingredientEntityWithName(IngredientTestConstants.INGREDIENT_STORED_NAME);

        // when
        var savedIngredient = ingredientRepository.saveAndFlush(ingredient);

        // then
        assertThat(savedIngredient.getId()).isNotNull();
        assertThat(savedIngredient.getId().version()).isEqualTo(7);
    }
}
