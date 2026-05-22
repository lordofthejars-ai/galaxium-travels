package com.galaxium.holdservice.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateQuoteRequest {

    @NotNull(message = "Flight ID is required")
    public Integer flightId;

    @NotBlank(message = "Seat class is required")
    public String seatClass;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    public Integer quantity;

    @NotNull(message = "Traveler ID is required")
    public Integer travelerId;

    @NotBlank(message = "Traveler name is required")
    public String travelerName;
}

// Made with Bob
