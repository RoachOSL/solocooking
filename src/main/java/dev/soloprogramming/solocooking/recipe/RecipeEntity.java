package dev.soloprogramming.solocooking.recipe;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Table(name = "recipe")
@Entity
class RecipeEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;
    private String imageUrl;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeEntity recipeEntity = (RecipeEntity) o;
        return Objects.equals(id, recipeEntity.id) && Objects.equals(name, recipeEntity.name) && Objects.equals(imageUrl, recipeEntity.imageUrl) && Objects.equals(description, recipeEntity.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageUrl, description);
    }
}
