package dev.soloprogramming.solocooking.recipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class RecipeDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Pattern(
            regexp = "^(https?://).*$",
            message = "Must be a valid URL starting with http:// or https://"
    )
    @Size(max = 2048)
    private String imageUrl;

    @Size(max = 5000)
    private String description;
}
