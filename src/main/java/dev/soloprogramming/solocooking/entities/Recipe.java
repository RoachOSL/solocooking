package dev.soloprogramming.solocooking.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Setter
@Getter
@Entity
public class Recipe {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;
    private String imageUrl;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Recipe recipe = (Recipe) o;
        return id != null && id.equals(recipe.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
