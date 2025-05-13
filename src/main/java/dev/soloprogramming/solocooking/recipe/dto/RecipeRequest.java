package dev.soloprogramming.solocooking.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Pattern(
            regexp = "^(https?://).*$",
            message = "Must be a valid URL starting with http:// or https://"
    )
    @Size(max = 2048)
    private String imageUrl;

    @NotBlank
    @Size(max = 5000)
    private String description;

    @NotBlank
    private String ingredients;
}
