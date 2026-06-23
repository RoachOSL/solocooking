package dev.soloprogramming.solocooking.common;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestConstants {

    public static final UUID RECIPE_ID = UUID.fromString("2b1c02d4-dc91-4f78-a4b3-e342b492bc25");
    public static final String RECIPE_NAME = "Pasta carbonara";
    public static final String RECIPE_IMAGE_URL = "https://example.com/carbonara.jpg";
    public static final String RECIPE_DESCRIPTION = "Classic pasta carbonara";
    public static final String RECIPE_INGREDIENTS = "pasta, eggs, guanciale, pecorino";
    public static final Instant RECIPE_CREATED_AT = Instant.parse("2026-01-10T10:15:30Z");
    public static final Instant RECIPE_UPDATED_AT = Instant.parse("2026-01-11T12:30:00Z");
}
