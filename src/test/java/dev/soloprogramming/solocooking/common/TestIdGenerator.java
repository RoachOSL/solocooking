/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

public final class TestIdGenerator {

    private int sequence;

    public UUID nextId(UUID firstId, String prefix, Set<UUID> usedIds) {
        if (!usedIds.contains(firstId)) {
            return firstId;
        }

        UUID id;
        do {
            id = UUID.nameUUIDFromBytes("%s-%d".formatted(prefix, sequence++).getBytes(StandardCharsets.UTF_8));
        } while (usedIds.contains(id));

        return id;
    }
}
