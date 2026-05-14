package com.galaxium.booking.entity;

/**
 * Seat class enumeration for flight bookings.
 * Each class has a different price multiplier applied to the base flight price.
 */
public enum SeatClass {
    ECONOMY("economy", 1.0),
    BUSINESS("business", 2.5),
    GALAXIUM("galaxium", 5.0);

    private final String value;
    private final double multiplier;

    SeatClass(String value, double multiplier) {
        this.value = value;
        this.multiplier = multiplier;
    }

    public String getValue() {
        return value;
    }

    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Convert string value to SeatClass enum.
     * Case-insensitive matching.
     */
    public static SeatClass fromString(String value) {
        if (value == null) {
            return ECONOMY; // Default
        }
        for (SeatClass sc : values()) {
            if (sc.value.equalsIgnoreCase(value)) {
                return sc;
            }
        }
        throw new IllegalArgumentException("Invalid seat class: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}

// Made with Bob
