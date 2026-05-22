package com.galaxium.holdservice.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Entity
@Table(name = "holds")
public class Hold {

    @Id
    @Column(name = "hold_id", nullable = false, length = 50)
    public String holdId;

    @Column(name = "quote_id", nullable = false, length = 50)
    public String quoteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    public HoldStatus status;

    @Column(name = "reserved_until", nullable = false)
    public Instant reservedUntil;

    @Column(name = "external_booking_reference", length = 255)
    public String externalBookingReference;

    @Column(name = "error_message", columnDefinition = "TEXT")
    public String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum HoldStatus {
        HELD,
        EXPIRED,
        CONFIRMED,
        RELEASED,
        CONFIRMATION_FAILED
    }
}

// Made with Bob
