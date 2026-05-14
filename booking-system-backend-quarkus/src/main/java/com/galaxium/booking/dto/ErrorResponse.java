package com.galaxium.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Standard error response for service layer operations.
 * Matches the Python backend's ErrorResponse pattern.
 */
public class ErrorResponse {
    public boolean success = false;
    public String error;
    
    @JsonProperty("error_code")
    public String errorCode;
    
    public String details;

    public ErrorResponse() {
    }

    public ErrorResponse(String error, String errorCode) {
        this.error = error;
        this.errorCode = errorCode;
    }

    public ErrorResponse(String error, String errorCode, String details) {
        this.error = error;
        this.errorCode = errorCode;
        this.details = details;
    }

    // Common error codes from Python backend
    public static final String INVALID_SEAT_CLASS = "INVALID_SEAT_CLASS";
    public static final String FLIGHT_NOT_FOUND = "FLIGHT_NOT_FOUND";
    public static final String NO_SEATS_AVAILABLE = "NO_SEATS_AVAILABLE";
    public static final String NAME_MISMATCH = "NAME_MISMATCH";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String INVALID_ADDON = "INVALID_ADDON";
    public static final String PRICE_TAMPERING = "PRICE_TAMPERING";
    public static final String BOOKING_NOT_FOUND = "BOOKING_NOT_FOUND";
    public static final String ALREADY_CANCELLED = "ALREADY_CANCELLED";
    public static final String EMAIL_EXISTS = "EMAIL_EXISTS";
    public static final String INVALID_EMAIL = "INVALID_EMAIL";
}

// Made with Bob
