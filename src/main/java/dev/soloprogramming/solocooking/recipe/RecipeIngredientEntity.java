/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.math.BigDecimal;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.persistence.BaseEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "recipe_ingredient")
class RecipeIngredientEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private RecipeSectionEntity section;

    @Basic(optional = false)
    @Column(name = "ingredient_id")
    private UUID ingredientId;

    @Basic(optional = false)
    private BigDecimal amount;

    @Basic(optional = false)
    private String unit;

    private String note;

    @Basic(optional = false)
    private Integer sortOrder;
}
