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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe_section")
class RecipeSectionEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Setter
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private RecipeEntity recipe;

    @Setter
    @Basic(optional = false)
    @Column(nullable = false, length = 255)
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    private Integer position;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<RecipeIngredientEntity> ingredients = new ArrayList<>();

    List<RecipeIngredientEntity> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    void replaceIngredients(List<RecipeIngredientEntity> newIngredients) {
        var ingredientsToAdd = List.copyOf(newIngredients);
        ingredients.clear();

        for (var position = 0; position < ingredientsToAdd.size(); position++) {
            var ingredient = ingredientsToAdd.get(position);
            ingredient.placeInSection(this, position);
            ingredients.add(ingredient);
        }
    }

    void placeInRecipe(RecipeEntity recipe, int position) {
        this.recipe = recipe;
        this.position = position;
    }
}
