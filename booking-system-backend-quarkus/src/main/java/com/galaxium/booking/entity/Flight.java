package com.galaxium.booking.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Flight entity representing an available flight route.
 * Uses Panache Active Record pattern for simplified data access.
 * 
 * Tracks seat availability separately for three classes:
 * - Economy (60% of total seats, 1x base price)
 * - Business (30% of total seats, 2.5x base price)
 * - Galaxium (10% of total seats, 5x base price)
 */
@Entity
@Table(name = "flights")
public class Flight extends PanacheEntity {

    @NotBlank(message = "Origin is required")
    @Column(nullable = false)
    public String origin;

    @NotBlank(message = "Destination is required")
    @Column(nullable = false)
    public String destination;

    @NotBlank(message = "Departure time is required")
    @Column(name = "departure_time", nullable = false)
    public String departureTime;  // ISO 8601 format or "YYYY-MM-DD HH:MM"

    @NotBlank(message = "Arrival time is required")
    @Column(name = "arrival_time", nullable = false)
    public String arrivalTime;    // ISO 8601 format or "YYYY-MM-DD HH:MM"

    @NotNull(message = "Base price is required")
    @Min(value = 0, message = "Base price must be non-negative")
    @Column(name = "base_price", nullable = false)
    public Integer basePrice;     // Economy price (1x multiplier)

    @NotNull(message = "Economy seats available is required")
    @Min(value = 0, message = "Economy seats must be non-negative")
    @Column(name = "economy_seats_available", nullable = false)
    public Integer economySeatsAvailable;

    @NotNull(message = "Business seats available is required")
    @Min(value = 0, message = "Business seats must be non-negative")
    @Column(name = "business_seats_available", nullable = false)
    public Integer businessSeatsAvailable;

    @NotNull(message = "Galaxium seats available is required")
    @Min(value = 0, message = "Galaxium seats must be non-negative")
    @Column(name = "galaxium_seats_available", nullable = false)
    public Integer galaxiumSeatsAvailable;

    /**
     * Get the number of available seats for a specific seat class.
     */
    public int getSeatsAvailable(SeatClass seatClass) {
        return switch (seatClass) {
            case ECONOMY -> economySeatsAvailable;
            case BUSINESS -> businessSeatsAvailable;
            case GALAXIUM -> galaxiumSeatsAvailable;
        };
    }

    /**
     * Decrement the seat counter for a specific class.
     * @return true if successful, false if no seats available
     */
    public boolean decrementSeats(SeatClass seatClass) {
        return switch (seatClass) {
            case ECONOMY -> {
                if (economySeatsAvailable > 0) {
                    economySeatsAvailable--;
                    yield true;
                }
                yield false;
            }
            case BUSINESS -> {
                if (businessSeatsAvailable > 0) {
                    businessSeatsAvailable--;
                    yield true;
                }
                yield false;
            }
            case GALAXIUM -> {
                if (galaxiumSeatsAvailable > 0) {
                    galaxiumSeatsAvailable--;
                    yield true;
                }
                yield false;
            }
        };
    }

    /**
     * Increment the seat counter for a specific class (used when cancelling).
     */
    public void incrementSeats(SeatClass seatClass) {
        switch (seatClass) {
            case ECONOMY -> economySeatsAvailable++;
            case BUSINESS -> businessSeatsAvailable++;
            case GALAXIUM -> galaxiumSeatsAvailable++;
        }
    }

    /**
     * Calculate the price for a specific seat class.
     */
    public int calculatePrice(SeatClass seatClass) {
        return (int) (basePrice * seatClass.getMultiplier());
    }

    /**
     * Get total seats available across all classes.
     */
    public int getTotalSeatsAvailable() {
        return economySeatsAvailable + businessSeatsAvailable + galaxiumSeatsAvailable;
    }

    /**
     * Find flights by origin (case-insensitive partial match).
     */
    public static java.util.List<Flight> findByOrigin(String origin) {
        return find("LOWER(origin) LIKE LOWER(?1)", "%" + origin + "%").list();
    }

    /**
     * Find flights by destination (case-insensitive partial match).
     */
    public static java.util.List<Flight> findByDestination(String destination) {
        return find("LOWER(destination) LIKE LOWER(?1)", "%" + destination + "%").list();
    }

    /**
     * Find flights with available seats in a specific class.
     */
    public static java.util.List<Flight> findWithSeatsAvailable(SeatClass seatClass) {
        return switch (seatClass) {
            case ECONOMY -> find("economySeatsAvailable > 0").list();
            case BUSINESS -> find("businessSeatsAvailable > 0").list();
            case GALAXIUM -> find("galaxiumSeatsAvailable > 0").list();
        };
    }
}

// Made with Bob
