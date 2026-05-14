package com.galaxium.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galaxium.booking.entity.SeatClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Request DTO for booking a flight.
 */
public record BookingRequest(
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @JsonProperty("user_id")
    Long userId,
    
    @NotBlank(message = "Name is required")
    String name,
    
    @NotNull(message = "Flight ID is required")
    @Positive(message = "Flight ID must be positive")
    @JsonProperty("flight_id")
    Long flightId,
    
    @JsonProperty("seat_class")
    SeatClass seatClass,
    
    List<AddOnDto> addons
) {
    // Default constructor with economy seat class
    public BookingRequest {
        if (seatClass == null) {
            seatClass = SeatClass.ECONOMY;
        }
    }
}

// Made with Bob
