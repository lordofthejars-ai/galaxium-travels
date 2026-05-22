package com.galaxium.holdservice.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @Column(name = "quote_id", nullable = false, length = 50)
    public String quoteId;

    @Column(name = "flight_id", nullable = false)
    public Integer flightId;

    @Column(name = "seat_class", nullable = false, length = 50)
    public String seatClass;

    @Column(name = "quantity", nullable = false)
    public Integer quantity;

    @Column(name = "traveler_id", nullable = false)
    public Integer travelerId;

    @Column(name = "traveler_name", nullable = false, length = 255)
    public String travelerName;

    @Column(name = "price_per_seat", nullable = false)
    public Long pricePerSeat;

    @Column(name = "total_price", nullable = false)
    public Long totalPrice;

    @Column(name = "expires_at", nullable = false)
    public Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    public QuoteStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public enum QuoteStatus {
        CREATED
    }
}

// Made with Bob
