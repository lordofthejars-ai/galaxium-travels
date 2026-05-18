package com.galaxium.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galaxium.booking.entity.Flight;
import com.galaxium.booking.entity.SeatClass;

/**
 * Flight data transfer object with computed prices for all seat classes.
 * Matches Python backend's FlightOut schema.
 */
public class FlightDto {
    @JsonProperty("flight_id")
    public Long flightId;
    
    public String origin;
    public String destination;
    
    @JsonProperty("departure_time")
    public String departureTime;
    
    @JsonProperty("arrival_time")
    public String arrivalTime;
    
    @JsonProperty("base_price")
    public Integer basePrice;
    
    @JsonProperty("economy_seats_available")
    public Integer economySeatsAvailable;
    
    @JsonProperty("business_seats_available")
    public Integer businessSeatsAvailable;
    
    @JsonProperty("galaxium_seats_available")
    public Integer galaxiumSeatsAvailable;
    
    // Computed prices for all classes
    @JsonProperty("economy_price")
    public Integer economyPrice;
    
    @JsonProperty("business_price")
    public Integer businessPrice;
    
    @JsonProperty("galaxium_price")
    public Integer galaxiumPrice;

    public FlightDto() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FlightDto{");
        sb.append("flightId=").append(flightId);
        sb.append(", origin='").append(origin).append('\'');
        sb.append(", destination='").append(destination).append('\'');
        sb.append(", departureTime='").append(departureTime).append('\'');
        sb.append(", arrivalTime='").append(arrivalTime).append('\'');
        sb.append(", basePrice=").append(basePrice);
        sb.append(", economySeatsAvailable=").append(economySeatsAvailable);
        sb.append(", businessSeatsAvailable=").append(businessSeatsAvailable);
        sb.append(", galaxiumSeatsAvailable=").append(galaxiumSeatsAvailable);
        sb.append(", economyPrice=").append(economyPrice);
        sb.append(", businessPrice=").append(businessPrice);
        sb.append(", galaxiumPrice=").append(galaxiumPrice);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Create DTO from entity with computed prices.
     */
    public static FlightDto from(Flight flight) {
        FlightDto dto = new FlightDto();
        dto.flightId = flight.id;
        dto.origin = flight.origin;
        dto.destination = flight.destination;
        dto.departureTime = flight.departureTime;
        dto.arrivalTime = flight.arrivalTime;
        dto.basePrice = flight.basePrice;
        dto.economySeatsAvailable = flight.economySeatsAvailable;
        dto.businessSeatsAvailable = flight.businessSeatsAvailable;
        dto.galaxiumSeatsAvailable = flight.galaxiumSeatsAvailable;
        
        // Compute prices for all seat classes
        dto.economyPrice = flight.calculatePrice(SeatClass.ECONOMY);
        dto.businessPrice = flight.calculatePrice(SeatClass.BUSINESS);
        dto.galaxiumPrice = flight.calculatePrice(SeatClass.GALAXIUM);
        
        return dto;
    }
}

// Made with Bob
