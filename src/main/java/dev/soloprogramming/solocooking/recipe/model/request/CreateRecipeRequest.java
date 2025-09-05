package dev.soloprogramming.solocooking.recipe.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateRecipeRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        @Pattern(
                regexp = "^(https?://).*$",
                message = "Must be a valid URL starting with http:// or https://"
        )
        @Size(max = 2048)
        String imageUrl,

        @NotBlank
        @Size(max = 5000)
        String description,

        @NotBlank
        String ingredients
) {
}
