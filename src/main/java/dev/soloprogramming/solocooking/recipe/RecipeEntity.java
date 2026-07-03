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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe")
class RecipeEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Setter
    private UUID id;

    @Setter
    @Basic(optional = false)
    private String name;

    @Setter
    @Basic(optional = false)
    private String imageUrl;

    @Setter
    private String description;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeSectionEntity> sections = new ArrayList<>();

    List<RecipeSectionEntity> getSections() {
        return Collections.unmodifiableList(sections);
    }

    void replaceSections(List<RecipeSectionEntity> newSections) {
        sections.clear();

        for (var position = 0; position < newSections.size(); position++) {
            var section = newSections.get(position);
            section.placeInRecipe(this, position);
            sections.add(section);
        }
    }
}
