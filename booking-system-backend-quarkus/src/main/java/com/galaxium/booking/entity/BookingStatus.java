package com.galaxium.booking.entity;

/**
 * Booking status enumeration.
 */
public enum BookingStatus {
    BOOKED("booked"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Convert string value to BookingStatus enum.
     * Case-insensitive matching.
     * Supports both "cancelled" and "canceled" (American spelling).
     */
    public static BookingStatus fromString(String value) {
        if (value == null) {
            return BOOKED; // Default
        }
        // Handle American spelling
        if ("canceled".equalsIgnoreCase(value)) {
            return CANCELLED;
        }
        for (BookingStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid booking status: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}

// Made with Bob
