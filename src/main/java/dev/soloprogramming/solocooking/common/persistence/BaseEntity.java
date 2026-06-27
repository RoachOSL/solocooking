/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.persistence;

import java.time.Instant;

import jakarta.persistence.Basic;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

    @UpdateTimestamp
    @Basic(optional = false)
    private Instant updatedAt;

    @CreationTimestamp
    @Basic(optional = false)
    private Instant createdAt;
}
