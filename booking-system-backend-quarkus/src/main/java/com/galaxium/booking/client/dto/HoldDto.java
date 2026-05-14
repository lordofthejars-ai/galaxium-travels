package com.galaxium.booking.client.dto;

import java.time.Instant;

/**
 * DTO for Hold from Java Hold Service.
 */
public class HoldDto {
    public String holdId;
    public String quoteId;
    public Long flightId;
    public Long travelerId;
    public String travelerName;
    public String seatClass;
    public Integer totalPrice;
    public String status;
    public Instant createdAt;
    public Instant expiresAt;
    public Instant confirmedAt;
    public Instant releasedAt;
}

// Made with Bob
