package com.galaxium.holdservice.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @UuidGenerator
    @Column(name = "event_id", length = 36)
    public String eventId;

    @Column(name = "entity_type", nullable = false, length = 50)
    public String entityType;

    @Column(name = "entity_id", nullable = false, length = 50)
    public String entityId;

    @Column(name = "event_type", nullable = false, length = 50)
    public String eventType;

    @Column(name = "details", columnDefinition = "TEXT")
    public String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}

// Made with Bob
