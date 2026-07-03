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
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe_ingredient")
class RecipeIngredientEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Setter
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private RecipeSectionEntity section;

    @Setter
    @Basic(optional = false)
    @Column(name = "ingredient_id")
    private UUID ingredientId;

    @Setter
    @Basic(optional = false)
    private BigDecimal amount;

    @Setter
    @Basic(optional = false)
    private String unit;

    @Setter
    private String note;

    @Basic(optional = false)
    private Integer position;

    void placeInSection(RecipeSectionEntity section, int position) {
        this.section = section;
        this.position = position;
    }
}
