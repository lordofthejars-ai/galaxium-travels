package com.galaxium.booking.client.dto;

import java.time.Instant;

/**
 * DTO for Quote from Java Hold Service.
 */
public class QuoteDto {
    public String quoteId;
    public Long flightId;
    public Long travelerId;
    public String travelerName;
    public String seatClass;
    public Integer basePrice;
    public Integer totalPrice;
    public String status;
    public Instant createdAt;
    public Instant expiresAt;
}

// Made with Bob
