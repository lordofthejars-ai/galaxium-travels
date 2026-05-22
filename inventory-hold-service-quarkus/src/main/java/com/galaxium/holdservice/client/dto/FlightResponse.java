package com.galaxium.holdservice.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightResponse {
    
    @JsonProperty("flight_id")
    public Integer flightId;
    
    @JsonProperty("base_price")
    public Integer basePrice;
}

// Made with Bob
