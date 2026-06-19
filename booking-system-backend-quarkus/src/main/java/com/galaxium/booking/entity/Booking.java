package com.galaxium.booking.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Booking entity representing a flight reservation.
 * Uses Panache Active Record pattern for simplified data access.
 * 
 * Stores add-ons as JSON in the database for flexibility.
 */
@Entity
@Table(name = "bookings")
public class Booking extends PanacheEntity {

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be positive")
    @Column(name = "user_id", nullable = false)
    public Long userId;

    @NotNull(message = "Flight ID is required")
    @Min(value = 1, message = "Flight ID must be positive")
    @Column(name = "flight_id", nullable = false)
    public Long flightId;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public BookingStatus status;

    @NotBlank(message = "Booking time is required")
    @Column(name = "booking_time", nullable = false)
    public String bookingTime;  // ISO 8601 timestamp

    @NotNull(message = "Seat class is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_class", nullable = false)
    public SeatClass seatClass;

    @NotNull(message = "Price paid is required")
    @Min(value = 0, message = "Price paid must be non-negative")
    @Column(name = "price_paid", nullable = false)
    public Integer pricePaid;

    /**
     * Add-ons stored as JSON.
     * Uses Hibernate's JSON type support for PostgreSQL.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(/*columnDefinition = "jsonb"*/)
    public String addons;

    /**
     * Find all bookings for a specific user.
     */
    public static java.util.List<Booking> findByUserId(Long userId) {
        return find("userId", userId).list();
    }

    /**
     * Find all bookings for a specific flight.
     */
    public static java.util.List<Booking> findByFlightId(Long flightId) {
        return find("flightId", flightId).list();
    }

    /**
     * Find bookings by status.
     */
    public static java.util.List<Booking> findByStatus(BookingStatus status) {
        return find("status", status).list();
    }

    /**
     * Find active (non-cancelled) bookings for a user.
     */
    public static java.util.List<Booking> findActiveByUserId(Long userId) {
        return find("userId = ?1 AND status != ?2", userId, BookingStatus.CANCELLED).list();
    }

    /**
     * Check if booking can be cancelled.
     */
    public boolean canBeCancelled() {
        return status != BookingStatus.CANCELLED && status != BookingStatus.COMPLETED;
    }

    /**
     * Cancel this booking.
     */
    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Booking cannot be cancelled in status: " + status);
        }
        status = BookingStatus.CANCELLED;
    }
}

// Made with Bob
