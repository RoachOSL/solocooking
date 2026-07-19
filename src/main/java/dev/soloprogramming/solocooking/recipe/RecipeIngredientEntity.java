/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.recipe;

import java.math.BigDecimal;
import java.util.UUID;

import dev.soloprogramming.solocooking.common.persistence.BaseEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.CheckConstraint;
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
@Table(
        name = "recipe_ingredient",
        check = @CheckConstraint(
                name = "ck_recipe_ingredient_amount_positive",
                constraint = "amount > 0"
        )
)
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
    @Column(name = "ingredient_id", nullable = false)
    private UUID ingredientId;

    @Setter
    @Basic(optional = false)
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal amount;

    @Setter
    @Basic(optional = false)
    @Column(nullable = false, length = 64)
    private String unit;

    @Setter
    @Column(length = 500)
    private String note;

    @Basic(optional = false)
    @Column(nullable = false)
    private Integer position;

    void placeInSection(RecipeSectionEntity section, int position) {
        this.section = section;
        this.position = position;
    }
}
