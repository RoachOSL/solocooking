/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.persistence.BaseEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe_section")
class RecipeSectionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private RecipeEntity recipe;

    @Setter
    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    private Integer position;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredientEntity> ingredients = new ArrayList<>();

    List<RecipeIngredientEntity> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    void replaceIngredients(List<RecipeIngredientEntity> newIngredients) {
        ingredients.clear();

        for (var position = 0; position < newIngredients.size(); position++) {
            var ingredient = newIngredients.get(position);
            ingredient.placeInSection(this, position);
            ingredients.add(ingredient);
        }
    }

    void placeInRecipe(RecipeEntity recipe, int position) {
        this.recipe = recipe;
        this.position = position;
    }
}
