/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestIdGenerator {

    public static UUID nextId(UUID firstId, String prefix, Set<UUID> usedIds) {
        if (!usedIds.contains(firstId)) {
            return firstId;
        }

        var sequence = 0;
        UUID id;
        do {
            id = UUID.nameUUIDFromBytes("%s-%d".formatted(prefix, sequence++).getBytes(StandardCharsets.UTF_8));
        } while (usedIds.contains(id));

        return id;
    }
}
