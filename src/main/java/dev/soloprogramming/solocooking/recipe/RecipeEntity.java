package dev.soloprogramming.solocooking.recipe;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "recipe")
class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    private String imageUrl;

    private String description;

    // TODO -> It should probably be some list maybe or enum dunno think trough
    private String ingredients;

    @CreationTimestamp
    @Basic(optional = false)
    private Instant updatedAt;

    @UpdateTimestamp
    @Basic(optional = false)
    private Instant createdAt;
}
