package com.galaxium.holdservice.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingResponse {
    
    @JsonProperty("booking_id")
    public Integer bookingId;
    
    @JsonProperty("user_id")
    public Integer userId;
    
    @JsonProperty("flight_id")
    public Integer flightId;
    
    @JsonProperty("seat_class")
    public String seatClass;
    
    public String status;
}

// Made with Bob
